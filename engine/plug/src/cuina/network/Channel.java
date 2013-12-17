package cuina.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

class Channel
{
	public static final int FLAG_EMPTY	= 0;
	public static final int FLAG_ACK	= 1;
	public static final int FLAG_CLOSE	= 2;
	public static final int FLAG_LOGIN	= 3;
	public static final int FLAG_CMD	= 4;
	public static final int FLAG_DATA	= 5;
	public static final int FLAG_BYTES	= 6;
	public static final int FLAG_TEXT	= 7;

	private Socket socket;
	private InputStream in;
	private OutputStream out;
	
//	private Thread messageListener;
//	private Queue<byte[]> messageQueue = new LinkedList<byte[]>();

	public Channel()
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

	public OutputStream getOutputStream()
	{
		return out;
	}
	
	public InputStream getInputStream()
	{
		return in;
	}
	
	public boolean isOpen()
	{
		return socket.isConnected(); 
	}
	
	public void login(String username, String password) throws IOException
	{
		StringBuilder builder = new StringBuilder(32);
		builder.append(username).append(':').append(password);
		send(FLAG_LOGIN, builder.toString().getBytes());
	}
	
	public void sendData(Map<String, String> data) throws IOException
	{
		StringBuilder builder = new StringBuilder(data.size() * 16);
		for(String key : data.keySet())
			builder.append(key).append('=').append(data.get(key)).append(';');
		send(FLAG_DATA, builder.toString().getBytes());
	}
	
	public void send(int flag, String data) throws IOException
	{
		send(flag, data.getBytes());
	}
	
	public void send(int flag, byte[] data) throws IOException
	{
		out.write(flag);
		out.write(StreamUtil.intToByteArray(data.length));
		out.write(data);
		out.flush();
	}
	
	public void close() throws IOException
	{
		socket.close();
	}
	
	public Message read()
	{
		try
		{
			return StreamUtil.read(in);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public void poll()
	{
		try
		{
			if (in.available() > 0)
			{
				int flag = in.read();
				switch(flag)
				{
					case FLAG_ACK:
					case FLAG_EMPTY: break;
					case FLAG_CLOSE: close(); break;
					
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
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
