package cuina.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandMessage extends Message
{
	private static final char SEPERATOR = '|';
	
	public String command;
	public String[] arguments;
	
	public CommandMessage(Message msg)
	{
		super(msg.getType(), msg.getReciever(), msg.getData());
		
		readCommands(msg.getData());
	}
	
	public CommandMessage(int type, int reciever, String command, String... arguments)
	{
		super(type, reciever, toBytes(command, arguments));
		this.command = command;
		this.arguments = arguments;
	}

	public String getCommand()
	{
		return command;
	}

	public String getArgument(int idenx)
	{
		return arguments[idenx];
	}

	public NetworkException getException()
	{
		if (getType() == Channel.FLAG_EXCEPTION)
			return new NetworkException(command + ": " + arguments[0]);
		else
			return null;
	}
	
	public void checkException() throws NetworkException
	{
		NetworkException e = getException();
		if (e != null) throw e;
	}

	@Override
	public String toString()
	{
		return "Message [type=" + getType() + ", cmd=" + command + ", args=" + Arrays.toString(arguments) + "]";
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
