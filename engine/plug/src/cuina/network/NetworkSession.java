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
	//TODO: Session-Managment implementieren
}
