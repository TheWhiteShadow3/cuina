package cuina.event;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Ein Ereignis für Trigger.
 * @author TheWhiteShadow
 */
public final class Event implements Serializable
{
	private static final long serialVersionUID = -2153248452225951248L;
	
	public static final Event ALWAYS;
	public static final Event NEVER;
	
	private static final HashMap<String, Event> CACHE;
	private static int lastID;
	
	static
	{	// Zuerst brauchen wir einen Cache.
		CACHE = new HashMap<String, Event>();
		
		ALWAYS = getEvent("always");
		NEVER = getEvent("never");
	}
	
	private String name;
	// Die ID ist zur Identifizierung des Objekts.
	private /*final*/ transient int id;
	
	public static Event getEvent(String name)
	{
		if (name == null) throw new NullPointerException();
		
		Event event = CACHE.get(name);
		if (event == null)
		{
			event = new Event(name);
		}
		return event;
	}
	
	private Event(String name)
	{
		this.name = name;
		addToCache();
	}
	
	private void addToCache()
	{
		CACHE.put(name, this);
		id = ++lastID;
	}

	public String getName()
	{
		return name;
	}
	
	@Override
	public int hashCode()
	{
		return id;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj instanceof Event)
			return (id == ((Event) obj).id);
		return false;
	}

	@Override
	public String toString()
	{
		return "Event: " + name;
	}
	
	private final synchronized void writeObject(ObjectOutputStream s) throws IOException
	{
		s.writeObject(name);
	}
	
	private final synchronized void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException
	{
		name = (String) s.readObject();
		Event cachedEvent = CACHE.get(name);
		if (cachedEvent == null)
			addToCache();
		else
			id = cachedEvent.id;
	}
}