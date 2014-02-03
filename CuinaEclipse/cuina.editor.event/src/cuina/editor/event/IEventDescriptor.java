package cuina.editor.event;

import cuina.event.Event;

public interface IEventDescriptor
{
	public String getID();

	public String getDescription();

	public Event getEvent();
}
