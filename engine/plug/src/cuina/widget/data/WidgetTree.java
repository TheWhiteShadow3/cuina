package cuina.widget.data;

import cuina.database.DatabaseObject;

public class WidgetTree implements DatabaseObject
{
	private static final long serialVersionUID = 3365171219051989287L;
	
	private String key;
	private String name;
	
	public WidgetNode root;
	public String active;

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
}
