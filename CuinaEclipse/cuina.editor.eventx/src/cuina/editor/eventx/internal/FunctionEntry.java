package cuina.editor.eventx.internal;

public class FunctionEntry
{
	public final String target;
	public Class clazz;
	public String name;
	public String label;
	public Class<?>[] argTypes;
	public String[] argNames;
	
	FunctionEntry(String target, Class clazz, String name, String label, Class<?>[] argTypes, String[] argNames)
	{
		this.target = target;
		this.clazz = clazz;
		this.name = name;
		this.label = label;
		this.argTypes = argTypes;
		this.argNames = argNames;
	}
}