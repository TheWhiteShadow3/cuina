package cuina.editor.script.library;



import java.util.List;

public class ValueDefinition implements Definition
{
	public final int DEFAULT 	= 0;
	public final int STATIC 	= 1;
	public final int READABLE 	= 2;
	public final int WRITABLE 	= 4;

	public Definition parent;
	public String id;
	public String label;
	public String type;
	public String def; // default
	public int access = READABLE | WRITABLE;
	public double minRange;
	public double maxRange;
	public List<String> validValues;
	public int minLenght = 0;
	public int maxLenght = 0;
	public String pattern;

	@Override
	public Definition getParent()
	{
		return parent;
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
	
	@Override
	public String getType()
	{
		return type;
	}

	@Override
	public String toString()
	{
		return "\nValueDefinition: " + id + " (" + type + ")";
		// return "\nEventParameter [id=" + id + ", label=" + label + ", type="
		// + type + ", def=" + def
		// + ", validValues=" + validValues + ", minLenght=" + minLenght +
		// ", maxLenght=" + maxLenght
		// + ", pattern=" + pattern + "]";
	}
}
