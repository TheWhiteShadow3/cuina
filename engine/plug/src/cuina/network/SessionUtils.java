package cuina.network;

import java.io.IOException;
import java.net.ServerSocket;

public class SessionUtils
{
	public static int getAvalibleLocalPort() throws NetworkException
	{
		try(ServerSocket socket = new ServerSocket(0))
		{
			return socket.getLocalPort();
		}
		catch(IOException e)
		{
			throw new NetworkException("Could not determine port.");
		}
	}
}
