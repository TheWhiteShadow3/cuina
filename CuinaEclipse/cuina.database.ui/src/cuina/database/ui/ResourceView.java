package cuina.database.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import cuina.database.ui.ReferenceCounter.Reference;

public class ResourceView extends Dialog
{
	private IProject project;
	private Reference[] references;
	private TreeViewer tree;
	
	public ResourceView(Shell shell, IProject project, Reference[] references)
	{
		super(shell);
		this.project = project;
		this.references = references;
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
//		parent = (Composite) super.createDialogArea(parent);
		tree = new TreeViewer(parent);
		tree.getControl().setSize(480, 480);
		tree.getTree().setLinesVisible(true);
		tree.getTree().setHeaderVisible(true);
		tree.setContentProvider(new ResourceContentProvider());
		
		TreeViewerColumn column = new TreeViewerColumn(tree, SWT.DEFAULT);
		column.getColumn().setWidth(200);
		column.getColumn().setText("Name");
		column.setLabelProvider(new ReferenceNames());
		
		column = new TreeViewerColumn(tree, SWT.DEFAULT);
		column.getColumn().setWidth(200);
		column.getColumn().setText("Referenzen");
		column.setLabelProvider(new ReferenceData());
		
		tree.setInput(project);
		return tree.getControl();
	}
	
	private class ResourceContentProvider implements ITreeContentProvider
	{
		@Override
		public void dispose()
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
			
		}

		@Override
		public Object[] getElements(Object element)
		{
			if (element instanceof IProject)
			{
				return references;//new Object[] {"Test 1", "Test 2", "Test 3"};
			}
//			if (element instanceof String)
//			{
//				String str = (String) element;
//				if (str.length() < 10)
//				{
//					return new Object[] {str + ".1", str + ".2"};
//				}
//			}
			return new Object[0];
		}

		@Override
		public Object[] getChildren(Object element)
		{
			return getElements(element);
		}

		@Override
		public Object getParent(Object element)
		{
			return null;
		}

		@Override
		public boolean hasChildren(Object element)
		{
//			return element instanceof IProject || (element instanceof String && ((String) element).length() < 10);
			return element instanceof IProject;
		}
	}
	
	private class ReferenceNames extends ColumnLabelProvider
	{
		@Override
		public String getText(Object element)
		{
			if (element instanceof Reference)
				return ((Reference) element).getName();
			else
				return super.getText(element);
		}
	}
	
	private class ReferenceData extends ColumnLabelProvider
	{
		@Override
		public String getText(Object element)
		{
			if (element instanceof Reference)
				return Integer.toString( ((Reference) element).getCount() );
			else
				return super.getText(element);
		}
	}
}
