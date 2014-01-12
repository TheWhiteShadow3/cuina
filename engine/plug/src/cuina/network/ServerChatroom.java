package cuina.network;

import java.io.IOException;

public class ServerChatroom extends AbstactServerRoom
{
	ServerChatroom(Server server, ServerClient owner, String name) throws IOException
	{
		super(server, owner, name, "room");
	}
	
	@Override
	public String toString()
	{
		return "Room '" + getName() + '\'';
	}
}
