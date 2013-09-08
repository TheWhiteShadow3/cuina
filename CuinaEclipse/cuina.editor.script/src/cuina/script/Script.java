package cuina.script;

import cuina.database.DatabaseObject;

public class Script implements DatabaseObject
{
	private static final long serialVersionUID = -6467215823639970856L;
	
	private String key;
	private String name;
	private String interfaceClass;
	private String code;
	
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
	
	public String getCode()
	{
		return code;
	}
	
	public void setCode(String code)
	{
		this.code = code;
	}

	public String getInterfaceClass()
	{
		return interfaceClass;
	}

	public void setInterfaceClass(String clazz)
	{
		this.interfaceClass = clazz;
	}
}
