package cuina.editor.gui.internal.wizard;


import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

import cuina.data.widget.Widget;
import cuina.editor.gui.internal.provider.WidgetContentProvider;
import cuina.editor.gui.internal.provider.WidgetLabelProvider;

public class SelectParentWizardPage extends WizardPage implements IDoubleClickListener
{
	private Widget root, selected;
	private TreeViewer viewer;

	public SelectParentWizardPage(Widget root, Widget preSelected)
	{
		super("Select Parent");
		setTitle("Select Parent Widget");
		setDescription("");
		this.root = root;
		this.selected = preSelected;
	}

	@Override
	public void createControl(Composite parent)
	{
		setPageComplete(false);
		
		viewer = new TreeViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		setControl(viewer.getControl());

		viewer.setContentProvider(new WidgetContentProvider());
		viewer.setLabelProvider(new WidgetLabelProvider(false));
		viewer.setInput(root);
		
		viewer.expandAll();
		
		if(selected != null)
		{
			viewer.setSelection(new StructuredSelection(selected), true);
			setPageComplete(true);
		}
		
		viewer.addDoubleClickListener(this);
		viewer.getTree().addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				TreeItem item = (TreeItem) e.item;
				
				if(item.getData() instanceof Widget)
				{
					selected = (Widget) item.getData();
					setPageComplete(true);
				}
			}
		});
	}

	public Widget getSelected()
	{
		return selected;
	}

	@Override
	public void doubleClick(DoubleClickEvent event)
	{
		IStructuredSelection thisSelection = (IStructuredSelection) event.getSelection();
	    if(thisSelection.getFirstElement() instanceof Widget)
	    	selected = (Widget) thisSelection.getFirstElement(); 
		setPageComplete(true);
		// call widget change wizards
		
	}

}
