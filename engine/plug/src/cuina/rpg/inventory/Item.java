package cuina.rpg.inventory;

import cuina.database.DatabaseObject;
import cuina.database.NamedItem;
import cuina.plugin.Upgradeable;

import java.util.HashMap;
import java.util.Set;


/**
 * Stellt ein Menü-Item da. 
 * 
 * @author cuina team
 * 
 */
public class Item implements DatabaseObject, NamedItem, Upgradeable
{
	public static final String ITEM_DB = "Item";
	
	private static final long	serialVersionUID	= 60575991296401894L;
	
	private String key;
	public String icon;
	public String name;
	public String description;
	public final HashMap<String, Object> extensions = new HashMap<String, Object>(4);

	public Item() {}
	
	public Item(String key, String name, String icon, String description)
	{
		this.key = key;
		this.name = name;
		this.icon = icon;
		this.description = description;
	}
	
	@Override
	public void setKey(String key)
	{
		this.key = key;
	}

	@Override
	public String getKey()
	{
		return key;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public Object getExtension(String key)
	{
		return extensions.get(key);
	}

	@Override
	public void addExtension(String key, Object instance)
	{
		extensions.put(key, instance);
	}

	@Override
	public Set<String> getExtensionKeys()
	{
		return extensions.keySet();
	}
}
