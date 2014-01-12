package cuina.network;

import java.io.IOException;
import java.nio.ByteBuffer;


public interface INetworkSession
{
	public static final int MESSAGE_OBJECT_CREATE	= 1;
	public static final int MESSAGE_OBJECT_UPDATE	= 2;
	public static final int MESSAGE_OBJECT_DISPOSE	= 3;
	
	public void sendData(ByteBuffer buffer) throws IOException;
	
	public void sendEvent(ByteBuffer buffer) throws IOException;
	
	public void sendMessage(Message msg) throws IOException;
	
	public boolean isOpen();
	
	public NetID getID();

	public void requestNetworkID(NetID netID) throws IOException;
}
