package cuina.network;

public abstract class NetworkSession implements INetworkSession, ChannelListener
{
	private NetID netID;
	private String name;
//	private final List<SessionListener> listeners = new ArrayList<SessionListener>();
	
	public NetworkSession(NetID netID, String name)
	{
		this.netID = netID;
		this.name = name;
	}

	@Override
	public NetID getID()
	{
		return netID;
	}
	
	public String getName()
	{
		return name;
	}

	public void close()
	{
		
	}
	
	@Override
	public void channelClosed(Channel source)
	{
		close();
	}

	@Override
	public void messageRecieved(Channel source, Message msg)
	{
		if (msg.getType() == Message.FLAG_CMD)
		{
			handleCommand(new CommandMessage(msg));
		}
		else if (msg.getType() == Message.FLAG_INFO || msg.getType() == Message.FLAG_ACK)
		{
			handleInfo(new CommandMessage(msg));
		}
	}

	protected void handleInfo(CommandMessage msg)
	{
		switch(msg.getCommand())
		{
			case "close": close(); break;
		}
	}

	abstract protected void handleCommand(CommandMessage msg);
}
