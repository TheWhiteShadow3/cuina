package cuina.editor.gui.internal.provider;

import cuina.editor.gui.internal.WidgetLibrary;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class WidgetLibaryContentProvider implements IStructuredContentProvider
{
	private WidgetLibrary library;

	@Override
	public void dispose()
	{}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{}

	@Override
	public Object[] getElements(Object inputElement)
	{
		if (!(inputElement instanceof WidgetLibrary)) throw new IllegalArgumentException();
		
		library = (WidgetLibrary) inputElement;
		return library.getWidgetTypes().toArray();
	}
}
