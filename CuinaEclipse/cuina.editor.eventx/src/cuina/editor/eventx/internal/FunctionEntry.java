package cuina.editor.eventx.internal;

public class FunctionEntry
{
	public final Category category;
	public final String target;
	public final String className;
	public final String name;
	public final String label;
	public String description;
	public final String[] argTypes;
	public final String[] argNames;
	
	FunctionEntry(Category category, String target, String className, String name, String label, String description,
			String[] argTypes, String[] argNames)
	{
		this.category = category;
		this.target = target;
		this.className = className;
		this.name = name;
		this.label = label;
		this.description = description;
		this.argTypes = argTypes;
		this.argNames = argNames;
	}
}