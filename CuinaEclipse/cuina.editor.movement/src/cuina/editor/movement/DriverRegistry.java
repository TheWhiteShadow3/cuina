package cuina.editor.movement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class DriverRegistry
{
	private static final String TYPE_EXTENSION = "cuina.movement.Driver";

	private static List<DriverType> driverTypes;
	
	private DriverRegistry() {}
	
	public static List<DriverType> getDriverTypes()
	{
		if (driverTypes == null) registDriverTypes();
		
		return Collections.unmodifiableList(driverTypes);
	}
	
	public static DriverType getDriverTypeFromClass(String className)
	{
		if (driverTypes == null) registDriverTypes();
		
		for(DriverType type : driverTypes)
		{
			if (type.getClassName().equals(className)) return type;
		}
		return null;
	}
	
	private static void registDriverTypes()
	{
		IConfigurationElement[] elements = Platform.getExtensionRegistry().
				getConfigurationElementsFor(TYPE_EXTENSION);
		
		driverTypes = new ArrayList<DriverType>(elements.length);
		for(IConfigurationElement conf : elements)
		{
			driverTypes.add(new DriverType(conf));
		}
	}
}
