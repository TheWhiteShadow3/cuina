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
	private NetID netID;
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	
	private Thread messageThread;
//	private Queue<byte[]> messageQueue = new LinkedList<byte[]>();
	private ChannelListener defaultChannelListener;
	private Map<NetID, ChannelListener> listeners = new HashMap<NetID, ChannelListener>();

	public Channel(NetID netID, ChannelListener defaultChannelListener)
	{
		this.netID = netID;
		this.defaultChannelListener = defaultChannelListener;
	}
	
	void setNetID(NetID netID)
	{
		this.netID = netID;
	}

	public NetID getID()
	{
		return netID;
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
		if (msg.getReciever().equals(NetID.GLOBAL_ID))
		{
			defaultChannelListener.messageRecieved(this, msg);
		}
		else
		{
			ChannelListener listener = listeners.get(msg.getReciever());
			if (listener != null)
			{
				listener.messageRecieved(this, msg);
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
			l.channelClosed(this);
		}
		defaultChannelListener.channelClosed(this);
	}

	public boolean isOpen()
	{
		return !socket.isClosed(); 
	}
	
	public void login(String username, String password) throws IOException
	{
		if (username == null) throw new NullPointerException();
		
		if (password != null)
			send(new CommandMessage(NetID.GLOBAL_ID, Message.FLAG_LOGIN, "login", username, password));
		else
			send(new CommandMessage(NetID.GLOBAL_ID, Message.FLAG_LOGIN, "login", username));
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
		send(msg.getReciever(), msg.getType(), msg.getData());
	}
	
	public void send(Exception e) throws IOException
	{
		send(NetID.GLOBAL_ID, Message.FLAG_EXCEPTION, e.getClass().getName() + ": " + e.getMessage());
	}
	
	public void send(NetID reciever, int flag, String data) throws IOException
	{
		send(reciever, flag, data.getBytes(StreamUtils.CHARSET));
	}
	
	public void send(NetID reciever, int flag, byte[] data) throws IOException
	{
		out.writeInt(netID.get());
		out.writeInt(reciever.get());
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
		NetID sender = new NetID(in.readInt());
		NetID reciever = new NetID(in.readInt());
	
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
		
		Message msg = new Message(sender, reciever, type, buffer);
		System.out.println("Channel (" + getID() + ") recieved: " + msg);
		if (!sender.isSet() && type != Message.FLAG_LOGIN)
		{
			String eMesssage = "Illegal message recieved. Client not logged in.\n\t" + msg;
			System.err.println(eMesssage);
			send(new NetworkException(eMesssage));
			return null;
		}
		return msg;
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
