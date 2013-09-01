package cuina.editor.movement.properties;

import java.util.ArrayList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class DriverRegistry
{
	private static final String TYPE_EXTENSION = "cuina.movement.Driver";

	private static String[] driverTypes;
	
	private DriverRegistry() {}
	
	public static String[] getDriverTypes()
	{
		if (driverTypes == null)
		{
			IConfigurationElement[] elements = Platform.getExtensionRegistry().
					getConfigurationElementsFor(TYPE_EXTENSION);
			
			ArrayList<String> list = new ArrayList<String>(elements.length);
			for(IConfigurationElement conf : elements)
			{
				String name = conf.getAttribute("class");
				if (name == null)
					throw new NullPointerException("attribut 'class' must not be null.");
				list.add(name);
			}
			driverTypes = list.toArray(new String[list.size()]);
		}
		return driverTypes;
	}
}
