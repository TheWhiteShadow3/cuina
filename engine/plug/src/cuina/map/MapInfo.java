package cuina.map;

import cuina.database.DatabaseObject;

public class MapInfo implements DatabaseObject
{
	private static final long serialVersionUID = 7434889275816520707L;
	
	private String key;
	private String name;
	
	public MapInfo(String key, String name)
	{
		this.key = key;
		this.name = name;
	}

	@Override
	public String getKey()
	{
		return key;
	}

	@Override
	public void setKey(String key)
	{
		this.key = key;
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
}
