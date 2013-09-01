package cuina.ks;

import cuina.database.DatabaseObject;

public class BattleGroundData implements DatabaseObject
{
	private static final long serialVersionUID = 7668354004932149964L;
	
	private String key;
	private String name;
	public String backgroundName;

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

	public void setName(String name)
	{
		this.name = name;
	}
}
