package cuina.editor.event.internal;

import cuina.editor.event.IEventDescriptor;
import cuina.event.Event;

import org.eclipse.core.runtime.IConfigurationElement;

public class EventDescriptor implements IEventDescriptor
{
	private String id;
	private String description;
	
	public EventDescriptor(IConfigurationElement conf)
	{
		this.id = conf.getAttribute("id");
		this.description = conf.getAttribute("description");
	}

	@Override
	public String getID()
	{
		return id;
	}

	@Override
	public String getDescription()
	{
		return description;
	}
	
	@Override
	public Event getEvent()
	{
		return Event.getEvent(id);
	}
}
