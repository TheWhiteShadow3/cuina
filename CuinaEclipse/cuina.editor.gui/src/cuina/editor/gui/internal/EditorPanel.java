package cuina.editor.gui.internal;

import cuina.editor.gui.internal.provider.WidgetContentProvider;
import cuina.editor.gui.internal.provider.WidgetLabelProvider;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.dialogs.PropertyDialogAction;

public class EditorPanel implements IDoubleClickListener, ISelectionProvider, IShellProvider
{
	private TreeViewer viewer;
	private Widget rootWidget, selected;
	
	public EditorPanel(Composite composite)
	{		
		viewer = new TreeViewer(composite, SWT.NONE);
		viewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		viewer.setContentProvider(new WidgetContentProvider());
//		viewer.setLabelProvider(new WidgetLabelProvider(true));

		viewer.addDoubleClickListener(this);
		viewer.getTree().addListener(SWT.Selection, new TypedListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				TreeItem item = (TreeItem) e.item;
				
				if(item.getData() instanceof Widget)
				{
					selected = (Widget) item.getData();
				}
			}
		}));
	}
	
	public void setWidgetData(Widget widgetData)
	{
		this.rootWidget = widgetData;
		viewer.setInput(widgetData);
	}
	
	public Widget getWidgetData()
	{
		return this.rootWidget;
	}
	
	public Control getControl()
	{
		return viewer.getControl();
	}
	
	public Widget getSelected()
	{
		return selected;
	}

	@Override
	public void doubleClick(DoubleClickEvent event)
	{
	    PropertyDialogAction dialogAction = new PropertyDialogAction(this, this);
	    dialogAction.run();
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		viewer.addSelectionChangedListener(listener);
	}

	@Override
	public ISelection getSelection()
	{
		return viewer.getSelection();
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		viewer.removeSelectionChangedListener(listener);
	}

	@Override
	public void setSelection(ISelection selection)
	{
		viewer.setSelection(selection, true);
	}

	@Override
	public Shell getShell()
	{
		return viewer.getTree().getShell();
	}

}
