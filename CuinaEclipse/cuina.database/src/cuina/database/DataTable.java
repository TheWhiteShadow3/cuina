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
public class DataTable<E extends DatabaseObject> implements Serializable
{
	private static final long serialVersionUID = 4194326407322198560L;

	private String name;
	private Class<E> clazz;
	private transient Database db;
	/** Ein Workspace-Relativer Pfad zur Datei. */ 
	private transient String fileName;
	private final HashMap<String, E> data = new HashMap<String, E>();
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

	public String getFileName()
	{
		return fileName;
	}

	void setRuntimeMetaData(Database db, String fileName)
	{
		this.db = db;
		this.fileName = fileName;
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
//		treeOrder.remove(key);
	}

	/**
	 * Entfernt alle Objekte aus der Tabelle.
	 */
	public void clear()
	{
		data.clear();
//		treeOrder.clear();
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
//		treeOrder.put(key, path);
	}

	public Database getDatabase()
	{
		return db;
	}
	
	public DatabaseInput createDatabaseInput(String key)
	{
		return new DatabaseInput(this, key);
	}
	
//	/**
//	 * Erstellt einen Baum um die Objekte hirachisch anzuzeigen und zu
//	 * bearbeiten.
//	 * 
//	 * @return Wurzel-Element des Baums.
//	 */
//	public TreeGroup createTreeRoot()
//	{
//		return new TreeGroup(this, "");
//	}
	
//	ArrayList<TreeNode> createChildren(TreeGroup parent)
//	{
//		ArrayList<TreeNode> childList = new ArrayList<TreeNode>();
//		OrderLoop:
//		for (String key : treeOrder.keySet())
//		{
//			String path = treeOrder.get(key);
//			String parentPath = parent.getPath();
//			if (path != null)
//			{
//				TreeNode node = null;
//				if (path.equals(parentPath) && data.containsKey(key))
//				{
//					node = new TreeLeaf(this, key);
//				}
//				else if (path.length() > parentPath.length() &&
//						 path.lastIndexOf('/') <= parentPath.length() &&
//						 path.startsWith(parentPath))
//				{
//					for (int i = 0; i < childList.size(); i++)
//					{
//						if ( path.equals(childList.get(i).getPath()) ) continue OrderLoop;
//					}
//					
//					node = new TreeGroup(this, path);
//				}
//				
//				if (node != null)
//				{
//					node.setParent(parent);
//					childList.add(node);
//				}
//			}
//		}
//		return childList;
//	}

//	/**
//	 * Erstellt eine Gruppe für die Baumstruktur-Ansicht. Die Gruppe bleibt auch
//	 * erhalten, wenn keine Objekte mehr dort drin sind.
//	 * 
//	 * @param path
//	 *            Der Pfad.
//	 */
//	public void createTreeGroup(String path)
//	{
//		treeOrder.put('!' + path, path);
//	}
//
//	/**
//	 * Löscht eine Gruppe von aus der Baumstruktur-Ansicht, falls diese leer
//	 * ist. Nach dieser Operation muss der Baum erneuert werden.
//	 * 
//	 * @param path
//	 *            Der Pfad.
//	 */
//	public void removeTreeGroup(String path)
//	{
//		treeOrder.remove('!' + path);
//	}
//
//	public boolean canGroupBeEmpty(String path)
//	{
//		return treeOrder.containsKey('!' + path);
//	}

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
