package cuina.network.core;

import cuina.network.NetworkException;
import cuina.network.StreamUtils;



public class Message
{
	/** Gibt an, dass die Verbindung beendet wurde. */
	public static final int FLAG_EOF		= -1;
	/** Gibt an, dass die Nachicht leer ist. */
	public static final int FLAG_EMPTY		= 0;
	/** Gibt an, dass die Nachicht eine Rückmeldung ist. */
	public static final int FLAG_ACK		= 1;
	/** Gibt an, dass die Verbindung beendet werden soll. */
	public static final int FLAG_INFO		= 2;
	/** Gibt an, dass die Verbindung beendet werden soll. */
	public static final int FLAG_CLOSE		= 3;
	public static final int FLAG_CMD		= 4;
	public static final int FLAG_EXCEPTION	= 5;
	public static final int FLAG_TEXT		= 6;
	public static final int FLAG_DATA		= 7;
	public static final int FLAG_EVENT		= 8;
	/** Gibt an, dass eine Netzwerk-ID angefordert wird. */
	public static final int FLAG_NETID		= 9;
	public static final int FLAG_LOGIN		= 10;

	NetID sender;
	private NetID reciever;
	private int type;
	private byte[] data;

	public Message(Message msg)
	{
		this.sender = msg.sender;
		this.reciever = msg.reciever;
		this.type = msg.type;
		this.data = msg.data;
	}
	
	Message(NetID sender, NetID reciever, int type, byte[] buffer)
	{
		this.sender = sender;
		this.reciever = reciever;
		this.type = type;
		this.data = buffer;
	}
	
	public Message(NetID reciever, int type, byte[] data)
	{
		this.reciever = reciever;
		this.type = type;
		this.data = data;
	}

	public int getType()
	{
		return type;
	}

	public NetID getSender()
	{
		return sender;
	}

	public NetID getReciever()
	{
		return reciever;
	}

	public byte[] getData()
	{
		return data;
	}
	
	public void checkException() throws NetworkException
	{
		if (type == FLAG_EXCEPTION)
			throw new NetworkException(new String(data, StreamUtils.CHARSET));
	}
	
	@Override
	public String toString()
	{
		return "Message [type=" + getType() +
				", sender=" + sender +
				", reciever=" + reciever +
				", data=" + new String(data) + "]";
	}
}
