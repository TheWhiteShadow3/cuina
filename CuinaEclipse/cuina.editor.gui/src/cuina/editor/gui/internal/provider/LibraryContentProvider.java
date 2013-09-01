package cuina.editor.gui.internal.provider;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import cuina.editor.gui.internal.WidgetLibrary;

public class LibraryContentProvider implements ITreeContentProvider
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
		if(inputElement instanceof WidgetLibrary)
		{
			library = (WidgetLibrary) inputElement;
			
			return library.getWidgets();
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement)
	{
		return null;
	}

	@Override
	public Object getParent(Object element)
	{
		return library;
	}

	@Override
	public boolean hasChildren(Object element)
	{
		return (element instanceof WidgetLibrary);
	}

}
