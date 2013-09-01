package cuina.object;

import cuina.database.DatabaseObject;

public class ObjectTemplate implements DatabaseObject
{
	private static final long	serialVersionUID	= 4006961914581064448L;
	
	private String key;
	public String name;
	public ObjectData sourceObject;
	
	public ObjectTemplate()
	{
		this.name = "new Template";
		this.sourceObject = new ObjectData();
	}
	
	public ObjectTemplate(ObjectData sourceObject, String name)
	{
		this.name = name;
		this.sourceObject = sourceObject;
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
	public void setName(String name)
	{
		this.name = name;
		if (sourceObject != null)
			sourceObject.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}
	
	public ObjectData createNewObject(int id)
	{
		ObjectData obj = new ObjectData();
		obj.id 			= id;
		obj.name 		= sourceObject.name;
		obj.x 			= sourceObject.x;
		obj.y 			= sourceObject.y;
		obj.z 			= sourceObject.z;
		obj.extensions	= sourceObject.extensions;
		obj.templateKey = key;
		return obj;
	}
}