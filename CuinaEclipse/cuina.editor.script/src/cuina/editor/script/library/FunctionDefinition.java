package cuina.editor.script.library;



import java.util.ArrayList;

public class FunctionDefinition implements Definition
{
	public Definition parent;
	public String id;
	public String label;
	public String code;
	public String text;
	public String returnType;
	public String help;
	public boolean staticFunction = true;

	public final ArrayList<ValueDefinition> params = new ArrayList<ValueDefinition>(4);

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
	
	/**
	 * Gibt den Anzeige-Namen der Funktion zurück. Wenn kein Anzeigename
	 * definiert ist, wird die ID zurückgegeben.
	 * 
	 * @return Anzeige-Namen oder, wenn nicht definiert, die ID.
	 */
	@Override
	public String getLabel()
	{
		return label != null ? label : id;
	}

	@Override
	public String toString()
	{
		return "FunctionDefinition [id=" + id + ", label=" + label + "\ncode=" + code + "\ntext=" + text
				+ "\nreturnType=" + returnType + "\nparams=" + params;
	}

	@Override
	public String getType()
	{
		return returnType;
	}
}
