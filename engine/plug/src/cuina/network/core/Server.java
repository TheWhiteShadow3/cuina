package cuina.network.core;

import cuina.network.ConnectionSecurityPolicy;
import cuina.network.NetworkException;
import cuina.network.ServerChatroom;
import cuina.network.ServerSession;
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
import java.util.concurrent.ConcurrentLinkedQueue;

@ForGlobal(name="Server")
public class Server extends LifeCycleAdapter
{
	public static final int PORT;
	static
	{
		int port = 1234;
//		try
//		{
//			port = Integer.parseInt(Game.getIni().get("Server", "Port"));
//			if (Boolean.getBoolean("server"))
//			{
//				InjectionManager.addObject(new Server(), "Server", Context.GLOBAL, Scene.ALL_SCENES);
//			}
//		}
//		catch(NumberFormatException e) {}
		PORT = port;
	}
	
//	private static final byte[] IDENTIFIER_SEQUENCE = Server.class.getName().getBytes();
	
//	private static final Random RANDOM = new Random();
	
	private int netIDPosition = 1;
	private ConnectionListener listener;
	private ConnectionIdentifier identifier;
	
	private final Map<NetID, ServerClient> clients = new HashMap<NetID, ServerClient>();
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

	public synchronized NetID generateNetworkID()
	{
		if (netIDPosition == 0) throw new OutOfMemoryError("No more network-ids available.");
		return new NetID(netIDPosition++);
	}
	
	public synchronized void requestNetworkID(NetID netID)
	{
		if (netIDPosition == 0) throw new OutOfMemoryError("No more network-ids available.");
		netID.id = netIDPosition++;
	}
	
	public boolean isRunning()
	{
		return !(listener.stop || identifier.stop);
	}
	
	public ServerClient getClient(NetID netID)
	{
		return clients.get(netID);
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
		System.out.println("[Server] " + client.getUsername() + " ist disconnectet.");
		if (clients.remove(client.getID()) != null)
		{
			for(ServerChatroom room : rooms.values()) room.leave(client);
			for(ServerSession session : sessions.values()) session.leave(client);
			return true;
		}
		else
		{
			identifier.removePendingClient(client);
			return false;
		}
	}

	public ServerChatroom getChatroom(String name)
	{
		return rooms.get(name);
	}

	public ServerSession getSession(String sessionName)
	{
		return sessions.get(sessionName);
	}
	
	public ServerChatroom getOrCreateChatroom(String roomName) throws IOException
	{
		ServerChatroom room = rooms.get(roomName);
		if (room != null) return room;
		
		NetID netID = generateNetworkID();
		room = new ServerChatroom(netID, roomName, this);
		rooms.put(roomName, room);
		System.out.println("[Server] Raum " + roomName + ", " + netID + " erstellt.");
		return room;
	}

	public boolean createNetworkSession(String sessionName, ServerClient owner) throws IOException
	{
		ServerSession session = sessions.get(sessionName);
		if (session != null) return false;
		
		NetID netID = generateNetworkID();
		session = new ServerSession(netID, sessionName, this, owner);
		sessions.put(sessionName, session);
		System.out.println("[Server] Session " + sessionName + ", " + netID + " erstellt.");
		return true;
	}
	
	public void destroyChatroom(ServerChatroom room)
	{
		System.out.println("[Server] Raum " + room.getName() + " zerstört.");
		rooms.remove(room.getName());
	}

	public void destroySession(ServerSession session)
	{
		System.out.println("[Server] Session " + session.getName() + " zerstört.");
		sessions.remove(session.getName());
	}

	public void sendException(NetID clientID, String message)
	{
		ServerClient client = getClient(clientID);
		if (client == null) return;
		
		try
		{
			client.getChannel().send(new NetworkException(message));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
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
//			int id;
//			do
//			{
//				id = RANDOM.nextInt();
//			}
//			while(server.clients.get(id) != null);
			NetID netID = server.generateNetworkID();
			ServerClient client = new ServerClient(netID, server, socket);
//			OutputStream out = client.getOutputStream();
//			out.write(IDENTIFIER_SEQUENCE);
//			out.write(0);
//			// Wir brauchen nur die letzen Zahlen für einen Abgleich. Das deckt gut einen Monat ab.
//			out.write(StreamUtil.intToByteArray((int) System.currentTimeMillis()));
//			out.write(StreamUtil.intToByteArray(id));
//			out.write(255);
//			out.flush();
			
			server.identifier.addPendingClient(client);
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
		
		private void addPendingClient(ServerClient client)
		{
			pendingClients.add(client);
		}
		
		private void removePendingClient(ServerClient client)
		{
			pendingClients.remove(client);
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
						if (client.accepted())
						{
							registClient(client);
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
		
		private void registClient(ServerClient client)
		{
			System.out.println("[Server] registriere Client (" + client.getID() + ") " + client.getUsername());
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
