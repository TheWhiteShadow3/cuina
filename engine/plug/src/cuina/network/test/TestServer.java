package cuina.network.test;

import cuina.network.Server;
import cuina.network.ServerClient;
import cuina.network.ServerSession;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class TestServer
{
	private static Server server;
	
	public static void main(String[] args) throws InterruptedException
	{
		server = new Server();
		server.init();
		
		Scanner scanner = new Scanner(System.in);
		while(server.isRunning())
		{
			try
			{
				String[] parts = scanner.nextLine().split(" ");
				switch(parts[0])
				{
					// Hilefebefehl. Beim erstellen weiterer Befehle bitte hier mit auflisten.
					case "help": System.out.println(
							"help        Zeigt diesen Text an.\n" +
							"state       Zeigt ewinen Status des Servers an.\n" +
							"kill        Entfernt einen Klienen ein Raum oder eine Session.\n" +
							"shutdown    Beendet den Server."
						); break;
							
					case "state": showServerstate(); break;
					case "shutdown": server.dispose(); break;
					case "kill": kill(parts); break;
					// Fehlerfall:
					default: System.err.println("Syntax Error! Der Befehl 'help' listet die möglichen Befehle auf.");
				}
			}
			catch(NoSuchElementException e)
			{
				System.out.println("Eingabe abgebrochen.");
				server.dispose();
			}
		}
		// Damit Deamon-Threads Zeit haben Fehlermeldungen zu schreiben.
		Thread.sleep(100);
	}

	private static void kill(String[] parts)
	{
		switch(parts[1])
		{
			case "client": server.getClients(); break;
			case "room": server.destroyChatroom(parts[2]); break;
			case "session": server.destroySession(parts[2]); break;
			default:
			{
				int id = Integer.parseInt(parts[2]);
				for(ServerClient client : server.getClients())
				{
					if (client.getID().get() == id)
					{
						client.close();
						System.out.println("Klient gekillt.");
						return;
					}
				}
//				for(ServerChatroom room : server.getChatrooms())
//				{
//					if (room.getID().get() == id)
//					{
//						room.close();
//						System.out.println("Client gekillt.");
//						return;
//					}
//				}
				for(ServerSession session : server.getSessions())
				{
					if (session.getID().get() == id)
					{
						session.close();
						System.out.println("Session gekillt.");
						return;
					}
				}
			}
		}
	}

	private static void showServerstate()
	{
		System.out.println("Status: " + (server.isRunning() ? "running" : "stopped"));
		System.out.println("Clients: " + server.getClients());
		System.out.println("Rooms: " + server.getChatrooms());
		System.out.println("Sessions: " + server.getSessions());
		System.out.println();
	}
}
