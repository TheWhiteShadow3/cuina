package cuina.editor.script.library;



import java.util.HashMap;

public class ClassDefinition implements Definition
{
	public String id;
	public String label;
	public final HashMap<String, ValueDefinition> fields = new HashMap<String, ValueDefinition>();
	public final HashMap<String, FunctionDefinition> methods = new HashMap<String, FunctionDefinition>();
	
	public ClassDefinition(String id, String label)
	{
		this.id = id;
		this.label = label;
	}
	
	@Override
	public Definition getParent()
	{
		return null;
	}

	@Override
	public String getID()
	{
		return id;
	}
	
	@Override
	public String getLabel()
	{
		return label != null ? label : id;
	}

	public void add(ValueDefinition value)
	{
		value.parent = this;
		fields.put(value.id, value);
	}

	public void add(FunctionDefinition value)
	{
		value.parent = this;
		methods.put(value.id, value);
	}

	@Override
	public String getType()
	{
		return id;
	}
}
