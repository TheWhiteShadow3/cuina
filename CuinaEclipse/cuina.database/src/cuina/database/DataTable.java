package cuina.database;

import cuina.database.internal.ReferenceValidator;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * Eine Datenbank-Tabelle für statische Spielobjekte.
 * 
 * @author TheWhiteShadow
 */
public class DataTable<E extends DatabaseObject> implements NamedItem, Serializable
{
	private static final long serialVersionUID = 4194326407322198560L;

	private String name;
	private Class<E> clazz;
	private transient Database db;
	/** Ein Workspace-Relativer Pfad zur Datei. */ 
	private transient String fileName;
	private final HashMap<String, E> data = new HashMap<String, E>();

	public DataTable(String name, Class<E> clazz)
	{
		this.name = name;
		this.clazz = clazz;
	}

	public Class<E> getElementClass()
	{
		return clazz;
	}

	public String getFileName()
	{
		return fileName;
	}

	void setRuntimeMetaData(Database db, String fileName)
	{
		this.db = db;
		this.fileName = fileName;
	}

	@Override
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
		put(obj.getKey(), obj);
	}
	public void put(String key, E obj)
	{
		nullCheck(key);

		obj.setKey(key);
		data.put(key, obj);
	}
	
	public Set<String> keySet()
	{
		return data.keySet();
	}
	
	public Collection<E> values()
	{
		return data.values();
	}
	
	public int size()
	{
		return data.size();
	}

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
	 * Ändert den Schlüssel eines Eintrags auf einen neuen Wert. Das assoziierte
	 * Objekt bleibt dabei erhalten.
	 * 
	 * @param oldKey
	 *            Alter Schlüssel.
	 * @param newKey
	 *            Neuer Schlüssel.
	 * @return Den Entgültigen Schlüssel, der für den neuen Eintrag vergeben wurde.
	 */
	public String changeKey(String oldKey, String newKey)
	{
		if (!data.containsKey(oldKey)) throw new NullPointerException("Entry '" + oldKey + "' not found!");
		
		String key = Database.getValidKey(newKey);
		if (oldKey.equals(key)) return key;
		
		if (data.containsKey(key))
		{
			String rawKey = key;
			int i = 2;
			{
				key = rawKey + Integer.toString(i++);
			}
			while (data.containsKey(key));
		}

		data.put(key, data.get(oldKey));
		remove(oldKey);
		
		if (db != null)
			ReferenceValidator.updateKey(db, name, oldKey, key, null);

		return key;
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

	public Database getDatabase()
	{
		return db;
	}
	
	public DatabaseInput createDatabaseInput(String key)
	{
		return new DatabaseInput(this, key);
	}

    private void nullCheck(Object value)
    {
        if (value == null) throw new NullPointerException("Key must not be null!");
    }
    
	public String createAviableKey(String key)
	{
		key = Database.getValidKey(key);
		
		int id = 0;
		String newKey = key;
		while (containsKey(newKey))
		{
			newKey = key + ++id;
		}
		return newKey;
	}
}
