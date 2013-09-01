package cuina.editor.ui;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class CuinaResourceContentProvider implements ITreeContentProvider
{
	protected static final Object[] EMPTY = new Object[0];
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	@Override
	public Object[] getElements(Object parent)
	{
		return getChildren(parent);
	}

	@Override
	public Object[] getChildren(Object parent)
	{
		try
		{
			if (parent instanceof IContainer)
			{
				return getContainerContent((IContainer) parent);
			}
		}
		catch(CoreException e) {}
		return EMPTY;
	}
	
	protected Object[] getContainerContent(IContainer container) throws CoreException
	{
		return container.members();
	}

	@Override
	public Object getParent(Object element)
	{
		if (element instanceof IResource)
		{
			return ((IResource) element).getParent();
		}
		
		return null;
	}

	@Override
	public boolean hasChildren(Object element)
	{
		if (element instanceof IFile) return false;
		
		return getChildren(element).length > 0;
	}
	
	@Override
	public void dispose() {}
}
