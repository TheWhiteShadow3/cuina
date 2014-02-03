package cuina.editor.event.internal;

import cuina.editor.event.IEventDescriptor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class EventRegistry
{
	private static final String EVENT_EXTENSION = "cuina.event.EventTypes";
	
	private static List<EventDescriptor> descriptors;
	
	public static IEventDescriptor[] getEventDescriptors()
	{
		if (descriptors == null) registrateEvents();
		
		return descriptors.toArray(new IEventDescriptor[descriptors.size()]);
	}
	
	private static void registrateEvents()
	{
		descriptors = new ArrayList<EventDescriptor>();
		
		IConfigurationElement[] elements = Platform.getExtensionRegistry().
				getConfigurationElementsFor(EVENT_EXTENSION);

		for (IConfigurationElement conf : elements)
		{
			descriptors.add(new EventDescriptor(conf));
		}
	}
}
