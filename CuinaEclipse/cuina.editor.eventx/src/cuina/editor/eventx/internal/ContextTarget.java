package cuina.editor.eventx.internal;

public class ContextTarget
{
	public String type;
	public String name;
	public Class clazz;
	
	public ContextTarget(String type, String name, Class clazz)
	{
		this.type = type;
		this.name = name;
		this.clazz = clazz;
	}
	
	public String getTarget(boolean isStatic)
	{
		if (isStatic)
			return type + ':' + clazz.getName();
		else
			return type + ':' + name;
	}
}