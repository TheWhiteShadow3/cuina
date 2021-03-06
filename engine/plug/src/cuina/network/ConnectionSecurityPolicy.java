package cuina.network;

import cuina.network.core.ServerClient;


public interface ConnectionSecurityPolicy
{
	public boolean newClient(ServerClient client, String username, String password);
	
	public boolean recieveCommand(ServerClient client, String command);
}
