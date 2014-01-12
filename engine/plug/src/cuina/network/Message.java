package cuina.network;

import java.util.Arrays;


public class Message
{
	private int type;
	private int reciever;
	private byte[] data;
	
	public Message(int type, int reciever, byte[] buffer)
	{
		this.type = type;
		this.reciever = reciever;
		this.data = buffer;
	}

	public int getType()
	{
		return type;
	}

	public int getReciever()
	{
		return reciever;
	}

	public byte[] getData()
	{
		return data;
	}
	
	@Override
	public String toString()
	{
		return "Message [type=" + getType() + ", reciever=" + reciever + ", data=" + Arrays.toString(data) + "]";
	}
}
