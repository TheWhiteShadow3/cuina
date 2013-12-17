package cuina.network;

public class Message
{
	public int flag;
	public byte[] data;
	
	public Message(int flag, byte[] data)
	{
		this.flag = flag;
		this.data = data;
	}
}
