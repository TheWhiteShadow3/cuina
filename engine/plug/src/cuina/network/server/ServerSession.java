package cuina.network.server;

import java.io.IOException;

public class ServerSession
{
	public ServerSession(Server server, String name, int memberCount)
	{
		
	}
	
	public boolean join(ServerClient client, String password) throws IOException
	{
		return true;
	}

	public void leave(ServerClient client)
	{
	}
	
	@Override
	public String toString()
	{
		return "Session";
	}
}
