package cuina.network;

import java.io.IOException;

public final class NetID
{
	int id = -1;
	
	/**
	 * Erzeugt eine neue Netzwerk-ID.
	 * <p>
	 * Da die ID erst beim Server registriert werden muss, gibt {@link #get()} anfangs noch keine nWert zurück.
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
	
//	/**
//	 * Erzeugt eine Netzwerk-ID mit der angegebenen ID-Nummer.
//	 * <p>
//	 * <b>Warnung!</b>
//	 * <i>Die erzeugte Netzwerk-ID ist möglicherweise nicht im Netzwerk registriert.</i>
//	 * </p>
//	 * @param id Die ID
//	 * @return Die neue Netzwerk-ID.
//	 * @see #NetID(NetworkContext)
//	 */
//	public static NetID unsaveNewID(int id)
//	{
//		return new NetID(id);
//	}
	
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
