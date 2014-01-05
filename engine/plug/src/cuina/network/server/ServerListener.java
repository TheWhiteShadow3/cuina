package cuina.network.server;


public interface ServerListener
{
	public void clientConnected(ServerEvent event);
	
	public void clientDisconnected(ServerEvent event);
	
	public void sessionCreated(ServerEvent event);
	
	public void sessionDestroyed(ServerEvent event);
	
	public void sessionJoined(ServerEvent event);
	
	public void sessionLeaved(ServerEvent event);
	
	public void roomCreated(ServerEvent event);
	
	public void roomDestroyed(ServerEvent event);
	
	public void roomJoined(ServerEvent event);
	
	public void roomLeaved(ServerEvent event);
}
