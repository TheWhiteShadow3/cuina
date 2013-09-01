package cuina.database.ui;

import cuina.database.DatabaseObject;
import cuina.database.ui.internal.tree.TreeDataNode;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class AbstractChildContentProvider<E extends DatabaseObject> implements ITreeContentProvider
{
	protected static final Object[] EMPTY = new Object[0];
	
	protected TreeDataNode root;
	
	public void setRoot(TreeDataNode root)
	{
		this.root = root;
	}
	
	public TreeDataNode getRoot()
	{
		return root;
	}

	@Override
	public Object[] getElements(Object inputElement)
	{
		return getChildren(inputElement);
	}
	
	@Override
	public void dispose()
	{
		root = null;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{}

	@Override
	public Object[] getChildren(Object parentElement)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getParent(Object element)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
