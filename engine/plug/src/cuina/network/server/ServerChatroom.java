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
	
	void addClient(ServerClient client)
	{
		members.add(client);
	}
	
	void removeClient(ServerClient client)
	{
		members.remove(client);
	}
	
	public boolean join(ServerClient client, String password) throws IOException
	{
		if (this.password != null && this.password != password) return false;
		
		members.add(client);
		String[] args = new String[members.size()*2+1];
		args[0] = name;
		for (int i = 0; i < args.length; i++)
		{
			ServerClient c = members.get(i);
			args[i*2+1] = Integer.toString(c.getID());
			args[i*2+2] = c.getName();
		}
		client.getChannel().send(Channel.FLAG_ACK, "room.joined", args);
		
		return true;
	}
	
	public boolean kick(ServerClient client)
	{
		if (client != getOwner()) return false;
		
		return true;
	}
	
	public void leave(ServerClient client)
	{
		members.remove(client);
		if (members.size() == 0)
		{
			server.destroyChatroom(name);
		}
	}
	
	public void send(int fromID, String text) throws IOException
	{
		for(ServerClient client : members)
		{
			sendMsg(client, fromID, text);
		}
	}
	
	private void sendMsg(ServerClient client, int fromID, String text) throws IOException
	{
		client.getChannel().send(Channel.FLAG_CMD, "room.msg", name, Integer.toString(fromID), text);
	}

	public boolean lock(ServerClient client, String password) throws IOException
	{
		if (client != getOwner()) return false;
		
		this.password = password;
		for(ServerClient c : members)
		{
			c.getChannel().send(Channel.FLAG_CMD, "room.lock", name, password);
		}
		return true;
	}
}
