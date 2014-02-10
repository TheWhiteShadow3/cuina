package cuina.editor.event;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

@Deprecated
public class EventComboViewer extends ComboViewer
{
	/** NULL-Element. */
	private static Object NULL = new String();
	
	public EventComboViewer(Composite parent, int style)
	{
		super(parent, style);
		setContentProvider(new EventContentProvider());
		setLabelProvider(new EventLaybelProvider());
		setInput(this);
	}
	
	/**
	 * Gibt das ausgewählte Element zurück oder <code>null</code>, wenn keins ausgewählt ist.
	 * @return ausgewählte Element.
	 */
	public IEventDescriptor getSelectedElement()
	{
		Object obj = ((IStructuredSelection) getSelection()).getFirstElement();
		if (obj == NULL) return null;
		return (IEventDescriptor) obj;
	}
	
	public void setSelectedElement(IEventDescriptor obj)
	{
		if (obj == null)
			setSelection(new StructuredSelection(NULL));
		else
			setSelection(new StructuredSelection(obj));
	}
	
	private static class EventContentProvider implements IStructuredContentProvider
	{
		@Override
		public void dispose() {}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

		@Override
		public Object[] getElements(Object inputElement)
		{
			return EventRegistry.getEventDescriptors();
		}
	}
	
	private static class EventLaybelProvider extends LabelProvider
	{
		@Override
		public Image getImage(Object element)
		{
			if (element instanceof IEventDescriptor)
			{
				return ((IEventDescriptor) element).getImage();
			}
			return null;
		}

		@Override
		public String getText(Object element)
		{
			if (element instanceof IEventDescriptor)
			{
				return ((IEventDescriptor) element).getID();
			}
			
			return super.getText(element);
		}
	}
}
