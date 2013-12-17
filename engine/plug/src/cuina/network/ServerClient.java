package cuina.network;


import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class ServerClient implements ChannelListener
{
	private int id;
	private Server server;
	private Channel channel;
	private String name;
	
	ServerClient(Server server, Socket socket, int id) throws IOException
	{
		this.id = id;
		this.server = server;
		this.channel = new Channel();
		channel.open(socket);
	}

	public boolean identify(ConnectionSecurityPolicy csp) throws IOException
	{
		Message msg = channel.read();
		if (msg.flag != Channel.FLAG_LOGIN) throw new IOException("No Login data recieved.");
		
		String data = new String(msg.data);
		int p = data.indexOf(':');
		this.name = data.substring(0, p);
		String password = data.substring(p+1);
		
		if (csp != null)
		{
			return csp.newClient(this, name, password);
		}
		return true;
	}

	public int getID()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public void dataRecieved(Map<String, String> data)
	{
	}

	@Override
	public void messageRecieved(int flag, byte[] data)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeRequested()
	{
		server.disconnect(this);
		close();
	}

	public void close()
	{
		try
		{
			channel.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
