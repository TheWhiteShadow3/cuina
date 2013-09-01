package cuina.data;

import cuina.database.DatabaseObject;

import java.util.HashMap;

public class Interface implements DatabaseObject
{
	private static final long serialVersionUID = 6491258846926197398L;

	private String key;
	private String name = "";

	public HashMap<Integer, InterfaceObject> objects;
	public String background = "";

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
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
}
