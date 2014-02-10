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
	
	public String getTarget()
	{
		switch(type)
		{
			case CommandLibrary.INTERNAL_CONTEXT: return type;
			case CommandLibrary.STATIC_CONTEXT: return type + ':' + className;
			default: return type + ':' + name;
		}
	}
}
