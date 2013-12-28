package cuina.network;

import cuina.network.server.Server;

import java.io.IOException;


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
		
		Thread.sleep(5);
		
		int port = Server.PORT;
		Connection con = new Connection("localhost", port, "TWS", null);
		con.addConnectionListener(new MyConnectionListener());
		System.out.println("[NetworkTester] Trete Chatroom bei.");
		con.joinChatroom("blub", null);
		Thread.sleep(100);
		
		con.close();
		
		// Warte auf erfolgreiches Verbindungsende vom Klienten.
		Thread.sleep(100);
		
		server.dispose();
		
		// Damit Deamon-Threads Zeit haben Fehlermeldungen zu schreiben.
		Thread.sleep(100);
	}
	
	private static class MyConnectionListener implements ConnectionListener
	{
		public NetworkSession session;
		public Chatroom room;

		@Override
		public void disconnected()
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sessionCreated(NetworkSession session)
		{
			this.session = session;
		}

		@Override
		public void sessionDestroyed(NetworkSession session)
		{
			if (this.session == session) this.session = null;
		}

		@Override
		public void roomJoined(Chatroom room)
		{
			this.room = room;
			room.addChatroomListener(new ChatroomListener()
			{
				@Override
				public void messageRecieved(Chatroom room, Chatroom.ChatMessage message)
				{
					System.out.println("Nachicht von " + message.client.getName() + ": " + message.text);
				}

				@Override
				public void memberJoined(Chatroom room, Client client)
				{}

				@Override
				public void memberLeaved(Chatroom room, Client client)
				{}
			});
			try
			{
				System.out.println("[NetworkTester] Sende Nachicht");
				room.send("Eine Nachicht.");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void roomLeaved(Chatroom room)
		{
			if (this.room == room) this.room = null;
		}
		
		@Override
		public void sessionJoined(NetworkSession session, Client client) {}

		@Override
		public void sessionLeaved(NetworkSession session, Client client) {}
	}
}
