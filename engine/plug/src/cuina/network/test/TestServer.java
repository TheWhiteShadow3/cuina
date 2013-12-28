package cuina.network.test;

import cuina.network.server.Server;

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
				switch(scanner.nextLine())
				{
					// Hilefebefehl. Beim erstellen weiterer Befehle bitte hier mit auflisten.
					case "help": System.out.println(
							"help        Zeigt diesen Text an.\n" +
							"state       Zeigt ewinen Status des Servers an.\n" +
							"shutdown    Beendet den Server."
						); break;
							
					case "state": showServerstate(); break;
					case "shutdown": server.dispose(); break;
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

	private static void showServerstate()
	{
		System.out.println(server.isRunning() ? "running" : "stopped");
		System.out.println("Clients: " + server.getClients());
		System.out.println("Rooms: " + server.getChatrooms());
		System.out.println("Sessions: " + server.getSessions());
		System.out.println();
	}
}
