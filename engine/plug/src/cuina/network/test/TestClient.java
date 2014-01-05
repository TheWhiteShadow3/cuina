package cuina.network.test;

import cuina.network.Chatroom;
import cuina.network.ChatroomListener;
import cuina.network.Client;
import cuina.network.Connection;
import cuina.network.ConnectionListener;
import cuina.network.NetworkSession;
import cuina.network.server.Server;

import java.io.IOException;


public class TestClient
{
	public static void main(String[] args) throws Exception
	{
		int port = Server.PORT;
		Connection con = new Connection("localhost", port, "TWS", null);
		con.addConnectionListener(new MyConnectionListener());
		System.out.println("[TestClient] Trete Chatroom bei.");
		con.joinChatroom("blub", null);
		// 10 Sekunden um zu reagieren bis Timeout erfolgt (Serverstatus prüfen, Zweiten Klienten starten, etc.)
		Thread.sleep(10000);
		
		con.close();
		
		// Warte auf erfolgreiches Verbindungsende vom Klienten.
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
				public void memberLeaved(Chatroom room, Client client, boolean forced)
				{}
			});
			try
			{
				System.out.println("[TestClient] Sende Nachicht");
				room.send("Eine Nachicht.");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void roomLeaved(Chatroom room, boolean forced)
		{
			if (this.room == room) this.room = null;
		}
		
		@Override
		public void sessionJoined(NetworkSession session, Client client) {}

		@Override
		public void sessionLeaved(NetworkSession session, Client client) {}
	}
}