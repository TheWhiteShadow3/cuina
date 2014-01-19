package cuina.editor.core;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.IAdaptable;

/**
 * Tester um Objekte auf mögliche Adapter zu testen, da Eclipse das nicht vernünftig kann. :(
 * <p>
 * Die zu prüfende Eigenschaft heißt: <pre>cuina.editor.core.adapt</pre>
 * Als Wert muss die Adapter-Klasse angegeben werden.
 * </p>
 * @author TheWhiteShadow
 */
public class AdapterTester extends PropertyTester
{
	private static final String ADAPT = "adapt";
	
	@Override
	public boolean test(Object receiver, String method, Object[] args, Object expectedValue)
	{
		if (receiver instanceof IAdaptable && ADAPT.equals(method))
		{
			return testObjectAdapter((IAdaptable) receiver, method, args, (String) expectedValue);
		}
		return false;
	}

	private boolean testObjectAdapter(IAdaptable receiver, String method, Object[] args, String className)
	{
		try
		{
			Class clazz = Class.forName(className);
			Object adapter = receiver.getAdapter(clazz);
			return adapter != null;
		}
		catch (ClassNotFoundException e)
		{
			return false;
		}
	}
}
