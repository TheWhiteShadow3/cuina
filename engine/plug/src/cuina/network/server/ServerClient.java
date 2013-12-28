package cuina.network.server;


import cuina.network.Channel;
import cuina.network.ChannelListener;
import cuina.network.ConnectionSecurityPolicy;
import cuina.network.Message;

import java.io.IOException;
import java.net.Socket;

public class ServerClient implements ChannelListener
{
	private Server server;
	private Channel channel;
	private String name;
	
	ServerClient(Server server, Socket socket, int id) throws IOException
	{
		this.server = server;
		this.channel = new Channel();
		channel.open(socket);
		channel.setID(id);
	}
	
	Channel getChannel()
	{
		return channel;
	}

	public boolean identify() throws IOException
	{
		Message msg = channel.read();
		if (!msg.command.equals("login") || msg.arguments.length == 0)
			throw new IOException("Invalid login dataformat recieved.");
		
		this.name = msg.arguments[0];
		String password = (msg.arguments.length == 2) ? msg.arguments[1] : null;
		
		ConnectionSecurityPolicy csp = server.getSecurityPolicy();
		boolean acepted = (csp != null) ? csp.newClient(this, name, password) : true;
		if (acepted)
		{
			channel.send(Channel.FLAG_ACK, "login", Integer.toString(getID()));
			channel.addChannelListener(this);
		}
		else
			channel.send(new SecurityException("Login failed."));
		return acepted;
	}

	public int getID()
	{
		return channel.getID();
	}

	public String getName()
	{
		return name;
	}
	
	@Override
	public void messageRecieved(Message msg)
	{
//		System.out.println("[ServerClient.messageRecieved] " + msg);
		switch(msg.flag)
		{
			case Channel.FLAG_EOF:
			case Channel.FLAG_CLOSE: close(); break;
			case Channel.FLAG_CMD: commandRecieved(msg);
		}
	}

	private void commandRecieved(Message msg)
	{
		ConnectionSecurityPolicy csp = server.getSecurityPolicy();
		if (csp != null)
		{
			if (!csp.recieveCommand(this, msg.command)) return;
		}
		try
		{
			int dot = msg.command.indexOf('.');
			if (dot == -1)
			{
				switch(msg.command)
				{
					case "login": channel.send(new IllegalStateException("Already logged in.")); break;
					default: System.out.println("Unknown Command " + msg.command);
				}
			}
			else
			{
				String part1 = msg.command.substring(0, dot);
				String part2 = msg.command.substring(dot+1);
				switch(part1)
				{
					case "session": handleSessionCommand(part2, msg.arguments); break;
					case "room": handleChatroomCommand(part2, msg.arguments); break;
					default: System.out.println("[ServerClient] Unknown Command " + msg.command);
				}	
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	private void handleSessionCommand(String cmd, String[] arguments) throws IOException
	{
		if ("open".equals(cmd))
		{
			int port = -1; //FIXME: Freien Port emitteln.
			server.createNetworkSession(this, arguments[0], port, Integer.parseInt(arguments[1]));
			return;
		}
		System.out.println("[ServerClient] Unknown Command session." + cmd);
	}
	
	private void handleChatroomCommand(String cmd, String[] arguments) throws IOException
	{
		ServerChatroom room = server.getChatroom(arguments[0]);
		switch(cmd)
		{
			case "join":
				if (room == null)
					server.createChatroom(this, arguments[0]);
				else
					room.join(this, arguments[1]);
				break;
			
			case "lock": room.lock(this, arguments[1]); break;
			case "leave": room.leave(this); break;
			case "msg": room.send(getID(), arguments[1]); break;
			default: System.out.println("[ServerClient] Unknown Command room." + cmd);
		}
	}

	public void close()
	{
		channel.close();
		server.disconnect(this);
	}
}
