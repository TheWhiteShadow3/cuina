package cuina.editor.eventx.internal;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;

import cuina.database.DatabaseObject;
import cuina.database.ui.AbstractDatabaseEditorPart;
import cuina.editor.core.CuinaProject;
import cuina.eventx.CommandList;

public class FlowEditor extends AbstractDatabaseEditorPart implements ITabbedPropertySheetPageContributor, IAdaptable, CommandDialogContext
{
	private Shell shell;
	private CommandList list;
	private TableViewer viewer;
	private FlowContentOutlinePage outlinePage;
	
	public void setValue(CommandList list)
	{
		this.list = list;
		if (viewer != null) viewer.setInput(list);
	}

	@Override
	public void createPartControl(Composite parent)
	{
		this.shell = parent.getShell();
		parent.setLayout(new FillLayout());
		
		SashForm splitter = new SashForm(parent, SWT.HORIZONTAL | SWT.SMOOTH);
		createViewer(splitter);
		createToolBox(splitter);
		splitter.setWeights(new int[] {70, 30});
		
		getEditorSite().setSelectionProvider(viewer);
	}
	
	private void createViewer(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		
		this.viewer = new TableViewer(composite, SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		viewer.setContentProvider(new FlowContentProvider());
		viewer.setLabelProvider(new FlowLabelProvider());
		viewer.setInput(list);
	}
	
	private void createToolBox(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		
		FunctionListPanel functionPanel = new FunctionListPanel(getCuinaProject(), this);
		functionPanel.createComponents(composite);
	}

	@Override
	public String getContributorId()
	{
		return "cuina.editor.eventx.CommandEditor";
	}

	@Override
	public Object getAdapter(Class adapter)
	{
		if (IContentOutlinePage.class.equals(adapter))
		{
			if (outlinePage == null)
			{
				outlinePage = new FlowContentOutlinePage();
				outlinePage.setInput(list);
			}
			return outlinePage;
		}
		return null;
	}

	@Override
	protected void init(DatabaseObject obj)
	{
		this.list = (CommandList) obj;
	}

	@Override
	protected boolean applySave()
	{
		return true;
	}

	@Override
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}

	@Override
	public Shell getShell()
	{
		return shell;
	}
	
	@Override
	public CuinaProject getCuinaProject()
	{
		return super.getCuinaProject();
	}
}