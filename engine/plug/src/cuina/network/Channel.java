package cuina.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Channel
{
	/** Gibt an, dass die Verbindung beendet wurde. */
	public static final int FLAG_EOF		= -1;
	/** Gibt an, dass die Nachicht leer ist. */
	public static final int FLAG_EMPTY		= 0;
	/** Gibt an, dass die Nachicht eine Bestätigung zu einer vorhergehenden Nachicht ist. */
	public static final int FLAG_ACK		= 1;
	/** Gibt an, dass die Verbindung beendet werden soll. */
	public static final int FLAG_CLOSE		= 2;
	public static final int FLAG_CMD		= 4;
	public static final int FLAG_EXCEPTION	= 5;
	public static final int FLAG_BYTES		= 6;
	public static final int FLAG_TEXT		= 7;
	

	private int id;
	private Socket socket;
	private InputStream in;
	private OutputStream out;
	
	private Thread messageThread;
//	private Queue<byte[]> messageQueue = new LinkedList<byte[]>();
	private List<ChannelListener> listeners = new ArrayList<ChannelListener>();

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

	public int getID()
	{
		return id;
	}

	public void setID(int id)
	{
		this.id = id;
	}

	public OutputStream getOutputStream()
	{
		return out;
	}
	
	public InputStream getInputStream()
	{
		return in;
	}
	
	public void addChannelListener(ChannelListener listener)
	{
		listeners.add(listener);
		if (messageThread == null)
		{
			messageThread = new MessageThread();
			messageThread.setDaemon(true);
			messageThread.start();
		}
	}

//	@SuppressWarnings("deprecation")
	public void removeChannelListener(ChannelListener listener)
	{
		listeners.remove(listener);
		if (listeners.size() == 0)
		{
//			messageThread.stop(); // FIXME: API zu InterruptableChannel angucken.
			messageThread = null;
		}
	}
	
	void fireMessageRecieved(Message msg)
	{
		for(ChannelListener l : listeners)
		{
			l.messageRecieved(msg);
		}
	}

	public boolean isOpen()
	{
		return !socket.isClosed(); 
	}
	
	public void login(String username, String password) throws IOException
	{
		if (username == null) throw new NullPointerException();
		
		if (password != null)
			send(FLAG_CMD, "login", username, password);
		else
			send(FLAG_CMD, "login", username);
	}
	
//	public void sendData(Map<String, String> data) throws IOException
//	{
//		StringBuilder builder = new StringBuilder(data.size() * 16);
//		for(String key : data.keySet())
//			builder.append(key).append('=').append(data.get(key)).append(';');
//		send(FLAG_DATA, builder.toString().getBytes());
//	}
	
	public void send(Exception e) throws IOException
	{
		send(FLAG_EXCEPTION, e.getClass().getName(), e.getMessage());
	}
	
	public void send(int flag, String command, String... arguments) throws IOException
	{
		StringBuilder builder = new StringBuilder(8 + arguments.length * 8);
		builder.append(command);
		for(int i = 0; i < arguments.length; i++)
			builder.append('|').append(arguments[i]);
		send(flag, builder.toString().getBytes());
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

	public void close()
	{
		try
		{
			socket.close();
		}
		catch (IOException e) { /* Keine Fehlermeldungen beim Schließen. */ }
	}
	
	public Message read() throws IOException
	{
		return StreamUtil.read(in);
	}
	
	public void poll()
	{
		try
		{
			if (in.available() > 0)
			{
				Message msg = read();
				fireMessageRecieved(msg);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	class MessageThread extends Thread
	{
		@Override
		public void run()
		{
			while (!socket.isClosed())
			{
				try
				{
					Message msg = read();
					if (msg != null) fireMessageRecieved(msg);
				}
				catch (IOException e)
				{
					close();
				}
			}
		}
	}
}
