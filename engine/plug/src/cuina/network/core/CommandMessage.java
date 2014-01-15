package cuina.network.core;

import cuina.network.StreamUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandMessage extends Message
{
	private static final char SEPERATOR = '|';
	
	private String command;
	private String[] arguments;
	
	public CommandMessage(Message msg)
	{
		super(msg);
		
		readCommands(msg.getData());
	}
	
	public CommandMessage(NetID reciever, int type, String command, String... arguments)
	{
		super(reciever, type, toBytes(command, arguments));
		this.command = command;
		this.arguments = arguments;
	}

	public String getCommand()
	{
		return command;
	}
	
	public int getArgumentCount()
	{
		return arguments.length;
	}

	public String getArgument(int index)
	{
		return arguments[index];
	}
	
	public int getArgumentAsInt(int index)
	{
		return Integer.parseInt(arguments[index]);
	}
	
	public NetID getArgumentAsID(int index)
	{
		return new NetID(getArgumentAsInt(index));
	}

	@Override
	public String toString()
	{
		return "Message [type=" + getType() +
				", sender=" + getSender() +
				", reciever=" + getReciever() +
				", cmd=" + command +
				", args=" + Arrays.toString(arguments) + "]";
	}
	
	private void readCommands(byte[] buffer)
	{
		String messageString = new String(buffer);
		List<String> list = new ArrayList<String>();
		int s = 0;
		int e = 0;
		while(e != -1)
		{
			e = messageString.indexOf(SEPERATOR, s);
			String str = messageString.substring(s, (e != -1) ? e : messageString.length());
			if (command == null)
				command = str;
			else
				list.add(str);
			
			s = e+1;
		}
		arguments = list.toArray(new String[list.size()]);
	}
	
	private static byte[] toBytes(String command, String[] arguments)
	{
		StringBuilder builder = new StringBuilder(8 + arguments.length * 8);
		builder.append(command);
		for(int i = 0; i < arguments.length; i++)
			builder.append(SEPERATOR).append(arguments[i]);
		return builder.toString().getBytes(StreamUtils.CHARSET);
	}
}
