package cuina;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Ein Kontext definiert eine globale Ablage für Objekte mit einem bestimmten Gültigkeitsbereich.
 * @author TheWhiteShadow
 */
public final class Context implements Serializable
{
	private static final long serialVersionUID = 2165028956150205701L;
	
	/** Globaler Kontext. */
	public static final int GLOBAL	= 1;
	/** Session Kontext. */
	public static final int SESSION = 2;
	/** Szenen Kontext. */
	public static final int SCENE	= 3;
	
	private final Map<String, Object> data = new HashMap<String, Object>();
	private final int type;
	private final List<WeakReference<ContextListener>> listeners = new ArrayList<WeakReference<ContextListener>>();
	
	Context(int type)
	{
		this.type = type;
	}
	
	/**
	 * Fügt dem Kontext ein Objekt hinzu.
	 * @param key Referenz-Schlüssel, zur Identifizierung.
	 * @param value Das Objekt.
	 */
	public boolean set(String key, Object value)
	{
		Object old = data.get(key);
		if (Objects.equals(old, value)) return false;
		
		data.put(key, value);
		fireEntryChanged(old, value);
		return true;
	}
	
	/**
	 * Gibt ein Objekt aus dem Kontext zurück.
	 * @param key Referenz-Schlüssel, zur Identifizierung.
	 */
	public <T> T get(String key)
	{
		return (T) data.get(key);
	}
	
	/**
	 * Gibt eine nicht modifizierbare Liste aller Einträge im Kontext zurück.
	 * @return Liste aller Einträge im Kontext.
	 */
	public Map<String, Object> getData()
	{
		return Collections.unmodifiableMap(data);
	}

	/**
	 * Löscht alle Einträge im Kontext.
	 */
	public void clear()
	{
		for(int i = 0; i < listeners.size();)
		{
			ContextListener l = listeners.get(i).get();
			if (l == null)
			{
				listeners.remove(i);
			}
			else
			{
				l.contextClearing(this);
				i++;
			}
		}
		data.clear();
	}

	/**
	 * Gibt den Typ des Kontexts zurück.
	 * @return Der Kontext-Typ.
	 */
	public int getType()
	{
		return type;
	}
	
	/**
	 * Fügt dem Kontext einen Listener hinzu.
	 * Listener werden über Weak-Referenzen gehalten,
	 * somit werden auch nicht mehr referenzierte Listener automatisch aus dem Kontext entfernt.
	 * @param l Listener
	 */
	public void addContextListener(ContextListener l)
	{
		listeners.add(new WeakReference<ContextListener>(l));
	}
	
	/**
	 * Entfernt den Listener vom Kontext.
	 * Listener werden automatisch entfernt, wenn sie nicht mehr referenziert werden.
	 * @param l
	 */
	public void removeContextListener(ContextListener l)
	{
		for(int i = 0; i < listeners.size(); i++)
		{
			if (listeners.get(i).get() == l)
			{
				listeners.remove(i);
				return;
			}
		}
		throw new IllegalArgumentException("Listener is not in the list.");
	}
	
	private void fireEntryChanged(Object oldValue, Object newValue)
	{
		for(int i = 0; i < listeners.size();)
		{
			ContextListener l = listeners.get(i).get();
			if (l == null)
			{
				listeners.remove(i);
			}
			else
			{
				l.entryChanged(this, oldValue, newValue);
				i++;
			}
		}
	}

	void dispose()
	{
		for(int i = 0; i < listeners.size(); i++)
		{
			ContextListener l = listeners.get(i).get();
			if (l != null) l.contextDisposing(this);
		}
		data.clear();
		listeners.clear();
	}
}
