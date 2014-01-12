package cuina.network;

import java.io.IOException;

public interface NetworkContext
{
	public void requestNetworkID(NetID netID) throws IOException;
	
	public void send(Message msg) throws IOException;

	public NetID getID();

	public String getUsername();
}
