package cuina.network;

import java.util.Map;

public interface ChannelListener
{
	public void dataRecieved(Map<String, String> data);
	
	public void messageRecieved(int flag, byte[] data);
	
	public void closeRequested();
}
