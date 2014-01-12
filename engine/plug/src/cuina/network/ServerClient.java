package cuina.network;



import java.io.IOException;
import java.net.Socket;

public class ServerClient implements ChannelListener
{
	private NetID netID;
	private Server server;
	private Channel channel;
	private String username;
	
	ServerClient(NetID netID, Server server, Socket socket) throws IOException
	{
		this.netID = netID;
		this.server = server;
		this.channel = new Channel(this);
		channel.open(socket);
	}
	
	public Channel getChannel()
	{
		return channel;
	}

	public NetID getID()
	{
		return netID;
	}
	
	public String getUsername()
	{
		return username;
	}

	public boolean identify() throws IOException
	{
		CommandMessage msg = new CommandMessage(channel.recieve());
		if (!msg.command.equals("login") || msg.arguments.length == 0)
			throw new IOException("Invalid login dataformat recieved.");
		
		this.username = msg.arguments[0];
		String password = (msg.arguments.length == 2) ? msg.arguments[1] : null;
		
		ConnectionSecurityPolicy csp = server.getSecurityPolicy();
		boolean acepted = (csp != null) ? csp.newClient(this, username, password) : true;
		if (acepted)
		{
			channel.send(Channel.FLAG_INFO, 0, "login", Integer.toString(getID().get()));
			channel.addChannelListener(netID, this);
		}
		else
			channel.send(new SecurityException("Login failed."));
		return acepted;
	}
	
	@Override
	public void messageRecieved(Message msg)
	{
		System.out.println("[ServerClient.messageRecieved] " + msg);
		switch(msg.getType())
		{
			case Channel.FLAG_EOF:
			case Channel.FLAG_CLOSE: close(); break;
			case Channel.FLAG_NETID: sendNetID(msg); break;
			case Channel.FLAG_CMD: commandRecieved(new CommandMessage(msg));
		}
	}

	private void sendNetID(Message msg)
	{
		byte[] bytes = StreamUtils.intToByteArray(server.getNetID());
		Message newMsg = new Message(Channel.FLAG_NETID, msg.getReciever(), bytes);
		try
		{
			channel.send(newMsg);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void commandRecieved(CommandMessage msg)
	{
		ConnectionSecurityPolicy csp = server.getSecurityPolicy();
		if (csp != null)
		{
			if (!csp.recieveCommand(this, msg.command)) return;
		}
		
		try
		{
			switch(msg.getCommand())
			{
				case "login": channel.send(new IllegalStateException("Already logged in.")); break;
				case "session.open": createNewSession(msg); break;
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void createNewSession(CommandMessage msg) throws IOException
	{
		server.createNetworkSession(msg.getArgument(0), this);
	}

//	private void handleSessionCommand(String cmd, String[] arguments) throws IOException
//	{
//		if ("open".equals(cmd))
//		{
//			if (!server.createNetworkSession(arguments[0], this, Integer.parseInt(arguments[1])))
//			{
//				channel.send(new NetworkException("Could not create Session."));
//			}
//			return;
//		}
//		System.out.println("[ServerClient] Unknown Command session." + cmd);
//	}
	
//	private void handleChatroomCommand(CommandMessage msg) throws IOException
//	{
//		String name = msg.arguments[0];
//		ServerChatroom room = server.getChatroom(name);
//		if (room != null)
//			room.messageRecieved(this, msg);
//		else
//		{
//			if (!server.createChatroom(this, name));
//			{
//				channel.send(new NetworkException("Could not create Room."));
//			}
//		}
//	}
	

	@Override
	public void channelClosed()
	{
		server.disconnect(this);
	}

	public void close()
	{
		channel.close();
	}
	
	@Override
	public String toString()
	{
		return "Client (" + getID() + ") " + username;
	}
}
