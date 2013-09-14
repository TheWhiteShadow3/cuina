package cuina.editor.debug;

import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;

public class CuinaVariableResolver implements IDynamicVariableResolver
{
	private static final HashMap<String, String> VALUES = new HashMap<String, String>();

	public static String getValue(String name)
	{
		return VALUES.get(name);
	}

	public static String setValue(String name, String value)
	{
		return VALUES.put(name, value);
	}
	
	@Override
	public String resolveValue(IDynamicVariable variable, String argument) throws CoreException
	{
		return VALUES.get(variable.getName());
	}
}
