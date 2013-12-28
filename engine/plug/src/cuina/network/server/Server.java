package cuina.network.server;

import cuina.network.ConnectionSecurityPolicy;
import cuina.plugin.ForGlobal;
import cuina.plugin.LifeCycleAdapter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

@ForGlobal(name="Server")
public class Server extends LifeCycleAdapter
{
	public static final int PORT = 1234;
//	static
//	{
//		int port = 1234;
//		try
//		{
//			port = Integer.parseInt(Game.getIni().get("Server", "Port"));
//		}
//		catch(NumberFormatException e) {}
//		PORT = port;
//	}
	
//	private static final byte[] IDENTIFIER_SEQUENCE = Server.class.getName().getBytes();
	
	private static final Random RANDOM = new Random();
	
	private ConnectionListener listener;
	private ConnectionIdentifier identifier;
	
	private final Map<Integer, ServerClient> clients = new HashMap<Integer, ServerClient>();
	private final Map<String, ServerChatroom> rooms = new HashMap<String, ServerChatroom>();
	private final Map<String, ServerSession> sessions = new HashMap<String, ServerSession>();

	public Server()
	{
		this.listener = new ConnectionListener(this, PORT);
		this.identifier = new ConnectionIdentifier(this);
	}

	@Override
	public void init()
	{
		listener.start();
		identifier.start();
		System.out.println("[Server] started on port " + PORT);
	}

	public boolean isRunning()
	{
		return !(listener.stop || identifier.stop);
	}
	
	public ServerClient getClient(int id)
	{
		return clients.get(id);
	}

	public ConnectionSecurityPolicy getSecurityPolicy()
	{
		return null;
	}

	public List<ServerClient> getClients()
	{
		return new ArrayList(clients.values());
	}
	
	public List<ServerChatroom> getChatrooms()
	{
		return new ArrayList(rooms.values());
	}
	
	public List<ServerSession> getSessions()
	{
		return new ArrayList(sessions.values());
	}

	boolean disconnect(ServerClient client)
	{
		System.out.println("[Server] Disconnect von " + client.getName());
		for(ServerChatroom room : rooms.values()) room.leave(client);
		for(ServerSession session : sessions.values()) session.leave(client);
		return clients.remove(client.getID()) != null;
	}

	@Override
	public void dispose()
	{
		identifier.stop = true;
		listener.stop = true;
		identifier.interrupt();
		listener.interrupt();
		System.out.println("[Server] closed");
	}
	
	public ServerChatroom getChatroom(String name)
	{
		return rooms.get(name);
	}
	
	public boolean createChatroom(ServerClient client, String name) throws IOException
	{
		ServerChatroom room = rooms.get(name);
		if (room != null) return false;
		
		room = new ServerChatroom(this, client, name);
		rooms.put(name, room);
		return true;
	}
	
	public boolean destroyChatroom(String name)
	{
		return rooms.remove(name) != null;
	}

	public ServerSession createNetworkSession(ServerClient owner, String name, int port, int memberCount)
	{
		ServerSession session = new ServerSession(this, name, memberCount);
		sessions.put(name, session);
		return session;
	}
	
	static class ConnectionListener extends Thread
	{
		private Server server;
		private boolean stop;
		private ServerSocket serverSocket;
		
		public ConnectionListener(Server server, int port)
		{
			this.server = server;
			setDaemon(true);
			try
			{
				serverSocket = new ServerSocket(port);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		@Override
		public void run()
		{
			stop = false;
			while(!stop)
			{
				try
				{
					Socket socket = serverSocket.accept();
					addPendingClient(socket);
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
				try
				{
					Thread.sleep(20);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		private void addPendingClient(Socket socket) throws IOException
		{
			int id;
			do
			{
				id = RANDOM.nextInt();
			}
			while(server.clients.get(id) != null);
			ServerClient client = new ServerClient(server, socket, id);
//			OutputStream out = client.getOutputStream();
//			out.write(IDENTIFIER_SEQUENCE);
//			out.write(0);
//			// Wir brauchen nur die letzen Zahlen für einen Abgleich. Das deckt gut einen Monat ab.
//			out.write(StreamUtil.intToByteArray((int) System.currentTimeMillis()));
//			out.write(StreamUtil.intToByteArray(id));
//			out.write(255);
//			out.flush();
			
			server.identifier.pendingClients.add(client);
		}
	}
	
	static class ConnectionIdentifier extends Thread
	{
		private Server server;
		private boolean stop;
		private Queue<ServerClient> pendingClients = new ConcurrentLinkedQueue<ServerClient>();
		
		public ConnectionIdentifier(Server server)
		{
			this.server = server;
			setDaemon(true);
		}

		@Override
		public void run()
		{
			stop = false;
			while(!stop)
			{
				try
				{
					while(pendingClients.size() == 0) doSleep(100);
					
					Iterator<ServerClient> itr = pendingClients.iterator();
					while(itr.hasNext())
					{
						ServerClient client = itr.next();
						if (client.identify())
						{
							addClient(client);
							itr.remove();
						}
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			// Schließe alle wartenden Klienten.
			for (ServerClient client : pendingClients)
			{
				client.close();
			}
		}
			
		private void doSleep(long millis)
		{
			try { Thread.sleep(millis); } catch(InterruptedException e) {}
		}
		
		private void addClient(ServerClient client)
		{
			System.out.println("[Server] registriere Client (" + client.getID() + ") " + client.getName());
			server.clients.put(client.getID(), client);
		}
		
//		private boolean identifyClient(ServerClient client) throws IOException
//		{
//			if (client.in.available() > 0)
//			{
//				byte[] buffer = new byte[256];
//				client.in.read(buffer);
//				
//				String name = StreamUtil.readString(buffer, 0);
//				if (name == null || name.length() < 2 || name.length() > 64) return false;
//				
//				client.setName(name);
//				return true;
//			}
//			return false;
//		}
	}
}
