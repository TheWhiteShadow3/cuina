package cuina.network;


public interface ConnectionListener
{
	public void connected();
	
	public void disconnected();
	
	public void sessionCreated(INetworkSession session);
	
	public void sessionDestroyed(INetworkSession session);
	
	public void sessionJoined(INetworkSession session, Client client);
	
	public void sessionLeaved(INetworkSession session, Client client);
	
	public void roomJoined(Chatroom room);
	
	public void roomLeaved(Chatroom room, boolean forced);
}
