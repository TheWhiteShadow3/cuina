package cuina.network;

import cuina.database.NamedItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstactChatroom implements NamedItem, ChannelListener
{
	private NetworkContext context;
	private int id;
	private String name;
	private boolean password;
	private String prefix;
	private final Map<Integer, Client> members = new HashMap<Integer, Client>();
	private List<ChatMessage> lines = new ArrayList<ChatMessage>();

	public AbstactChatroom(String username, NetworkContext context)
	{
		this.context = context;
		switch(msg.command.substring(prefix.length()+1))
		{
			case "opened":
			case "joined":
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
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	protected Channel getChannel()
	{
		return channel;
	}
	
	public Map<Integer, Client> getMembers()
	{
		return Collections.unmodifiableMap(members);
	}
	
	public boolean isPasswordProtected()
	{
		return password;
	}
	
	void addMember(int id, String name)
	{
		Client client = new Client(id, name);
		members.put(client.id, client);
		fireMemberJoined(client);
	}
	
	void removeMember(int id, boolean forced)
	{
		Client client = members.remove(id);
		fireMemberLeaved(client, forced);
	}
	
	void messageRecieved(int id, String text)
	{
		Client client = members.get(id);
		ChatMessage cm = new ChatMessage(client, System.currentTimeMillis(), text);
		lines.add(cm);
		
		fireMessageRecieved(client, cm);
	}
	
	protected void fireMemberJoined(Client client) {}
	
	protected void fireMemberLeaved(Client client, boolean forced) {}
	
	protected void fireMessageRecieved(Client client, ChatMessage cm) {}
	
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
		send(Message.FLAG_CMD, "msg", name, text);
	}
	
	public void send(int flag, String command, String... arguments) throws IOException
	{
		channel.send(flag, prefix + '.' + command, arguments);
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

	@Override
	public void messageRecieved(Message msg)
	{
		if (msg.getType() == Message.FLAG_CMD)
		{
			handleCommand(new CommandMessage(msg));
		}
	}

	private void handleCommand(CommandMessage msg)
	{
		switch(msg.getCommand())
		{
			case "opened": msg.getArgument(0)
		}
	}
}
