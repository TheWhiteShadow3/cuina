package cuina.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Channel
{
	private int id;
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	
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
		
		this.in = new DataInputStream(socket.getInputStream());
		this.out = new DataOutputStream(socket.getOutputStream());
		
		messageThread = new MessageThread();
		messageThread.setDaemon(true);
		messageThread.start();
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
		return true;
	}

	public boolean removeChannelListener(NetID netID)
	{
		if (!netID.isSet()) return false;
		
		listeners.remove(netID);
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
			ChannelListener listener = listeners.get(msg.getRecieverID());
			if (listener != null)
			{
				listener.messageRecieved(msg);
			}
			else
			{
				System.err.println("Unbehandelte Nachicht! " + msg);
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
			send(new CommandMessage(0, Message.FLAG_CMD, "login", username, password));
		else
			send(new CommandMessage(0, Message.FLAG_CMD, "login", username));
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
		send(msg.getSender(), msg.getReciever(), msg.getType(), msg.getData());
	}
	
	public void send(Exception e) throws IOException
	{
		send(new CommandMessage(0, Message.FLAG_EXCEPTION, e.getClass().getName(), e.getMessage()));
	}
	
	public void send(int sender, int reciever, int flag, String data) throws IOException
	{
		send(sender, flag, reciever, data.getBytes(StreamUtils.CHARSET));
	}
	
	public void send(int sender, int reciever, int flag, byte[] data) throws IOException
	{
		out.writeInt(sender);
		out.writeInt(reciever);
		out.write(flag);
		if (flag != Message.FLAG_EMPTY)
		{	// Stelle sicher, dass leere Nachichten auch leer sind.
			out.write(StreamUtils.intToByteArray(data.length));
			out.write(data);
		}
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
		int sender = in.readInt();
		int reciever = in.readInt();
		
		int type = in.read();
		if (type == Message.FLAG_EMPTY) return null;
		
		byte[] buffer = null;
		if (type == Message.FLAG_EOF)
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
		return new Message(sender, reciever, type, buffer);
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
