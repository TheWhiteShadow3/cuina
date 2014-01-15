package cuina.network;

import cuina.network.core.Message;
import cuina.network.core.NetID;

import java.io.IOException;


public interface INetworkSession
{
	public static final int MESSAGE_OBJECT_CREATE	= 1;
	public static final int MESSAGE_OBJECT_UPDATE	= 2;
	public static final int MESSAGE_OBJECT_DISPOSE	= 3;
	
	public void sendData(byte[] data) throws IOException;
	
	public void sendEvent(byte[] data) throws IOException;
	
	public void sendMessage(Message msg) throws IOException;
	
	public boolean isOpen();
	
	public void close();
	
	public NetID getID();

	public void requestNetworkID(NetID netID) throws IOException;
}
