package cuina.network;


public interface ChannelListener
{
	public void messageRecieved(Channel source, Message msg);
	public void channelClosed(Channel source);
}
