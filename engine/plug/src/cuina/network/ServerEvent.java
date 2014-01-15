package cuina.network;

import cuina.network.core.Server;
import cuina.network.core.ServerClient;


public class ServerEvent
{
	public Server server;
	public ServerClient client;
	public Object data;
	public int type;
	public boolean doIt = true;
}
