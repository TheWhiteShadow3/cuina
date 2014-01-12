package cuina.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Channel
{
	/** Gibt an, dass die Verbindung beendet wurde. */
	public static final int FLAG_EOF		= -1;
	/** Gibt an, dass die Nachicht leer ist. */
	public static final int FLAG_EMPTY		= 0;
	/** Gibt an, dass die Nachicht eine Rückmeldung ist. */
	public static final int FLAG_ACK		= 1;
	/** Gibt an, dass die Verbindung beendet werden soll. */
	public static final int FLAG_INFO		= 2;
	/** Gibt an, dass die Verbindung beendet werden soll. */
	public static final int FLAG_CLOSE		= 3;
	public static final int FLAG_CMD		= 4;
	public static final int FLAG_EXCEPTION	= 5;
	public static final int FLAG_BYTES		= 6;
	public static final int FLAG_TEXT		= 7;
	/** Gibt an, dass eine Netzwerk-ID angefordert wird. */
	public static final int FLAG_NETID		= 8;
	

	private int id;
	private Socket socket;
	private InputStream in;
	private OutputStream out;
	
	private Thread messageThread;
//	private Queue<byte[]> messageQueue = new LinkedList<byte[]>();
	private ChannelListener defaultChannelListener;
	private Map<NetID, ChannelListener> listeners = new HashMap<NetID, ChannelListener>();

	public Channel(ChannelListener defaultChannelListener)
	{
		this.defaultChannelListener = defaultChannelListener;
	}
	
	public void open(Socket socket) throws IOException
	{
		this.socket = socket;
		
		this.in = socket.getInputStream();
		this.out = socket.getOutputStream();
	}
	
	public InetAddress getInetAddress()
	{
		return socket.getInetAddress();
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
	
	public boolean addChannelListener(NetID netID, ChannelListener listener)
	{
		if (!netID.isSet()) return false;
		
		listeners.put(netID, listener);
		if (messageThread == null)
		{
			messageThread = new MessageThread();
			messageThread.setDaemon(true);
			messageThread.start();
		}
		return true;
	}

	public boolean removeChannelListener(NetID netID)
	{
		if (!netID.isSet()) return false;
		
		listeners.remove(netID);
		if (listeners.size() == 0)
		{
//			messageThread.stop(); // TODO: API zu InterruptableChannel angucken.
			messageThread = null;
		}
		return true;
	}
	
	void fireMessageRecieved(Message msg)
	{
		if (msg.getReciever() == 0)
		{
			defaultChannelListener.messageRecieved(msg);
		}
		else
		{
			ChannelListener listener = listeners.get(msg.getReciever());
			if (listener != null)
			{
				listener.messageRecieved(msg);
			}
		}
	}
	
	void fireChannelClosed()
	{
		for(ChannelListener l : listeners.values())
		{
			l.channelClosed();
		}
		defaultChannelListener.channelClosed();
	}

	public boolean isOpen()
	{
		return !socket.isClosed(); 
	}
	
	public void login(String username, String password) throws IOException
	{
		if (username == null) throw new NullPointerException();
		
		if (password != null)
			send(FLAG_CMD, 0, "login", username, password);
		else
			send(FLAG_CMD, 0, "login", username);
	}
	
//	public void sendData(Map<String, String> data) throws IOException
//	{
//		StringBuilder builder = new StringBuilder(data.size() * 16);
//		for(String key : data.keySet())
//			builder.append(key).append('=').append(data.get(key)).append(';');
//		send(FLAG_DATA, builder.toString().getBytes());
//	}
	
	public void send(Message msg) throws IOException
	{
		send(msg.getType(), msg.getReciever(), msg.getData());
	}
	
	public void send(Exception e) throws IOException
	{
		send(FLAG_EXCEPTION, 0, e.getClass().getName(), e.getMessage());
	}
	
	public void send(int flag, int reciever, String command, String... arguments) throws IOException
	{
		StringBuilder builder = new StringBuilder(8 + arguments.length * 8);
		builder.append(command);
		for(int i = 0; i < arguments.length; i++)
			builder.append('|').append(arguments[i]);
		send(flag, reciever, builder.toString().getBytes());
	}
	
	public void send(int flag, int reciever, String data) throws IOException
	{
		send(flag, reciever, data.getBytes(StreamUtils.CHARSET));
	}
	
	public void send(int flag, int reciever, byte[] data) throws IOException
	{
		out.write(flag);
		out.write(reciever);
		out.write(StreamUtils.intToByteArray(data.length));
		out.write(data);
		out.flush();
	}

	public void close()
	{
		try
		{
			socket.close();
			fireChannelClosed();
		}
		catch (IOException e) { /* Keine Fehlermeldungen beim Schließen. */ }
	}
	
	private static final byte[] LENGTH_BUFFER = new byte[4];
	
	public Message recieve() throws IOException
	{
		int type = in.read();
		if (type == Channel.FLAG_EMPTY) return null;
		
		int reciever = in.read();
		
		byte[] buffer = null;
		if (type == Channel.FLAG_EOF)
		{
			buffer = new byte[0];
		}
		else
		{
			in.read(LENGTH_BUFFER);
			int lenght = StreamUtils.byteArrayToInt(LENGTH_BUFFER, 0);
			buffer = new byte[lenght];
			in.read(buffer);
			if (buffer.length == 0) return null;
		}
		return new Message(type, reciever, buffer);
	}
	
	public void poll()
	{
		try
		{
			while (in.available() > 0)
			{
				Message msg = recieve();
				if (msg != null) fireMessageRecieved(msg);
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
					Message msg = recieve();
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
