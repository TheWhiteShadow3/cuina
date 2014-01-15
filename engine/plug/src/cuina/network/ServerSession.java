package cuina.network;


import cuina.network.core.CommandMessage;
import cuina.network.core.Message;
import cuina.network.core.NetID;
import cuina.network.core.Server;
import cuina.network.core.ServerClient;
import cuina.network.core.UDP;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ServerSession implements INetworkSession, ChannelListener
{
	private Server server;
	private NetID netID;
	private String name;
	private UDP udp;
	private Map<NetID, SessionMember> members = new HashMap<NetID, SessionMember>();
	private final Map<NetID, Control> controls = new HashMap<NetID, Control>();
	private boolean open;
	
	public ServerSession(NetID netID, String name, Server server, ServerClient host) throws IOException
	{
		this.server = server;
		this.udp = new UDP(new InetSocketAddress(SessionUtils.getAvalibleLocalPort()), null);
		int port = udp.getLokalAddress().getPort();
		System.out.println("[ServerSession] Port: " + port);
		SessionMember member = addMember(host, -1);
		
		// sende die Nachicht ohne Empfänger, da dieser noch nicht erstellt wurde.
		member.sendMessage(new CommandMessage(NetID.GLOBAL_ID, Message.FLAG_INFO,
				"session.opened", name, Integer.toString(netID.get()), Integer.toString(port)));
		open = true;
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
		return server.isRunning() && open;
	}
	
	@Override
	public void close()
	{
		for(SessionMember member : members.values()) try
		{
			member.sendMessage(new CommandMessage(getID(), Message.FLAG_CLOSE, "close"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		members.clear();
		server.destroySession(this);
		udp.close();
		open = false;
	}
	
	public void join(ServerClient client, int port) throws IOException
	{
		SessionMember member = addMember(client, port);

		String portStr = Integer.toString(udp.getLokalAddress().getPort());
		member.sendMessage(new CommandMessage(NetID.GLOBAL_ID, Message.FLAG_INFO,
				"session.joined", getName(), Integer.toString(getID().get()), portStr));
	}

	public void leave(ServerClient client)
	{
		removeMember(client);
		if (members.isEmpty()) close();
	}

	@Override
	public void sendData(byte[] data) throws IOException
	{
		for(SessionMember member : members.values())
		{
			member.sendData(data);
		}
	}

	@Override
	public void sendEvent(byte[] data) throws IOException
	{
		for(SessionMember member : members.values())
		{
			member.sendEvent(data);
		}
	}

	@Override
	public void sendMessage(Message msg) throws IOException
	{
		for(SessionMember member : members.values())
		{
			member.sendMessage(msg);
		}
	}

	@Override
	public void requestNetworkID(NetID netID) throws IOException
	{
		server.requestNetworkID(netID);
	}
	
	@Override
	public void messageRecieved(Object source, Message msg)
	{
		validateClient(msg.getSender());
		if (msg.getType() == Message.FLAG_CMD)
		{
			handleCommand(new CommandMessage(msg));
		}
		else if (msg.getType() == Message.FLAG_INFO || msg.getType() == Message.FLAG_ACK)
		{
			handleInfo(new CommandMessage(msg));
		}
		else if (msg.getType() == Message.FLAG_EVENT) try
		{
			sendEvent(msg.getData());
		}
		catch(IOException e) {}
		else if (msg.getType() == Message.FLAG_DATA) try
		{
			sendData(msg.getData());
		}
		catch(IOException e) {}
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
			case "opened": handlePortMesssage(msg); break;
		}
	}

	private void handleCommand(CommandMessage msg)
	{
		try
		{
			switch(msg.getCommand())
			{
				case "close":
					sendMessage(new CommandMessage(getID(), Message.FLAG_INFO, "close"));
					close();
					break;
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	private void validateClient(NetID netID)
	{
		if (!members.containsKey(netID))
		{
			server.sendException(netID, "Client ist no Member of Session: " + getID());
		}
	}
	
	private void handlePortMesssage(CommandMessage msg)
	{
		members.get(msg.getSender()).setPort(msg.getArgumentAsInt(0));
	}
	
//	private void join(CommandMessage msg)
//	{
//		int port = Integer.parseInt(msg.getArgument(0));
//		try
//		{
//			join(server.getClient(msg.getSender()), port);
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//	}
	
	private void removeMember(ServerClient client)
	{
		SessionMember member = members.remove(client.getID());
		if (member == null) return;
		
		member.client.getChannel().addChannelListener(getID(), this);
	}
	
	private SessionMember addMember(ServerClient client, int port)
	{
		SessionMember member = new SessionMember(client, port);
		this.members.put(client.getID(), member);
		client.getChannel().addChannelListener(getID(), this);
		
		return member;
	}
	
	@Override
	public String toString()
	{
		return "Session (" + getID() + ") " + name;
	}
	
	private class SessionMember
	{
		private ServerClient client;
		private InetSocketAddress address;
		
		public SessionMember(ServerClient client, int port)
		{
			this.client = client;
			if (port != -1) setPort(port);
		}

		public void setPort(int port)
		{
			this.address = new InetSocketAddress(client.getChannel().getInetAddress(), port);
		}

		public void sendData(byte[] buffer) throws IOException
		{
			udp.send(buffer, address);
		}

		public void sendEvent(byte[] buffer) throws IOException
		{
			client.getChannel().send(getID(), Message.FLAG_DATA, buffer);
		}
		
		public void sendMessage(Message msg) throws IOException
		{
			client.getChannel().send(msg);
		}
	}
}
