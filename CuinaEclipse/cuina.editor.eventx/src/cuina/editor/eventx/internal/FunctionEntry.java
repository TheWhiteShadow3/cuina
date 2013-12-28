package cuina.editor.eventx.internal;

public class FunctionEntry
{
	public final Category category;
	public final String target;
	public final String className;
	public final String name;
	public final String label;
	public final Class<?>[] argTypes;
	public final String[] argNames;
	
	FunctionEntry(Category category, String target, String className, String name, String label,
			Class<?>[] argTypes, String[] argNames)
	{
		this.category = category;
		this.target = target;
		this.className = className;
		this.name = name;
		this.label = label;
		this.argTypes = argTypes;
		this.argNames = argNames;
	}
}