package cuina.network;

public class NetworkTester
{
	private static Server server;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		server = new Server();
		server.init();
		
		Thread.sleep(10);
		
		int port = Server.PORT;
		Connection con = new Connection("localhost", port, "TWS");
		System.out.println("Verbunden? " + con.isConnected());
		
		Thread.sleep(100);
		
		con.close();
		server.dispose();
	}
}
