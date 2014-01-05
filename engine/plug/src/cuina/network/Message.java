package cuina.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Message
{
	public int flag;
	public String command;
	public String[] arguments;
	
	public Message(int flag, byte[] buffer)
	{
		this.flag = flag;
		if (buffer == null) return;
		
		String messageString = new String(buffer);
		List<String> list = new ArrayList<String>();
		int s = 0;
		int e = 0;
		while(e != -1)
		{
			e = messageString.indexOf('|', s);
			String str = messageString.substring(s, (e != -1) ? e : messageString.length());
			if (command == null)
				command = str;
			else
				list.add(str);
			
			s = e+1;
		}
		arguments = list.toArray(new String[list.size()]);
	}
	
	public Message(int flag, String command, String[] arguments)
	{
		this.flag = flag;
		this.command = command;
		this.arguments = arguments;
	}

	public NetworkException getException()
	{
		if (flag == Channel.FLAG_EXCEPTION)
			return new NetworkException(command + ": " + arguments[0]);
		else
			return null;
	}
	
	public void checkException() throws NetworkException
	{
		NetworkException e = getException();
		if (e != null) throw e;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Message [flag=" + flag + ", cmd=" + command + ", args=" + Arrays.toString(arguments) + "]";
	}
}
