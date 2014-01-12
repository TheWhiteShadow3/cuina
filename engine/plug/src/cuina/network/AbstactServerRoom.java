package cuina.network;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstactServerRoom
{
	private Server server;
	private String name;
	private String password;
	private String prefix;
	private final List<ServerClient> members = new ArrayList<ServerClient>();

	AbstactServerRoom(Server server, ServerClient owner, String name, String prefix) throws IOException
	{
		this.server = server;
		this.name = name;
		this.prefix = prefix;
		members.add(owner);
		send(Channel.FLAG_ACK, "opened", name);
	}

	public Server getServer()
	{
		return server;
	}

	public String getName()
	{
		return name;
	}

	public List<ServerClient> getMembers()
	{
		return Collections.unmodifiableList(members);
	}
	
	public ServerClient getOwner()
	{
		return members.get(0);
	}
	
	public boolean join(ServerClient client, String password) throws IOException
	{
		if (this.password != null && this.password != password) return false;
		
		sendBroadcast("joined", name, Integer.toString(client.getID()), client.getName());
		members.add(client);
		String[] args = new String[members.size()*2+1];
		args[0] = name;
		for (int i = 0; i < members.size(); i++)
		{
			ServerClient c = members.get(i);
			args[i*2+1] = Integer.toString(c.getID());
			args[i*2+2] = c.getName();
		}
		send(Channel.FLAG_ACK, "joined", args);
		
		return true;
	}
	
	public boolean kick(ServerClient from, String targetID) throws IOException
	{
		if (from != getOwner()) return false;
		
		sendBroadcast("kicked", name, targetID);
		members.remove(from);
		
		if (members.size() == 0)
			server.destroyChatroom(name);

		return true;
	}
	
	public void leave(ServerClient client)
	{
		sendBroadcast("leaved", name, Integer.toString(client.getID()));
		members.remove(client);
		
		if (members.size() == 0)
			server.destroyChatroom(name);
	}

	public boolean lock(ServerClient client, String password) throws IOException
	{
		if (client != getOwner()) return false;
		
		this.password = password;
		sendBroadcast("locked", name, password);
		return true;
	}
	
	protected void send(int flag, String command, String... arguments) throws IOException
	{
		getOwner().getChannel().send(flag, prefix + '.' + command, arguments);
	}
	
	private void sendBroadcast(String cmd, String... args)
	{
		for(ServerClient c : members) try
		{
			c.getChannel().send(Channel.FLAG_INFO, prefix + '.' + cmd, args);
		}
		catch(IOException e) {}
	}
	
	public void send(int fromID, String text) throws IOException
	{
		sendBroadcast("msg", name, Integer.toString(fromID), text);
	}

	public void messageRecieved(ServerClient client, Message msg) throws IOException
	{
		if (!msg.command.startsWith(prefix)) throw new IOException("Illegal Command-Prefix.");
		
		try
		{
			switch(msg.command.substring(prefix.length()+1))
			{
				case "join": join(client, msg.arguments[0]); break;
				case "lock": lock(client, msg.arguments[1]); break;
				case "kick": kick(client, msg.arguments[1]); break;
				case "leave": leave(client); break;
				case "event": send(client.getID(), msg.arguments[1]); break;
				default: System.out.println("[ServerClient] Unknown Command room: " + msg.command);
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
