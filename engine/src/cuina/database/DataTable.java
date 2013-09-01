package cuina.database;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * Eine Datenbank-Tabelle für statische Spielobjekte.
 * 
 * @author TheWhiteShadow
 */
public class DataTable<E extends DatabaseObject> implements Serializable
{
	private static final long serialVersionUID = 4194326407322198560L;

	private String name;
	private Class<E> clazz;
	private HashMap<String, E> data = new HashMap<String, E>();
//	private HashMap<String, String> treeOrder = new HashMap<String, String>();

	public DataTable(String name, Class<E> clazz)
	{
		this.name = name;
		this.clazz = clazz;
	}

	public Class<E> getElementClass()
	{
		return clazz;
	}

	public String getName()
	{
		return name;
	}

	void setName(String name)
	{
		this.name = name;
	}

	public void put(E obj)
	{
		nullCheck(obj);
		put(obj.getKey(), obj);
	}

//	public void put(String path, E obj)
//	{
//		nullCheck(obj);
//		put(path, obj.getKey(), obj);
//	}

	public void put(String key, E obj)
	{
		nullCheck(key);
		nullCheck(obj);
		if (data.containsKey(key)) throw new IllegalArgumentException("dublicate Key");

		obj.setKey(key);
		data.put(key, obj);
//		treeOrder.put(key, path);
	}
	
	public Set<String> keySet()
	{
		return data.keySet();
	}
	
	public Collection<E> values()
	{
		return data.values();
	}

//	/**
//	 * Gibt die Anordnung der Elemente im Baum für den Editor zurück.<br>
//	 * <i>Dieses Attribut ist für die Engine irrelevant.</i>
//	 * */
//	public HashMap<String, String> getTreeOrder()
//	{
//		return treeOrder;
//	}

	/**
	 * Aktuallisiert einen Eintrag, der zuvor in der Tabelle angelegt wurde und
	 * belegt ihn mit einem neuen Objekt. Der Schlüssel des Objekts wird dabei
	 * überschrieben.
	 * 
	 * @param key
	 *            Schlüssel des Objekts.
	 * @param obj
	 *            neues Objekt.
	 */
	public void update(String key, E obj)
	{
		nullCheck(key);
		if (!data.containsKey(key)) throw new NullPointerException("No entry with key: " + key);

		obj.setKey(key);
		data.put(key, obj);
	}

	/**
	 * Gibt das Objekt mit dem angegebenen Schlüssel zurück. Wenn der Schlüssel
	 * in der Tabelle nicht vorhanden ist, wird <code>null</code> zurückgegeben.
	 * 
	 * @param key
	 *            Schlüssel
	 * @return Das Objekt mit dem angegebenen Schlüssel.
	 */
	public E get(String key)
	{
		return data.get(key);
	}

	/**
	 * Entfernt das Objekt mit dem angegebenen Schlüssel aus der Tabelle.
	 * 
	 * @param key
	 *            Schlüssel
	 */
	public void remove(String key)
	{
		data.remove(key);
	}

	/**
	 * Entfernt alle Objekte aus der Tabelle.
	 */
	public void clear()
	{
		data.clear();
	}

	public boolean isEmpty()
	{
		return data.isEmpty();
	}

	public boolean containsKey(String key)
	{
		return data.containsKey(key);
	}

	public boolean containsValue(E value)
	{
		return data.containsValue(value);
	}

	/**
	 * Ändert den Pfad des Objekts innerhalb der Tabelle.
	 * 
	 * @param key
	 * @param path
	 */
	public void move(String key, String path)
	{
		if (!data.containsKey(key)) throw new NullPointerException("No entry with key: " + key);
	}

    private void nullCheck(Object value)
    {
        if (value == null) throw new NullPointerException("Key must not be null!");
    }
}
