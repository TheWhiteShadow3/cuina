package cuina.network;

import cuina.database.NamedItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chatroom implements NamedItem
{
	private Channel channel;
	private String name;
	private boolean password;
	private final Map<Integer, Client> members = new HashMap<Integer, Client>();
	private List<ChatMessage> lines = new ArrayList<ChatMessage>();
	private final List<ChatroomListener> listeners = new ArrayList<ChatroomListener>();
//	private ChannelListener channelListeners;

	public Chatroom(Channel channel, String username, Message msg)
	{
		this.channel = channel;
		switch(msg.command)
		{
			case "room.opened":
			case "room.joined":
			{
				this.name = msg.arguments[0];
				members.put(channel.getID(), new Client(channel.getID(), username));
				for(int i = 1; i < msg.arguments.length; i += 2)
				{
					int id = Integer.parseInt(msg.arguments[i]);
					String name = msg.arguments[i+1];
					members.put(id, new Client(id, name));
				}
			}
		}
//		this.channelListeners = new RoomChannelListener();
//		channel.addChannelListener(channelListeners);
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	public Map<Integer, Client> getMembers()
	{
		return Collections.unmodifiableMap(members);
	}
	
	public boolean isPasswordProtected()
	{
		return password;
	}
	
	public void addChatroomListener(ChatroomListener l)
	{
		listeners.add(l);
	}

	public void removeChatroomListener(ChatroomListener l)
	{
		listeners.remove(l);
	}
	
	void fireMemberJoined(int id, String name)
	{
		Client client = new Client(id, name);
		members.put(client.id, client);
		
		if (listeners.size() == 0) return;
		for (ChatroomListener l : listeners)
		{
			l.memberJoined(this, client);
		}
	}
	
	void fireMemberLeaved(int id)
	{
		Client client = members.remove(id);
		
		if (client == null || listeners.size() == 0) return;
		for (ChatroomListener l : listeners)
		{
			l.memberLeaved(this, client);
		}
	}
	
	void fireMessageRecieved(int id, String text)
	{
		Client client = members.get(id);
		ChatMessage cm = new ChatMessage(client, System.currentTimeMillis(), text);
		lines.add(cm);
		
		if (listeners.size() == 0) return;
		for (ChatroomListener l : listeners)
		{
			l.messageRecieved(this, cm);
		}
	}
	
	/**
	 * Sendet einen Text zu dem angegebenen Klienten.
	 * Wenn der Klient <code>null</code> ist, wird die Nachicht an den Server gesendet.
	 * Handelt es sich um einen Befehl, interpretiert der Server diesen.
	 * Bei einer einfachen Nachicht wird diese für alle angezeigt.
	 * @param text Die Nachicht.
	 * @throws IOException Wenn die Nachicht nicht gesendet werden konnte.
	 */
	public void send(String text) throws IOException
	{
		channel.send(Channel.FLAG_CMD, "room.msg", name, text);
	}
	
//	public void dispose()
//	{
//		channel.removeChannelListener(channelListeners);
//	}
	
	public class ChatMessage
	{
		public Client client;
		public long time;
		public String text;
		
		ChatMessage(Client client, long time, String text)
		{
			this.client = client;
			this.time = time;
			this.text = text;
		}
	}
	
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
