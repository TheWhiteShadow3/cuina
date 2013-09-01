package cuina.editor.gui.internal.provider;


import cuina.database.ui.AbstractChildContentProvider;
import cuina.database.ui.internal.tree.TreeDataNode;
import cuina.widget.data.WidgetNode;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.Viewer;

public class WidgetContentProvider extends AbstractChildContentProvider
{
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{}

	@Override
	public Object[] getElements(Object element)
	{
		return getChildren(element);
	}

	@Override
	public Object[] getChildren(Object parent)
	{
//		if(parentElement instanceof RootElement)
//			return ((RootElement)parentElement).getChildren();
//		
		if(parent instanceof WidgetNode)
		{
			WidgetNode widget = (WidgetNode) parent;
			if(widget != null && widget.children != null)
				return widget.children.toArray();
		}
		else if (parent instanceof IAdaptable)
		{
			return new Object[] { ((IAdaptable) parent).getAdapter(WidgetNode.class) };
		}
		
		return EMPTY;
	}

	@Override
	public Object getParent(Object element)
	{
//		if(element instanceof Widget)
//		{
//			if(((Widget) element).getParent() == null)
//				return root;
//			return ((Widget)element).getParent();
//		}
		return root;
	}

	@Override
	public boolean hasChildren(Object element)
	{
		if (element instanceof TreeDataNode) return true;
		
		if(element instanceof WidgetNode)
		{
			WidgetNode widget = (WidgetNode)element;
			if(widget.children != null)
				return !(widget.children.isEmpty());
		}
		
		return false;
	}
}
