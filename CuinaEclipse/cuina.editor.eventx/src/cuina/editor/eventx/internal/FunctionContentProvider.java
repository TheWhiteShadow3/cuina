package cuina.editor.eventx.internal;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class FunctionContentProvider implements ITreeContentProvider
{
	private static final Object[] EMPTY = new Object[0];
	
	private CommandLibrary library;
	
	public FunctionContentProvider(CommandLibrary library)
	{
		this.library = library;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		if (newInput instanceof CommandLibrary)
			this.library = (CommandLibrary) newInput;

	}

	@Override
	public Object[] getElements(Object element)
	{
		return getChildren(element);
	}

	@Override
	public Object[] getChildren(Object element)
	{
		if (element instanceof CommandLibrary)
		{
			return ((CommandLibrary) element).getCategories().values().toArray();
		}
		if (element instanceof Category)
		{
			return ((Category) element).functions.toArray();
		}
		return EMPTY;
	}

	@Override
	public Object getParent(Object element)
	{
		if (element instanceof FunctionEntry)
		{
			return library.findCategory((FunctionEntry) element);
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element)
	{
		if (element instanceof Category)
		{
			return ((Category) element).functions.size() > 0;
		}
		return false;
	}

	@Override
	public void dispose() { library = null; }
}