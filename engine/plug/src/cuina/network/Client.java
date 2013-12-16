package cuina.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client
{
	protected int id;
	protected Socket socket;
	private String name;
	protected InputStream in;
	protected OutputStream out;
	
//	private Thread messageListener;
//	private Queue<byte[]> messageQueue = new LinkedList<byte[]>();

	public Client()
	{
		
	}
	
	public void open(Socket socket) throws IOException
	{
		this.socket = socket;
		
		this.in = socket.getInputStream();
		this.out = socket.getOutputStream();
		
//		messageListener = new MessageThread();
//		messageListener.start();
	}

	public int getID()
	{
		return id;
	}

	protected void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public OutputStream getOutputStream()
	{
		return out;
	}
	
	public InputStream getInputStream()
	{
		return in;
	}
	
	public void close() throws IOException
	{
		socket.close();
	}
	
//	class MessageThread extends Thread
//	{
//		private byte[] buffer = new byte[1 << 16];
//		
//		@Override
//		public void run()
//		{
//			try
//			{
//				while (in.available() > 0)
//				{
//					in.read(buffer);
//				}
//			}
//			catch (IOException e)
//			{
//				e.printStackTrace();
//			}
//		}
//	}
}
