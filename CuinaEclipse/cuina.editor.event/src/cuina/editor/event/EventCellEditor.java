package cuina.editor.event;

import cuina.editor.event.internal.EventRegistry;
import cuina.event.Event;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.widgets.Composite;

public class EventCellEditor extends ComboBoxCellEditor
{
	private IEventDescriptor[] descriptors;
	
	public EventCellEditor(Composite parent)
	{
		super();
		this.descriptors = EventRegistry.getEventDescriptors();
		setItems(createItems());
		create(parent);
	}
	
	private String[] createItems()
	{
		String[] items = new String[descriptors.length];
		for (int i = 0; i < items.length; i++)
		{
			items[i] = descriptors[i].getID();
		}
		return items;
	}
	
	public Event getEvent()
	{
		return (Event) getValue();
	}

	@Override
	protected Event doGetValue()
	{
		return descriptors[(Integer) super.doGetValue()].getEvent();
	}

	@Override
	protected void doSetValue(Object value)
	{
		for (int i = 0; i < descriptors.length; i++)
			if (descriptors[i].getEvent().equals(value))
			{
				super.doSetValue(i);
				return;
			}
		super.doSetValue(-1);
	}
}
