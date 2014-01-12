package cuina.network;


public interface ChannelListener
{
	public void messageRecieved(Message msg);
	public void channelClosed();
}
