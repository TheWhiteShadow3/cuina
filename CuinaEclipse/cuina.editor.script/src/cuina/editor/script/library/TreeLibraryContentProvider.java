package cuina.editor.script.library;


import cuina.editor.script.library.TreeLibrary.LibraryTree;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TreeLibraryContentProvider implements ITreeContentProvider
{
	private static final Object[] EMPTY_LIST = new Object[0];
	
	private LibraryTree tree;
	
	@Override
	public void dispose()
	{
		tree = null;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		if (newInput == null) return;
		if (!(newInput instanceof LibraryTree))
			throw new IllegalArgumentException("Input must be an Instance of TreeLibrary.LibraryTree");
		
		this.tree = (LibraryTree) newInput;
	}

	@Override
	public Object[] getElements(Object element)
	{
		return getChildren(element);
	}

	@Override
	public Object[] getChildren(Object element)
	{
		if (element instanceof LibraryTree)
			return tree.values().toArray();
		if (element instanceof Reciver)
			return ((Reciver) element).entries.values().toArray();
		
		return EMPTY_LIST;
	}

	@Override
	public Object getParent(Object element)
	{
		if (element instanceof TreeDefinition)
			return ((TreeDefinition) element).getParent();
		else
			return null;
	}

	@Override
	public boolean hasChildren(Object element)
	{
		if (element instanceof LibraryTree) return ((LibraryTree) element).size() > 0;
		if (element instanceof Reciver) return ((Reciver) element).entries.size() > 0;
		return false;
	}
}
