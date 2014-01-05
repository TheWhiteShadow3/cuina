package cuina.editor.eventx.internal;

public class ContextTarget
{
	public String type;
	public String name;
	public String className;
	
	public ContextTarget(String type, String name, String className)
	{
		this.type = type;
		this.name = name;
		this.className = className;
	}
	
	public String getTarget(boolean isStatic)
	{
		if (isStatic)
			return type + ':' + className;
		else
			return type + ':' + name;
	}
}