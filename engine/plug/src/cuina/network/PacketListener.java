package cuina.network;

import cuina.network.core.NetID;

import java.nio.ByteBuffer;

public interface PacketListener
{
	public void packetRecieved(NetID reciever, ByteBuffer buffer);
}
