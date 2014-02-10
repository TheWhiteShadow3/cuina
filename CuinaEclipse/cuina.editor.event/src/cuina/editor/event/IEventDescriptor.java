package cuina.editor.event;

import cuina.database.NamedItem;
import cuina.event.Event;

import org.eclipse.swt.graphics.Image;

public interface IEventDescriptor extends NamedItem
{
	public String getID();

	public String getDescription();

	public Event getEvent();

	public Image getImage();
}
