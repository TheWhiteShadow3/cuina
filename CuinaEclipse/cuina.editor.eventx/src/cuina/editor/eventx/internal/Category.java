package cuina.editor.eventx.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Category
{
//	public final String id;
	public String name;
	final List<FunctionEntry> functions = new ArrayList<FunctionEntry>();
	
	public Category(String name)
	{
//		this.id = id;
		this.name = name;
	}
	
	public FunctionEntry getFunction(String target, String name)
	{
		for (int i = 0; i < functions.size(); i++)
		{
			FunctionEntry entry = functions.get(i);
			if (entry.target.equals(target) && entry.name.equals(name))
				return entry;
		}
		return null;
	}
	
	public List<FunctionEntry> getFunctions()
	{
		return Collections.unmodifiableList(functions);
	}
	
	public void addFunktion(FunctionEntry function)
	{
		if (functions.contains(function)) return;
		functions.add(function);
	}
}