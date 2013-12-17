package cuina.network;

import java.util.ArrayList;
import java.util.List;

public class Chatroom
{
	private String name;
	private final List<Client> members = new ArrayList<Client>();
	
	public Chatroom(String name)
	{
		this.name = name;
	}
	
	/**
	 * Sendet einen Text zu dem angegebenen Klienten.
	 * Wenn der Klient <code>null</code> ist, wird die Nachicht an den Server gesendet.
	 * Handelt es sich um einen Befehl, interpretiert der Server diesen.
	 * Bei einer einfachen Nachicht wird diese für alle angezeigt.
	 * @param client Klient, für PNs oder <code>null</code> für Servernachichten.
	 * @param text Die Nachicht.
	 */
	public void send(Client client, String text)
	{
		
	}
}
