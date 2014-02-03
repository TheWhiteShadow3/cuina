package cuina.editor.event;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class EventPropertyDescriptor extends PropertyDescriptor
{
	public EventPropertyDescriptor(Object id, String displayName)
	{
		super(id, displayName);
	}

	@Override
	public CellEditor createPropertyEditor(Composite parent)
	{
		return new EventCellEditor(parent);
	}
}
