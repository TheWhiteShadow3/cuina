package cuina.network;

import java.io.IOException;
import java.net.Socket;

public class ServerClient extends Client
{
	private Server server;
	
	ServerClient(Server server, Socket socket, int id) throws IOException
	{
		this.id = id;
		this.server = server;
		open(socket);
	}
}
