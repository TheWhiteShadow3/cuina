package cuina.network;

import java.util.ArrayList;
import java.util.List;

public class Chatroom extends AbstactChatroom
{
	private final List<ChatroomListener> listeners = new ArrayList<ChatroomListener>();

	public Chatroom(Channel channel, String username, Message msg)
	{
		super(channel, username, msg, "room");
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
	protected void fireMemberJoined(Client client)
	{
		if (listeners.size() == 0) return;
		for (ChatroomListener l : listeners)
		{
			l.memberJoined(this, client);
		}
	}
	
	@Override
	protected void fireMemberLeaved(Client client, boolean forced)
	{
		if (client == null || listeners.size() == 0) return;
		for (ChatroomListener l : listeners)
		{
			l.memberLeaved(this, client, forced);
		}
	}
	
	@Override
	protected void fireMessageRecieved(Client client, ChatMessage cm)
	{
		if (listeners.size() == 0) return;
		for (ChatroomListener l : listeners)
		{
			l.messageRecieved(this, cm);
		}
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
