package cuina.editor.eventx.internal;

import cuina.editor.eventx.internal.tree.CommandNode;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class FlowOutlineProvider implements ITreeContentProvider
{
	private static final Object[] EMPTY = new Object[0];
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	@Override
	public Object[] getElements(Object element)
	{
		return getChildren(element);
	}

	@Override
	public Object[] getChildren(Object element)
	{
		if (element instanceof CommandNode)
		{
			return ((CommandNode) element).getChildren().toArray();
		}
		
		return EMPTY;
	}

	@Override
	public Object getParent(Object element)
	{
		if (element instanceof CommandNode)
		{
			return ((CommandNode) element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element)
	{
		if (element instanceof CommandNode)
		{
			return ((CommandNode) element).getChildren().size() > 0;
		}
		return false;
	}

	@Override
	public void dispose() {}
}
