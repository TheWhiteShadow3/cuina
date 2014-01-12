package cuina.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class ClientNetworkSession extends NetworkSession
{
	private Connection connection;
	private InetSocketAddress address;
	private DatagramSocket socket;
	private int datagramPort;
	
	public ClientNetworkSession(NetID netID, String name, Connection connection, int datagramPort) throws IOException
	{
		super(netID, name);
		this.connection = connection;
		this.datagramPort = datagramPort;
		this.address = new InetSocketAddress(SessionUtils.getAvalibleLocalPort());
		this.socket = new DatagramSocket(address);
		connection.getChannel().addChannelListener(netID, this);
		
		System.out.println("[ClientNetworkSession] Port: " + address.getPort());
		sendMessage(createSessionMessage(Message.FLAG_ACK, "opened", Integer.toString(address.getPort())));
	}
	
	@Override
	public boolean isOpen()
	{
		return getID().isSet() && connection.isConnected();
	}

	@Override
	protected void close()
	{
		socket.close();
		super.close();
	}

	@Override
	public void requestNetworkID(NetID netID) throws IOException
	{
		connection.requestNetworkID(netID);
	}
	
	@Override
	protected void handleCommand(CommandMessage msg)
	{
		try
		{
			switch(msg.command)
			{
				case "close": sendMessage(createSessionMessage(Message.FLAG_CMD, "close")); break;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void sendData(ByteBuffer buffer) throws IOException
	{
		buffer.flip();
		byte[] bytes = new byte[buffer.limit()];
		buffer.get(bytes);
		DatagramPacket packet = new DatagramPacket(
				bytes, bytes.length, connection.getChannel().getInetAddress(), datagramPort);
		socket.send(packet);
	}

	@Override
	public void sendEvent(ByteBuffer buffer) throws IOException
	{
		buffer.flip();
		byte[] bytes = new byte[buffer.limit()];
		buffer.get(bytes);
		connection.getChannel().send(getID().get(), getID().get(), Message.FLAG_BYTES, bytes);
	}

	@Override
	public void sendMessage(Message msg) throws IOException
	{
		connection.send(msg);
	}
}
