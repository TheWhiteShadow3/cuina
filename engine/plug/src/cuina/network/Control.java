package cuina.network;

import cuina.network.core.NetID;

import java.nio.ByteBuffer;

public interface Control
{
	public NetID getID();
	
	public void recieveData(ByteBuffer data);
}
