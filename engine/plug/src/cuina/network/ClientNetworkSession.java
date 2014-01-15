package cuina.network;

import cuina.network.core.CommandMessage;
import cuina.network.core.Connection;
import cuina.network.core.Message;
import cuina.network.core.NetID;
import cuina.network.core.UDP;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ClientNetworkSession implements INetworkSession, ChannelListener
{
	private Connection connection;
	private NetID netID;
	private String name;
	private UDP udp;
	private final Map<NetID, Control> controls = new HashMap<NetID, Control>();
	private boolean open;
	
	public ClientNetworkSession(NetID netID, String name, Connection connection, int datagramPort) throws IOException
	{
		this.netID = netID;
		this.name = name;
		this.connection = connection;
		connection.getChannel().addChannelListener(netID, this);
		InetSocketAddress lokalAddr = new InetSocketAddress(SessionUtils.getAvalibleLocalPort());
		InetSocketAddress remoteAddr = new InetSocketAddress(connection.getChannel().getInetAddress(), datagramPort);
		this.udp = new UDP(lokalAddr, remoteAddr);
		
		int port = lokalAddr.getPort();
		System.out.println("[ClientNetworkSession] Port: " + port);
		sendMessage(new CommandMessage(netID, Message.FLAG_ACK, "opened", Integer.toString(port)));
		this.open = true;
	}

	@Override
	public NetID getID()
	{
		return netID;
	}
	
	public String getName()
	{
		return name;
	}
	
	@Override
	public boolean isOpen()
	{
		return getID().isSet() && open;
	}

	@Override
	public void close()
	{
		connection.getChannel().removeChannelListener(getID());
		udp.close();
		open = false;
	}
	
	@Override
	public void requestNetworkID(NetID netID) throws IOException
	{
		connection.requestNetworkID(netID);
	}

	@Override
	public void messageRecieved(Object source, Message msg)
	{
		if (msg.getType() == Message.FLAG_CMD)
		{
			handleCommand(new CommandMessage(msg));
		}
		else if (msg.getType() == Message.FLAG_INFO || msg.getType() == Message.FLAG_ACK)
		{
			handleInfo(new CommandMessage(msg));
		}
		else if (msg.getType() == Message.FLAG_EVENT)
		{
			
		}
		else if (msg.getType() == Message.FLAG_DATA)
		{
			
		}
	}
	
	@Override
	public void channelClosed(Object source)
	{
		close();
	}

	private void handleInfo(CommandMessage msg)
	{
		switch(msg.getCommand())
		{
			case "close": close(); break;
		}
	}

	private void handleCommand(CommandMessage msg)
	{
		try
		{
			switch(msg.getCommand())
			{
				case "close": sendMessage(new CommandMessage(connection.getID(), Message.FLAG_CMD, "close")); break;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void sendData(byte[] data) throws IOException
	{
		udp.send(data);
	}

	@Override
	public void sendEvent(byte[] data) throws IOException
	{
		connection.getChannel().send(getID(), Message.FLAG_DATA, data);
	}

	@Override
	public void sendMessage(Message msg) throws IOException
	{
		connection.send(msg);
	}
}
