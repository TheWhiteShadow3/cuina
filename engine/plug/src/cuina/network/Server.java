package cuina.network;

import cuina.plugin.ForGlobal;
import cuina.plugin.LifeCycleAdapter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

@ForGlobal(name="Server")
public class Server extends LifeCycleAdapter
{
	public static int PORT = 1234;
	private static final byte[] IDENTIFIER_SEQUENCE = Server.class.getName().getBytes();
	
	private static final Random RANDOM = new Random();
	
	private ConnectionListener listener;
	private ConnectionIdentifier identifier;
	
	private final Map<Integer, ServerClient> clients = new HashMap<Integer, ServerClient>();
	private final Map<String, NetworkSession> sessions = new HashMap<String, NetworkSession>();

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
	}

	public boolean disconnect(ServerClient client)
	{
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

	public NetworkSession createNetworkSession(String name, int port, int memberCount)
	{
		NetworkSession session = new NetworkSession(this, name, memberCount);
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
			int id = RANDOM.nextInt();
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
			while(!stop)
			{
				try
				{
					while(pendingClients.size() == 0) doSleep(100);
					
					Iterator<ServerClient> itr = pendingClients.iterator();
					while(itr.hasNext())
					{
						ServerClient client = itr.next();
						if (client.identify(null))
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
