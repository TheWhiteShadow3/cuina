package cuina.network.core;

import cuina.network.NetworkContext;

import java.io.IOException;

/**
 * Eine im Netzwerk eindeutige Identifikationsnummer.
 * <p>
 * Die Nummern symbolisieren eine Zugehörigkeit von Klient zu Server.
 * Wird eine Nachicht zu einer bestimmten Netzwerk-ID gesendet ist sicher gestellt,
 * dass nur der <code>ChannelListener</code> mit dieser ID auf der anderen Seite die Nachicht empfängt.
 * Ausnahme bildet die globale Netzwerk-ID {@link #GLOBAL_ID}.
 * </p>
 * @author TheWhiteShadow
 */
public final class NetID
{
	/**
	 * Eine Leere Netzwerk-ID. Kann benutzt werden um Null-Referenzen zu vermeiden.
	 * Ein Aufruf von {@link #isSet()} gibt <code>false</code> zurück.
	 */
	public static final NetID EMPTY_ID		= new NetID(-1);
	/**
	 * Die globale Netzwerk-ID definiert keine bestimmte Zuweisung.
	 * Diese ID wird niemals vom Server vergeben.
	 */
	public static final NetID GLOBAL_ID		= new NetID(0);
	
	int id = -1;
	
	/**
	 * Erzeugt eine neue Netzwerk-ID.
	 * <p>
	 * Da die ID erst beim Server registriert werden muss,
	 * gibt {@link #isSet()} anfangs noch <code>false</code> zurück und die Bedingung
	 * <pre>netID.equals(EMPTY_ID)</pre>
	 * ist erfüllt.
	 * </p>
	 * <i>Die ID ist eindeutig im Netzwerk.</i>
	 * 
	 * @param context Der Netzwerk-Kontext, in dem die ID erstellt wird.
	 * @throws IOException
	 * @see #get()
	 * @see #isSet()
	 */
	public NetID(NetworkContext context) throws IOException
	{
		context.requestNetworkID(this);
	}
	
	NetID(int id)
	{
		this.id = id;
	}
	
	/**
	 * Gibt die ID zurück.
	 * <p>
	 * Da es sich um eine Netzwerk-ID handelt, kann es sein, dass sie noch nicht verfügbar ist.
	 * In diesem Fall wird <code>-1</code> zurück gegeben.
	 * Ob die ID gesetzt ist, kann man über {@link #isSet()} prüfen.
	 * </p>
	 * <i>Die ID ist eindeutig im Netzwerk.</i>
	 * @return Die ID.
	 */
	public int get()
	{
		return id;
	}

	/**
	 * Gibt an, ob dis Netzwerk-ID gesetzt ist.
	 * @return <code>true</code>, wenn die ID gesetzt ist, andernfalls <code>false</code>.
	 * @see #get()
	 */
	public boolean isSet()
	{
		return id != -1;
	}
	
	@Override
	public int hashCode()
	{
		return id;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof NetID)
			return ((NetID) obj).id == id;
		return false;
	}

	@Override
	public String toString()
	{
		return "NetID " + id;
	}
}
