package cuina.network;


public class NetworkSession
{
	private Server server;
	private String name;
	private final ServerClient[] clients;

	NetworkSession(Server server, String name, int memberCount)
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
		// TODO Auto-generated method stub
		
	}
}
