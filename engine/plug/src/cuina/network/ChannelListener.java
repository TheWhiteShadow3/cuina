package cuina.network;

import cuina.network.core.Message;


public interface ChannelListener
{
	public void messageRecieved(Object source, Message msg);
	public void channelClosed(Object source);
}
