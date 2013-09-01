package cuina.debug;

import java.io.IOException;
import java.io.Writer;

import javax.swing.JTextArea;

public class MessagePage extends JTextArea implements DebugPage
{
	private static final long serialVersionUID = 1L;
	
	private Writer writer;
	
	public MessagePage()
	{
		writer = new MessageWriter();
//		EventExecuter.setEventWriter(writer);
		setEditable(false);
	}
	
	@Override
	public void update()
	{
		try
		{
			writer.flush();
		}
		catch (IOException e) { /* Da kommt nix */ }
	}

	private class MessageWriter extends Writer
	{
		private StringBuilder builder = new StringBuilder();
		
		@Override
		public void write(char[] cbuf, int off, int len) throws IOException
		{
			builder.append(cbuf, off, len);
		}

		@Override
		public void flush() throws IOException
		{
			setText(builder.toString());
//			builder = new StringBuilder();
		}

		@Override
		public void close() throws IOException
		{
			builder = null;
		}
		
	}
}
