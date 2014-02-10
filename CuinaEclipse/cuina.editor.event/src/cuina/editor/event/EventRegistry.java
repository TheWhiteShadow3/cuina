package cuina.editor.event;


import cuina.event.Event;
import cuina.event.Trigger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class EventRegistry
{
	private static final String EVENT_EXTENSION = "cuina.event.EventTypes";
	private static final String TRIGGER_EXTENSION = "cuina.event.TriggerTypes";
	
	private static List<IEventDescriptor> events;
	private static List<ITriggerDescriptor> triggers;
	
	public static List<IEventDescriptor> getEventDescriptors()
	{
		if (events == null) registrateEvents();
		
		return Collections.unmodifiableList(events);
	}
	
	public static IEventDescriptor getEventDescriptor(Event event)
	{
		if (events == null) registrateEvents();
		
		for (IEventDescriptor desc : events)
		{
			if (desc.getEvent().equals(event)) return desc;
		}
		return null;
	}
	
	private static void registrateEvents()
	{
		events = new ArrayList<IEventDescriptor>();
		
		IConfigurationElement[] elements = Platform.getExtensionRegistry().
				getConfigurationElementsFor(EVENT_EXTENSION);

		for (IConfigurationElement conf : elements) try
		{
			events.add(new EventDescriptor(conf));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static List<ITriggerDescriptor> getTriggerDescriptors()
	{
		if (triggers == null) registrateTriggers();
		
		return Collections.unmodifiableList(triggers);
	}
	
	public static ITriggerDescriptor getTriggerDescriptor(Trigger trigger)
	{
		if (triggers == null) registrateEvents();
		
		for (ITriggerDescriptor desc : triggers)
		{
			if (desc.getTriggerClass().equals(trigger.getClass())) return desc;
		}
		return null;
	}
	
	private static void registrateTriggers()
	{
		triggers = new ArrayList<ITriggerDescriptor>();
		
		IConfigurationElement[] elements = Platform.getExtensionRegistry().
				getConfigurationElementsFor(TRIGGER_EXTENSION);

		for (IConfigurationElement conf : elements) try
		{
			triggers.add(new TriggerDescriptor(conf));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
