package cuina.network;

import cuina.network.server.Server;
import cuina.network.server.ServerClient;

import java.util.HashMap;
import java.util.Map;



public class NetworkSession
{
	private Server server;
	private String name;
	private final ServerClient[] clients;
	private final Map<Integer, Control> controls = new HashMap<Integer, Control>();
	
	public NetworkSession(Server server, String name, int memberCount)
	{
		this.server = server;
		this.name = name;
		this.clients = new ServerClient[memberCount];
	}
	
	public ServerClient getHost()
	{
		return clients[0];
	}
	//TODO: Session-Managment implementieren

	public void update()
	{
		
	}

	public void addControl(Control control)
	{
		// TODO Auto-generated method stub
		
	}
}
