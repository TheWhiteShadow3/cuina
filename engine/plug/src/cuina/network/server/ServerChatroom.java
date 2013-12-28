package cuina.network.server;

import cuina.network.Channel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerChatroom
{
	private Server server;
	private String name;
	private String password;
	private final List<ServerClient> members = new ArrayList<ServerClient>();

	ServerChatroom(Server server, ServerClient owner, String name) throws IOException
	{
		this.server = server;
		this.name = name;
		members.add(owner);
		owner.getChannel().send(Channel.FLAG_ACK, "room.opened", name);
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
		
		sendBroadcast("room.joined", name, Integer.toString(client.getID()), client.getName());
		members.add(client);
		String[] args = new String[members.size()*2+1];
		args[0] = name;
		for (int i = 0; i < members.size(); i++)
		{
			ServerClient c = members.get(i);
			args[i*2+1] = Integer.toString(c.getID());
			args[i*2+2] = c.getName();
		}
		client.getChannel().send(Channel.FLAG_ACK, "room.joined", args);
		
		return true;
	}
	
	public boolean kick(ServerClient from, String targetID) throws IOException
	{
		if (from != getOwner()) return false;
		
		sendBroadcast("room.kicked", name, targetID);
		members.remove(from);
		
		if (members.size() == 0)
			server.destroyChatroom(name);

		return true;
	}
	
	public void leave(ServerClient client)
	{
		sendBroadcast("room.leaved", name, Integer.toString(client.getID()));
		members.remove(client);
		
		if (members.size() == 0)
			server.destroyChatroom(name);
	}
	
	public void send(int fromID, String text) throws IOException
	{
		sendBroadcast("room.msg", name, Integer.toString(fromID), text);
	}

	public boolean lock(ServerClient client, String password) throws IOException
	{
		if (client != getOwner()) return false;
		
		this.password = password;
		sendBroadcast("room.locked", name, password);
		return true;
	}
	
	private void sendBroadcast(String cmd, String... args)
	{
		for(ServerClient c : members) try
		{
			c.getChannel().send(Channel.FLAG_INFO, cmd, args);
		}
		catch(IOException e) {}
	}

	@Override
	public String toString()
	{
		return "Room '" + name + '\'';
	}
}
