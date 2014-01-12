package cuina.network;



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
	public static final int FLAG_BYTES		= 6;
	public static final int FLAG_TEXT		= 7;
	/** Gibt an, dass eine Netzwerk-ID angefordert wird. */
	public static final int FLAG_NETID		= 8;

	private int sender;
	private int type;
	private int reciever;
	private byte[] data;

	public Message(int sender, int reciever, int type, byte[] buffer)
	{
		this.sender = sender;
		this.type = type;
		this.reciever = reciever;
		this.data = buffer;
	}
	
//	public Message(int type, int reciever, byte[] buffer)
//	{
//		this.type = type;
//		this.reciever = reciever;
//		this.data = buffer;
//	}

	public int getType()
	{
		return type;
	}

	public int getSender()
	{
		return sender;
	}

	public int getReciever()
	{
		return reciever;
	}
	
	public NetID getSenderID()
	{
		return new NetID(sender);
	}

	public NetID getRecieverID()
	{
		return new NetID(reciever);
	}

	public byte[] getData()
	{
		return data;
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
