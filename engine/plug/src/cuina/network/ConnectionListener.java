package cuina.network;


public interface ConnectionListener
{
	public void connected();
	
	public void disconnected();
	
	public void sessionCreated(NetworkSession session);
	
	public void sessionDestroyed(NetworkSession session);
	
	public void sessionJoined(NetworkSession session, Client client);
	
	public void sessionLeaved(NetworkSession session, Client client);
	
	public void roomJoined(Chatroom room);
	
	public void roomLeaved(Chatroom room, boolean forced);
}
