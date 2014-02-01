package cuina.debug;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;

public class MessagePage extends JTextArea implements DebugPage
{
	private static final int BUFFER_SIZE = 1 << 16;
	
	private static final long serialVersionUID = 1L;
	
	private PrintStream writer;
	
	public MessagePage()
	{
		writer = new PrintStream(new MessageWriter());
		System.setOut(writer);
		System.setErr(writer);
		setEditable(false);
	}
	
	@Override
	public void update()
	{
		writer.flush();
	}

	private class MessageWriter extends OutputStream
	{
		private StringBuilder builder = new StringBuilder();

		@Override
		public void write(byte[] b, int off, int len) throws IOException
		{
			builder.append(new String(b, off, len));
			if (builder.length() > BUFFER_SIZE)
				builder.delete(0, builder.length() - BUFFER_SIZE);
		}
		
		@Override
		public void write(int b) throws IOException
		{
			builder.append((char) b);
			if (builder.length() > BUFFER_SIZE)
				builder.delete(0, builder.length() - BUFFER_SIZE);
		}
		
		@Override
		public void flush() throws IOException
		{
			setText(builder.toString());
		}

		@Override
		public void close() throws IOException
		{
			builder = null;
		}
	}
}
