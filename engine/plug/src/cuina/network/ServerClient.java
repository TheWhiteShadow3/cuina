package cuina.network;



import java.io.IOException;
import java.net.Socket;

public class ServerClient implements NetworkContext, ChannelListener
{
	private NetID netID;
	private Server server;
	private Channel channel;
	private String username;
	private boolean accepted;
	
	ServerClient(NetID netID, Server server, Socket socket) throws IOException
	{
		this.netID = netID;
		this.server = server;
		this.channel = new Channel(netID, this);
		channel.open(socket);
	}
	
	public Channel getChannel()
	{
		return channel;
	}

	@Override
	public NetID getID()
	{
		return netID;
	}
	
	@Override
	public String getUsername()
	{
		return username;
	}
	
	public void identify(CommandMessage msg) throws IOException
	{
		if (!msg.command.equals("login") || msg.arguments.length == 0)
			throw new IOException("Invalid login dataformat recieved.");
		
		this.username = msg.arguments[0];
		String password = (msg.arguments.length == 2) ? msg.arguments[1] : null;
		
		ConnectionSecurityPolicy csp = server.getSecurityPolicy();
		this.accepted = (csp != null) ? csp.newClient(this, username, password) : true;
		if (accepted)
		{
			channel.send(new CommandMessage(
					NetID.GLOBAL_ID, Message.FLAG_LOGIN, "login", Integer.toString(getID().get())));
		}
		else
			channel.send(new SecurityException("Login failed."));
	}

	public boolean accepted() throws IOException
	{
		return accepted;
	}
	
	@Override
	public void messageRecieved(Channel channel, Message msg)
	{
		try
		{
			System.out.println("[ServerClient] recieved: " + msg);
			msg.checkException();
			
			switch(msg.getType())
			{
				case Message.FLAG_EOF:
				case Message.FLAG_CLOSE: close(); break;
				case Message.FLAG_NETID: sendNetID(msg); break;
				
				case Message.FLAG_LOGIN:
					if (accepted)
						channel.send(new IllegalStateException("Already logged in."));
					else
						identify(new CommandMessage(msg));
					break;
				
				case Message.FLAG_CMD: commandRecieved(new CommandMessage(msg));
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	private void sendNetID(Message msg)
	{
		byte[] bytes = StreamUtils.intToByteArray(server.getNetID());
		Message newMsg = new Message(netID, msg.getReciever(), Message.FLAG_NETID, bytes);
		try
		{
			channel.send(newMsg);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void commandRecieved(CommandMessage msg) throws IOException
	{
		ConnectionSecurityPolicy csp = server.getSecurityPolicy();
		if (csp != null)
		{
			if (!csp.recieveCommand(this, msg.command)) return;
		}
		
		switch(msg.getCommand())
		{
			case "session.open": createNewSession(msg); break;
			case "session.join": joinSession(msg); break;
		}
	}

	private void createNewSession(CommandMessage msg) throws IOException
	{
		// XXX: Argument 1 (wenn vorhanden) enthält ein optionales Password.
		server.createNetworkSession(msg.getArgument(0), this);
	}

	private void joinSession(CommandMessage msg) throws IOException
	{
		 ServerSession session = server.getSession(msg.getArgument(0));
		 if (session == null)
		 {
			 channel.send(new NetworkException("Session " + msg.getArgument(0) + " does not exist."));
			 return;
		 }
		 session.join(this, Integer.parseInt(msg.getArgument(1)));
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
	public void channelClosed(Channel channel)
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

	@Override
	public void requestNetworkID(NetID netID) throws IOException
	{
		netID.id = server.getNetID();
	}

	@Override
	public void send(Message msg) throws IOException
	{
		channel.send(msg);
	}
}
