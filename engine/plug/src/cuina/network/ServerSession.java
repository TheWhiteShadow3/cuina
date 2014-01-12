package cuina.network;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ServerSession extends NetworkSession
{
	private Server server;
	private DatagramSocket socket;
	private List<SessionMember> members = new ArrayList<SessionMember>();
//	private int maxMembers;
	
	public ServerSession(NetID netID, String name, Server server, ServerClient host) throws IOException
	{
		super(netID, name);
		this.server = server;
		this.socket = new DatagramSocket();
		socket.bind(null);
		System.out.println("Port: " + socket.getPort());
		SessionMember hostMember = new SessionMember(host, -1);
		this.members.add(hostMember);
		
		hostMember.sendMessage(new CommandMessage(Channel.FLAG_INFO, 0, "session.opened",
				Integer.toString(netID.get()), name, Integer.toString(socket.getPort())));
		
		//TODO: socket.getPort() ist -1, solange keine Verbindung erfolgt. Ohne Port gibts aber auch Keine. 
	}

	@Override
	public boolean isOpen()
	{
		return server.isRunning();
	}
	
	@Override
	protected void close()
	{
		server.destroySession(this);
		super.close();
	}
	
	public void join(ServerClient client, int port) throws IOException
	{
		SessionMember hostMember = new SessionMember(client, port);
		this.members.add(hostMember);
		
		String sessionPort = Integer.toString(socket.getPort());
		hostMember.sendMessage(new CommandMessage(Channel.FLAG_INFO, 0, "session.joined",
				Integer.toString(getID().get()), getName(), sessionPort));
	}

	public void leave(ServerClient client)
	{
		for(int i = 0; i < members.size(); i++)
		{
			if (members.get(i).client == client)
			{
				members.remove(i);
				return;
			}
		}
		if (members.isEmpty()) close();
	}

	@Override
	public String toString()
	{
		return "Session";
	}

	@Override
	public void sendData(ByteBuffer buffer) throws IOException
	{
		for(SessionMember member : members)
		{
			member.sendData(buffer);
		}
	}

	@Override
	public void sendEvent(ByteBuffer buffer) throws IOException
	{
		for(SessionMember member : members)
		{
			member.sendEvent(buffer);
		}
	}

	@Override
	public void sendMessage(Message msg) throws IOException
	{
		for(SessionMember member : members)
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
		switch(msg.getCommand())
		{
			case "close": close(); break;
		}
	}
	
	@Override
	protected void handleCommand(CommandMessage msg)
	{
		try
		{
			switch(msg.command)
			{
				case "close": sendMessage(new CommandMessage(Channel.FLAG_INFO, 0, "close")); break;
				case "join": addMember(msg); break;
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	private void addMember(CommandMessage msg)
	{
		int id = Integer.parseInt(msg.getArgument(0));
		int port = Integer.parseInt(msg.getArgument(1));
		try
		{
			join(server.getClient(id), port);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
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
			client.getChannel().send(Channel.FLAG_BYTES, getID().get(), bytes);
		}
		
		public void sendMessage(Message msg) throws IOException
		{
			client.getChannel().send(msg);
		}
	}
}
