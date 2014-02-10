package cuina.editor.event;

import cuina.event.Event;

import java.util.List;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.widgets.Composite;

public class EventCellEditor extends ComboBoxCellEditor
{
	private List<IEventDescriptor> descriptors;
	
	public EventCellEditor(Composite parent)
	{
		super();
		this.descriptors = EventRegistry.getEventDescriptors();
		setItems(createItems());
		create(parent);
	}
	
	private String[] createItems()
	{
		String[] items = new String[descriptors.size()];
		for (int i = 0; i < items.length; i++)
		{
			items[i] = descriptors.get(i).getID();
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
		return descriptors.get((Integer) super.doGetValue()).getEvent();
	}

	@Override
	protected void doSetValue(Object value)
	{
		for (int i = 0; i < descriptors.size(); i++)
			if (descriptors.get(i).getEvent().equals(value))
			{
				super.doSetValue(i);
				return;
			}
		super.doSetValue(-1);
	}
}
