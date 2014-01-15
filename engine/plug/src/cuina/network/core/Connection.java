package cuina.network.core;

import cuina.network.ChannelListener;
import cuina.network.Chatroom;
import cuina.network.Client;
import cuina.network.ClientNetworkSession;
import cuina.network.ConnectionListener;
import cuina.network.INetworkSession;
import cuina.network.NetworkContext;
import cuina.network.StreamUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Connection implements ChannelListener, NetworkContext
{
	private NetID netID;
	private String serverHost;
	private Channel channel;
	private String username;
	
	private INetworkSession session;
	private final Map<String, Chatroom> rooms = new HashMap<String, Chatroom>();
	private final List<ConnectionListener> listeners = new ArrayList<ConnectionListener>();
	private Queue<NetID> idQueue = new LinkedList<NetID>();
	
	public Connection(String serverHost, int port, String username, String password) throws IOException
	{
		this.netID = NetID.EMPTY_ID; // temporäre ID.
		this.serverHost = serverHost;
		this.username = username;
		this.channel = new Channel(netID, this);
		
		channel.open(new Socket(serverHost, port));
		channel.login(username, password);
		try
		{
			synchronized (this)
			{
				wait();
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public NetID getID()
	{
		return netID;
	}
	
	@Override
	public String getUsername()
	{
		return username;
	}
	
	public String getServerHost()
	{
		return serverHost;
	}
	
	public Channel getChannel()
	{
		return channel;
	}
	
	@Override
	public void requestNetworkID(NetID netID) throws IOException
	{
		channel.send(this.netID, Message.FLAG_NETID, new byte[0]);
		idQueue.add(netID);
	}
	
	public void addConnectionListener(ConnectionListener l)
	{
		listeners.add(l);
	}
	
	public void removeConnectionListener(ConnectionListener l)
	{
		listeners.remove(l);
	}
	
	void fireConnected()
	{
		for(ConnectionListener l : listeners)
			l.connected();
	}
	
	void fireDisconnected()
	{
		for(ConnectionListener l : listeners)
			l.disconnected();
	}
	
	void fireSessionCreated(INetworkSession session)
	{
		for(ConnectionListener l : listeners)
			l.sessionCreated(session);
	}
	
	void fireSessionDestroyed(INetworkSession session)
	{
		for(ConnectionListener l : listeners)
			l.sessionDestroyed(session);
	}
	
	void fireSessionJoined(INetworkSession session, Client client)
	{
		for(ConnectionListener l : listeners)
			l.sessionJoined(session, client);
	}
	
	void fireSessionLeaved(INetworkSession session, Client client)
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
	
	private String[] authArguments(String name, String password)
	{
		if (password != null)
			return new String[] {name, password};
		else
			return new String[] {name};
	}
	
	public void joinChatroom(String roomName, String password) throws IOException
	{
		sendCommand(NetID.GLOBAL_ID, "room.join", authArguments(roomName, password));
	}
	
	public void openSession(String sessionName, String password) throws IOException
	{
		sendCommand(NetID.GLOBAL_ID, "session.open", authArguments(sessionName, password));
	}
	
	public void joinSession(String sessionName, String password) throws IOException
	{
		sendCommand(NetID.GLOBAL_ID, "session.join", authArguments(sessionName, password));
	}
	
	private void sendCommand(NetID reciever, String command, String... arguments) throws IOException
	{
		channel.send(new CommandMessage(reciever, Message.FLAG_CMD, command, arguments));
	}
	
//	private CommandMessage readResponse() throws IOException
//	{
//		CommandMessage msg = new CommandMessage(channel.recieve());
//		msg.checkException();
//		if (msg.getType() != Channel.FLAG_INFO)
//			throw new NetworkException(NetworkException.UNEXPECTET_RESPONSE);
//		
//		return msg;
//	}

	public void close()
	{
		if (channel.isOpen()) try
		{
			channel.send(NetID.GLOBAL_ID, Message.FLAG_CLOSE, new byte[0]);
			Thread.sleep(100);
		}
		catch (IOException | InterruptedException e)
		{
			e.printStackTrace();
		}
		channel.close();
	}
	

	@Override
	public void channelClosed(Object source)
	{
		fireDisconnected();
	}

	@Override
	public void messageRecieved(Object source, Message msg)
	{
		try
		{
			System.out.println("[Connection] recieved " + msg);
			msg.checkException();
			
			switch(msg.getType())
			{
				case Message.FLAG_LOGIN: handleLogin(msg); break;
				case Message.FLAG_CLOSE:
				case Message.FLAG_EOF: close(); break;
				case Message.FLAG_NETID: handleIDMessage(msg); break;
				
				case Message.FLAG_ACK:
				case Message.FLAG_INFO: InfoRecieved(new CommandMessage(msg)); break;
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	private void handleLogin(Message msg)
	{
		this.netID = msg.getSender();
		channel.setNetID(netID);
		synchronized (this)
		{
			notify();
		}
		fireConnected();
	}

	private void handleIDMessage(Message msg)
	{
		idQueue.poll().id = StreamUtils.byteArrayToInt(msg.getData(), 0);
	}

	private void InfoRecieved(CommandMessage msg) throws IOException
	{
		switch(msg.getCommand())
		{
			case "room.opened":
			case "room.joined":
			{
				String roomName = msg.getArgument(0);
				Chatroom room = rooms.get(roomName);
				if (room == null)
				{
					NetID netID = msg.getArgumentAsID(1);
					room = new Chatroom(netID, roomName, this);
					fillRoom(room, msg);
					rooms.put(room.getName(), room);
					fireRoomJoined(room);
				}
			}
			break;
			
//			case "room.leaved": roomLeavingEvent(msg, false); break;
//			case "room.kicked": roomLeavingEvent(msg, true); break;
//			
//			case "room.msg":
//			{
//				Chatroom room = rooms.get(msg.arguments[0]);
//				if (room != null)
//					room.messageRecieved(Integer.valueOf(msg.arguments[1]), msg.arguments[2]);
//			}
//			break;
			
			case "session.opened":
			if (session == null)
			{
				this.session = new ClientNetworkSession(
						msg.getArgumentAsID(1), msg.getArgument(0), this, msg.getArgumentAsInt(2));
				fireSessionCreated(session);
			}
			break;
		}
	}

	private void fillRoom(Chatroom room, CommandMessage msg)
	{
		for(int i = 2; i < msg.getArgumentCount(); i += 2)
		{
			room.addMember(msg.getArgumentAsID(i), msg.getArgument(i+1));
		}
	}

	@Override
	public void send(Message msg) throws IOException
	{
		channel.send(msg);
	}
	
//	private void roomLeavingEvent(Message msg, boolean forced)
//	{
//		Chatroom room = rooms.get(msg.arguments[0]);
//		if (room == null) return;
//		
//		int id = Integer.parseInt(msg.arguments[1]);
//		if (id == channel.getID())
//			fireRoomLeaved(room, true);
//		else
//			room.removeMember(id, true);
//	}
	
//	private void sessionLeavingEvent(Message msg, boolean forced)
//	{
//		Chatroom room = rooms.get(msg.arguments[0]);
//		if (room == null) return;
//		
//		int id = Integer.parseInt(msg.arguments[1]);
//		if (id == channel.getID())
//			fireRoomLeaved(room, true);
//		else
//			room.removeMember(id, true);
//	}
}
