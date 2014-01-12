package cuina.network;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class ServerSession extends NetworkSession
{
	private Server server;
	private InetSocketAddress address;
	private DatagramSocket socket;
	private Map<NetID, SessionMember> members = new HashMap<NetID, SessionMember>();
//	private int maxMembers;
	private boolean open;
	
	public ServerSession(NetID netID, String name, Server server, ServerClient host) throws IOException
	{
		super(netID, name);
		this.server = server;
		this.address = new InetSocketAddress(SessionUtils.getAvalibleLocalPort());
		this.socket = new DatagramSocket(address);
		this.open = true;
		
		System.out.println("[ServerSession] Port: " + address.getPort());
		SessionMember member = addMember(host, -1);
		
		// sende die Nachicht ohne Empfänger, da dieser noch nicht erstellt wurde.
		member.sendMessage(new CommandMessage(NetID.GLOBAL_ID, Message.FLAG_INFO,
				"session.opened", name, Integer.toString(netID.get()), Integer.toString(address.getPort())));
	}

	@Override
	public boolean isOpen()
	{
		return server.isRunning() && this.open;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		members.clear();
		server.destroySession(this);
		this.open = false;
	}
	
	public void join(ServerClient client, int port) throws IOException
	{
		SessionMember member = addMember(client, port);

		member.sendMessage(new CommandMessage(NetID.GLOBAL_ID, Message.FLAG_INFO,
				"session.joined", getName(), Integer.toString(getID().get()), Integer.toString(socket.getPort())));
	}

	public void leave(ServerClient client)
	{
		removeMember(client);
		if (members.isEmpty()) close();
	}

	@Override
	public void sendData(ByteBuffer buffer) throws IOException
	{
		for(SessionMember member : members.values())
		{
			member.sendData(buffer);
		}
	}

	@Override
	public void sendEvent(ByteBuffer buffer) throws IOException
	{
		for(SessionMember member : members.values())
		{
			member.sendEvent(buffer);
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
		netID.id = server.getNetID();
	}
	
	@Override
	protected void handleInfo(CommandMessage msg)
	{
		validateClient(msg.getSender());
		switch(msg.getCommand())
		{
			case "close": close(); break;
			case "opened": handlePortMesssage(msg); break;
				
		}
	}

	@Override
	protected void handleCommand(CommandMessage msg)
	{
		validateClient(msg.getSender());
		try
		{
			switch(msg.getCommand())
			{
				case "close": sendMessage(new CommandMessage(getID(), Message.FLAG_INFO, "close")); break;
//				case "join": join(msg); break;
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
		members.get(msg.getSender()).port = msg.getArgumentAsInt(0);
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
		return "Session (" + getID() + ") " + getName();
	}
	
	private class SessionMember
	{
		public ServerClient client;
		public int port;
		
		public SessionMember(ServerClient client, int port)
		{
			this.client = client;
			this.port = port;
		}

		public void sendData(ByteBuffer buffer) throws IOException
		{
			if (port == -1) throw new IOException("Data-Port not set.");
			
			buffer.flip();
			byte[] bytes = new byte[buffer.limit()];
			buffer.get(bytes);
			InetSocketAddress address = new InetSocketAddress(client.getChannel().getInetAddress(), port);
			DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address);
			socket.send(packet);
		}

		public void sendEvent(ByteBuffer buffer) throws IOException
		{
			buffer.flip();
			byte[] bytes = new byte[buffer.limit()];
			buffer.get(bytes);
			client.getChannel().send(getID(), Message.FLAG_BYTES, bytes);
		}
		
		public void sendMessage(Message msg) throws IOException
		{
			client.getChannel().send(msg);
		}
	}
}
