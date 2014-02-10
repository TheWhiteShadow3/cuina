package cuina.editor.eventx.internal;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;

import cuina.editor.core.CuinaProject;

public class FunctionListPanel
{
	private CommandLibrary library;
	private TreeViewer viewer;
	private FlowEditor editor;
	
	public FunctionListPanel(CuinaProject project, FlowEditor editor)
	{
		this.library = project.getService(CommandLibrary.class);
		this.editor = editor;
	}
	
	public void createComponents(Composite parent)
	{
		this.viewer = new TreeViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.getTree().setHeaderVisible(true);
		
		TreeColumn nameColumn = new TreeColumn(viewer.getTree(), SWT.LEFT);
		nameColumn.setAlignment(SWT.LEFT);
		nameColumn.setText("Name");
		nameColumn.setWidth(160);
		TreeColumn argsColumn = new TreeColumn(viewer.getTree(), SWT.RIGHT);
		argsColumn.setAlignment(SWT.LEFT);
		argsColumn.setText("Argumente");
		argsColumn.setWidth(100);
		
		viewer.setContentProvider(new FunctionContentProvider());
		viewer.setLabelProvider(new FunctionLabelProvider());
		viewer.setInput(library);
		
		EventHandler handler = new EventHandler();
		viewer.addDoubleClickListener(handler);
	}
	
	private class EventHandler implements IDoubleClickListener
	{
		@Override
		public void doubleClick(DoubleClickEvent event)
		{
			Object item = ((IStructuredSelection) event.getSelection()).getFirstElement();
			
			if (item instanceof FunctionEntry)
			{
				CommandDialog dialog = new CommandDialog(editor, (FunctionEntry) item);
				int result = dialog.open();
				if (result == TitleAreaDialog.OK)
				{
					
					System.out.println("Command erstellt: " + dialog.getCommand());
					editor.addCommand(dialog.getCommand());
				}
			}
		}
	}
}