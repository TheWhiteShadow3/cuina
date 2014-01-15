package cuina.network.core;

import cuina.network.ChannelListener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class UDP
{
	private static final int BUFFER_SIZE = 4096;
	
	private DatagramSocket socket;
	private InetSocketAddress lokalAddress;
	private InetSocketAddress remoteAddress;
	private UDPThread udpThread;
	private int sendID;
	private int recieveID = -1;
	
	private DatagramPacket packet;
	private ByteBuffer buffer;
	private List<ChannelListener> listeners = new ArrayList<ChannelListener>();

	public UDP(InetSocketAddress lokalAddress, InetSocketAddress remoteAddress) throws SocketException
	{
		this.lokalAddress = lokalAddress;
		this.remoteAddress = remoteAddress;
		this.socket = new DatagramSocket(lokalAddress);
		
		this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
		this.packet = new DatagramPacket(buffer.array(), buffer.capacity());
		udpThread = new UDPThread();
		udpThread.start();
	}
	
	public InetSocketAddress getLokalAddress()
	{
		return lokalAddress;
	}

	public InetSocketAddress getRemoteAddress()
	{
		return remoteAddress;
	}
	
	public void addChannelListener(ChannelListener listener)
	{
		listeners.add(listener);
	}

	public void removeChannelListener(ChannelListener listener)
	{
		listeners.remove(listener);
	}
	
	void fireDataRecieved(Message msg)
	{
		for(ChannelListener l : listeners)
		{
			l.messageRecieved(this, msg);
		}
	}

	public void send(byte[] data) throws IOException
	{
		send(data, remoteAddress);
	}
	
	public void send(byte[] data, InetSocketAddress remoteAddress) throws IOException
	{
		if (remoteAddress == null) throw new NullPointerException("Remote-Address is null.");
		if (data.length >= BUFFER_SIZE - 4) throw new IOException("Data-array to long.");
		
		ByteBuffer buffer = ByteBuffer.allocate(data.length + 4);
		buffer.putInt(sendID++);
		buffer.put(data);
		DatagramPacket packet = new DatagramPacket(buffer.array(), buffer.position(), remoteAddress);
		socket.send(packet);
	}
	
	public ByteBuffer recieve() throws IOException
	{
		int id;
		buffer.clear();
		do
		{
			socket.receive(packet);
			buffer.limit(packet.getLength());
			id = buffer.getInt();
		}
		while(id <= recieveID);
		recieveID = id;
		return buffer;
	}
	
//	private int readInt(byte[] b, int offset)
//	{
//		buffer.put(b, offset, 4);
//		buffer.flip();
//		return buffer.getInt();
//	}
	
	public void close()
	{
		socket.close();
	}
	
	private class UDPThread extends Thread
	{
		public UDPThread()
		{
			setDaemon(true);
		}

		@Override
		public void run()
		{
			while(!socket.isClosed()) try
			{
				ByteBuffer buffer = recieve();
				
				NetID sender = new NetID(buffer.getInt());
				NetID reciever = new NetID(buffer.getInt());
				
				byte[] data = new byte[buffer.limit() - buffer.position()];
				buffer.get(data);
				fireDataRecieved(new Message(sender, reciever, Message.FLAG_DATA, data));
			}
			catch (SocketException e)
			{
				return;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}