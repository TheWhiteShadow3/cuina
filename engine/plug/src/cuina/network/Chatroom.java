package cuina.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chatroom extends AbstactChatroom
{
	private Connection connection;
	private final Map<NetID, Client> members = new HashMap<NetID, Client>();
	private final List<ChatMessage> lines = new ArrayList<ChatMessage>();
	private final List<ChatroomListener> listeners = new ArrayList<ChatroomListener>();
	
	public Chatroom(NetID netID, String name, Connection connection) throws IOException
	{
		super(netID, name);
		this.connection = connection;
		connection.getChannel().addChannelListener(netID, this);
	}
	
	@Override
	public boolean isOpen()
	{
		return getID().isSet() && connection.isConnected();
	}
	
	public void addChatroomListener(ChatroomListener l)
	{
		listeners.add(l);
	}

	public void removeChatroomListener(ChatroomListener l)
	{
		listeners.remove(l);
	}
	
	@Override
	protected void addMember(NetID netID, String name)
	{
		Client client = new Client(netID, name);
		members.put(client.getID(), client);
		fireMemberJoined(client);
	}
	
	@Override
	protected void removeMember(NetID netID, boolean forced)
	{
		Client client = members.remove(netID);
		fireMemberLeaved(client, forced);
	}
	
	@Override
	protected void messageRecieved(NetID netID, String text)
	{
		Client client = members.get(netID);
		ChatMessage cm = new ChatMessage(client, System.currentTimeMillis(), text);
		lines.add(cm);
		
		fireMessageRecieved(client, cm);
	}
	
	protected void fireMemberJoined(Client client)
	{
		if (listeners.size() == 0) return;
		for (ChatroomListener l : listeners)
		{
			l.memberJoined(this, client);
		}
	}
	
	protected void fireMemberLeaved(Client client, boolean forced)
	{
		if (client == null || listeners.size() == 0) return;
		for (ChatroomListener l : listeners)
		{
			l.memberLeaved(this, client, forced);
		}
	}
	
	protected void fireMessageRecieved(Client client, ChatMessage cm)
	{
		if (listeners.size() == 0) return;
		for (ChatroomListener l : listeners)
		{
			l.messageRecieved(this, cm);
		}
	}
	
	/**
	 * Sendet einen Text.
	 * @param text Die Nachicht.
	 * @throws IOException Wenn die Nachicht nicht gesendet werden konnte.
	 */
	public void send(String text) throws IOException
	{
		connection.send(new CommandMessage(
				getID(), Message.FLAG_CMD, "msg", Integer.toString(connection.getID().get()), text));
	}
	
	@Override
	public void close()
	{
		connection.getChannel().removeChannelListener(getID());
	}
	
//	public void dispose()
//	{
//		channel.removeChannelListener(channelListeners);
//	}
	

	
//	private class RoomChannelListener implements ChannelListener
//	{
//		@Override
//		public void messageRecieved(Message msg)
//		{
//			if (msg.flag != Channel.FLAG_ACK) return;
//			switch(msg.command)
//			{
//				case "room.joined": memberJoined(getClient(msg)); break;
//				case "room.leaved": memberLeaved(getClient(msg)); break;
//				case "room.msg": messageRecieved(msg); break;
//			}
//		}
//
//		private Client getClient(Message msg)
//		{
//			return new Client(Integer.parseInt(msg.arguments[1]),  msg.arguments[2]);
//		}
//	}
}
