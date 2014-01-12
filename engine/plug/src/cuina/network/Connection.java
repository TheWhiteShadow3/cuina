package cuina.network;

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
	
	private NetworkSession session;
	private final Map<String, Chatroom> rooms = new HashMap<String, Chatroom>();
	private final List<ConnectionListener> listeners = new ArrayList<ConnectionListener>();
	private Queue<NetID> idQueue = new LinkedList<NetID>();
	
	public Connection(String serverHost, int port, String username, String password) throws IOException
	{
		this.netID = new NetID(-1); // temporäre ID.
		this.serverHost = serverHost;
		this.username = username;
		this.channel = new Channel(this);
		
		channel.open(new Socket(serverHost, port));
		channel.login(username, password);
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
		channel.send(this.netID.get(), this.netID.get(), Message.FLAG_NETID, new byte[0]);
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
	
	private String[] authArguments(String name, String password)
	{
		if (password != null)
			return new String[] {name, password};
		else
			return new String[] {name};
	}
	
	public void joinChatroom(String roomName, String password) throws IOException
	{	
		sendCommand(0, "room.join", authArguments(roomName, password));
	}
	
	public void openSession(String sessionName, String password) throws IOException
	{
		sendCommand(0, "session.open", authArguments(sessionName, password));
	}
	
	public void joinSession(String sessionName, String password) throws IOException
	{
		sendCommand(0, "session.join", authArguments(sessionName, password));
	}
	
	private void sendCommand(int reciever, String command, String... arguments) throws IOException
	{
		channel.send(new CommandMessage(netID.get(), reciever, Message.FLAG_CMD, command, arguments));
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
			channel.send(getID().get(), Message.FLAG_CLOSE, 0, new byte[0]);
			Thread.sleep(100);
		}
		catch (IOException | InterruptedException e)
		{
			e.printStackTrace();
		}
		channel.close();
	}
	

	@Override
	public void channelClosed()
	{
		fireDisconnected();
	}

	@Override
	public void messageRecieved(Message msg)
	{
		System.out.println("[Connection] recieved " + msg);
		switch(msg.getType())
		{
			case Message.FLAG_CLOSE:
			case Message.FLAG_EOF: close(); break;
			case Message.FLAG_NETID: handleIDMessage(msg); break;
//			case Channel.FLAG_CMD:
			case Message.FLAG_ACK:
			case Message.FLAG_INFO: try
			{
				handleCommandMessage(new CommandMessage(msg)); break;
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void handleIDMessage(Message msg)
	{
		idQueue.poll().id = StreamUtils.byteArrayToInt(msg.getData(), 0);
	}

	private void handleCommandMessage(CommandMessage msg) throws IOException
	{
		msg.checkException();
		
		switch(msg.command)
		{
			case "login":
				this.netID.id = Integer.parseInt(msg.arguments[0]);
				fireConnected();
				break;
				
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
					room.addMember(Integer.parseInt(msg.arguments[1]), msg.arguments[2]);
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
				int port = Integer.parseInt(msg.arguments[1]);
				this.session = new ClientNetworkSession(msg.getSenderID(), msg.arguments[0], this, port);
				fireSessionCreated(session);
			}
			break;
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
