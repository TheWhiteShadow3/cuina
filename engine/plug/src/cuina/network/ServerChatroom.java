package cuina.network;

import cuina.network.core.CommandMessage;
import cuina.network.core.Message;
import cuina.network.core.NetID;
import cuina.network.core.Server;
import cuina.network.core.ServerClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServerChatroom extends AbstactChatroom
{
	private Server server;
	private boolean open;
	private final Map<NetID, ServerClient> members = new HashMap<NetID, ServerClient>();
	
	public ServerChatroom(NetID netID, String name, Server server) throws IOException
	{
		super(netID, name);
		this.server = server;
		this.open = true;
	}
	
	@Override
	public boolean isOpen()
	{
		return server.isRunning() && this.open;
	}

	@Override
	public void close()
	{
		server.destroyChatroom(this);
		this.open = false;
	}

	public void join(ServerClient client) throws IOException
	{
		members.put(client.getID(), client);
		client.getChannel().addChannelListener(getID(), this);

		String[] args = new String[members.size()*2+2];
		args[0] = getName();
		args[1] = Integer.toString(getID().get());
		int i = 2;
		for (ServerClient c : members.values())
		{
			args[i++] = Integer.toString(c.getID().get());
			args[i++] = c.getUsername();
		}
		
		client.send(new CommandMessage(NetID.GLOBAL_ID, Message.FLAG_INFO, "room.joined", args));
	}
	
	public void leave(ServerClient client)
	{
		removeMember(client.getID(), false);
		if (members.isEmpty())
			close();
		else
			sendBroadcast("leaved", Integer.toString(client.getID().get()));
	}
	
	public void kick(ServerClient client)
	{
		removeMember(client.getID(), false);
		if (members.isEmpty())
			close();
		else
			sendBroadcast("kicked", Integer.toString(client.getID().get()));
	}
	
	@Override
	protected void addMember(NetID netID, String name)
	{
		try
		{
			join(server.getClient(netID));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void removeMember(NetID netID, boolean forced)
	{
		ServerClient client = members.remove(netID);
		if (client == null) return;
		
		client.getChannel().removeChannelListener(getID());
	}

	@Override
	protected void messageRecieved(NetID netID, String text)
	{
		sendBroadcast("msg", Integer.toString(netID.get()), text);
	}
	
	private void sendBroadcast(String cmd, String... args)
	{
		for(ServerClient client : members.values()) try
		{
			client.getChannel().send(new CommandMessage(getID(), Message.FLAG_CMD, cmd, args));
		}
		catch(IOException e) {}
	}
	
	@Override
	public String toString()
	{
		return "Room '" + getName() + '\'';
	}
}
