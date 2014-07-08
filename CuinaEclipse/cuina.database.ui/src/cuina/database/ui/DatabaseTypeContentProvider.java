package cuina.database.ui;

import cuina.database.IDatabaseDescriptor;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class DatabaseTypeContentProvider implements IStructuredContentProvider
{
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	@Override
	public void dispose() {}

	@Override
	public Object[] getElements(Object element)
	{
		if (!(element instanceof IDatabaseDescriptor[])) throw new IllegalArgumentException();
		
		return (IDatabaseDescriptor[]) element;
	}
}
