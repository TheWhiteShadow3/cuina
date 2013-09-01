package cuina.editor.gui.internal;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import cuina.editor.gui.internal.provider.LibraryContentProvider;
import cuina.editor.gui.internal.provider.LibraryLabelProvider;
import cuina.editor.gui.internal.wizard.CreateWidgetWizard;

public class WidgetLibraryTree implements IDoubleClickListener
{
	private final EditorPanel editorPanel;
	private Composite parent;
	
	private TreeViewer viewer;
	
	public WidgetLibraryTree(EditorPanel editorPanel)
	{
		this.editorPanel = editorPanel;
	}
	
	public void createControl(Composite parent)
	{
		this.parent = parent;
		WidgetLibrary library = new WidgetLibrary();
		
		viewer = new TreeViewer(parent, SWT.NONE);
		viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer.setContentProvider(new LibraryContentProvider());
		viewer.setLabelProvider(new LibraryLabelProvider(library));
		viewer.setInput(library);
		viewer.expandToLevel(3);
		viewer.addDoubleClickListener(this);
	}
	
	@Override
	public void doubleClick(DoubleClickEvent ev)
	{
		Object obj = ((IStructuredSelection) ev.getSelection()).getFirstElement();

		CreateWidgetWizard widgetWizard = new CreateWidgetWizard(editorPanel.getWidgetData(), editorPanel.getSelected(), (Class<?>) obj);
		WizardDialog wizardDialog = new WizardDialog(parent.getShell(), widgetWizard);
		wizardDialog.open();
	}

}
