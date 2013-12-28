package cuina.network;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Connection implements ChannelListener
{
	private Channel channel;
	private String username;
	private final Map<String, Chatroom> rooms = new HashMap<String, Chatroom>();
	private final List<ConnectionListener> listeners = new ArrayList<ConnectionListener>();
	
	public Connection(String host, int port, String username, String password) throws IOException
	{
		this.channel = new Channel();
		this.username = username;
		
		channel.open(new Socket(host, port));
		channel.login(username, password);
		Message msg = readResponse();
		channel.setID(Integer.parseInt(msg.arguments[0]));
		channel.addChannelListener(this);
	}
	
	public int getID()
	{
		return channel.getID();
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public void addConnectionListener(ConnectionListener l)
	{
		listeners.add(l);
	}
	
	public void removeConnectionListener(ConnectionListener l)
	{
		listeners.remove(l);
	}
	
	void fireDisconnected()
	{
		for(ConnectionListener l : listeners)
			l.disconnected();
	}
	
	void fireSessionCreated(NetworkSession session)
	{
		for(ConnectionListener l : listeners)
			l.sessionCreated(session);
	}
	
	void fireSessionDestroyed(NetworkSession session)
	{
		for(ConnectionListener l : listeners)
			l.sessionDestroyed(session);
	}
	
	void fireSessionJoined(NetworkSession session, Client client)
	{
		for(ConnectionListener l : listeners)
			l.sessionJoined(session, client);
	}
	
	void fireSessionLeaved(NetworkSession session, Client client)
	{
		for(ConnectionListener l : listeners)
			l.sessionLeaved(session, client);
	}
	
	void fireRoomJoined(Chatroom room)
	{
		for(ConnectionListener l : listeners)
			l.roomJoined(room);
	}
	
	void fireRoomLeaved(Chatroom room, boolean forced)
	{
		for(ConnectionListener l : listeners)
			l.roomLeaved(room, forced);
	}

	public void update()
	{
		if (channel.isOpen()) channel.poll();
	}
	
	public boolean isConnected()
	{
		return channel.isOpen();
	}
	
	public void joinChatroom(String name, String password) throws IOException
	{
		if (password != null)
			channel.send(Channel.FLAG_CMD, "room.join", name, password);
		else
			channel.send(Channel.FLAG_CMD, "room.join", name);
	}
	
	private Message readResponse() throws IOException
	{
		Message msg = channel.read();
		msg.checkException();
		if (msg.flag != Channel.FLAG_INFO)
			throw new NetworkException(NetworkException.UNEXPECTET_RESPONSE);
		
		return msg;
	}

	public void close()
	{
		if (channel.isOpen()) try
		{
			channel.send(Channel.FLAG_CLOSE, new byte[0]);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		channel.close();
		fireDisconnected();
	}

	@Override
	public void messageRecieved(Message msg)
	{
		System.out.println("[Connection.messageRecieved] " + msg);
		switch(msg.flag)
		{
			case Channel.FLAG_CLOSE:
			case Channel.FLAG_EOF: close(); break;
			
//			case Channel.FLAG_CMD:
			case Channel.FLAG_ACK:
			case Channel.FLAG_INFO: handleAck(msg); break;
		}
	}

	private void handleAck(Message msg)
	{
		switch(msg.command)
		{
			case "room.opened":
			case "room.joined":
			{
				Chatroom room = rooms.get(msg.arguments[0]);
				if (room == null)
				{
					room = new Chatroom(channel, username, msg);
					rooms.put(room.getName(), room);
					fireRoomJoined(room);
				}
				else
					room.fireMemberJoined(Integer.parseInt(msg.arguments[1]), msg.arguments[2]);
			}
			break;
			
			case "room.leaved": leavingEvent(msg, false); break;
			case "room.kicked": leavingEvent(msg, true); break;
			
			case "room.msg":
			{
				Chatroom room = rooms.get(msg.arguments[0]);
				if (room != null)
					room.fireMessageRecieved(Integer.valueOf(msg.arguments[1]), msg.arguments[2]);
			}
		}
	}
	
	private void leavingEvent(Message msg, boolean forced)
	{
		Chatroom room = rooms.get(msg.arguments[0]);
		if (room == null) return;
		
		int id = Integer.parseInt(msg.arguments[1]);
		if (id == channel.getID())
			fireRoomLeaved(room, true);
		else
			room.fireMemberLeaved(id, true);
	}
}
