package cuina.network;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class Connection implements ChannelListener
{
	private Channel channel;
	private String username;
	
	public Connection(String host, int port, String username, String password) throws NetworkException
	{
		this.channel = new Channel();
		this.username = username;
		try
		{
			this.channel.open(new Socket(host, port));
			channel.login(username, password);
		}
		catch (IOException e)
		{
			throw new NetworkException(e);
		}
	}
	
	public String getUsername()
	{
		return username;
	}

	public void update()
	{
		if (channel.isOpen()) channel.poll();
	}
	
	public boolean isConnected()
	{
		return channel.isOpen();
	}
	
//	public Chatroom openChatroom(String name)
//	{
//		try
//		{
//			channel.send(Channel.FLAG_CMD, "open room " + name);
//			Message msg = channel.read();
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//	}

	@Override
	public void messageRecieved(int flag, byte[] data)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeRequested()
	{
		close();
	}

	@Override
	public void dataRecieved(Map<String, String> data)
	{
		// TODO Auto-generated method stub
		
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
