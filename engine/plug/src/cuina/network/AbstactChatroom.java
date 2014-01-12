package cuina.network;

import cuina.database.NamedItem;

public abstract class AbstactChatroom implements NamedItem, ChannelListener
{
	private NetID netID;
	private String name;
	private boolean password;

	public AbstactChatroom(NetID netID, String name)
	{
		this.netID = netID;
		this.name = name;
	}
	
	public abstract boolean isOpen();
	
	public abstract void close();
	
	public NetID getID()
	{
		return netID;
	}

	@Override
	public String getName()
	{
		return name;
	}
	
//	public Map<NetID, Client> getMembers()
//	{
//		return Collections.unmodifiableMap(members);
//	}
//	
//	public boolean isEmpty()
//	{
//		return members.isEmpty();
//	}
	
	public boolean isPasswordProtected()
	{
		return password;
	}
	
	@Override
	public void messageRecieved(Channel source, Message msg)
	{
		if (msg.getType() == Message.FLAG_CMD)
		{
			handleCommand(new CommandMessage(msg));
		}
	}
	
	protected abstract void addMember(NetID netID, String name);

	protected abstract void removeMember(NetID netID, boolean forced);

	protected abstract void messageRecieved(NetID netID, String text);

	@Override
	public void channelClosed(Channel source)
	{
		close();
	}

	public static class ChatMessage
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

	private void handleCommand(CommandMessage msg)
	{
		switch(msg.getCommand())
		{
			case "opened": msg.getArgument(0); break;
			case "joined": addMember(msg.getSender(), msg.getArgument(2)); break;
			case "leaved": removeMember(msg.getArgumentAsID(0), false); break;
			case "kicked": removeMember(msg.getArgumentAsID(0), true); break;
			case "msg": messageRecieved(msg.getArgumentAsID(0), msg.getArgument(1)); break;
		}
	}
}
