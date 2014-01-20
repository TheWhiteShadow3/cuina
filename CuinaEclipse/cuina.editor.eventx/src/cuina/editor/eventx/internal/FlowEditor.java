package cuina.editor.eventx.internal;

import cuina.database.DatabaseObject;
import cuina.database.ui.AbstractDatabaseEditorPart;
import cuina.editor.core.CuinaProject;
import cuina.editor.eventx.internal.FlowContentProvider.Item;
import cuina.editor.eventx.internal.prefs.EventPreferences;
import cuina.editor.eventx.internal.tree.CommandNode;
import cuina.editor.eventx.internal.tree.CommandTree;
import cuina.eventx.Command;
import cuina.eventx.CommandList;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;

public class FlowEditor extends AbstractDatabaseEditorPart implements ITabbedPropertySheetPageContributor, IAdaptable, CommandEditorContext
{
	private Shell shell;
	private CommandList list;
	private TableViewer viewer;
	private FlowContentOutlinePage outlinePage;
	private CommandLibrary library;
	private CommandTree tree;

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
		Control ctl = viewer.getControl();
		ctl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Font font = new Font(ctl.getDisplay(), EventPreferences.getFontData(EventPreferences.CMDLINE_FONT));
		ctl.setFont(font);
		
		viewer.setContentProvider(new FlowContentProvider(library));
		viewer.setLabelProvider(new FlowLabelProvider(library));
		viewer.setInput(tree);
		
		EventHandler handler = new EventHandler();
		viewer.addDoubleClickListener(handler);
		ctl.addKeyListener(handler);
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
		this.library = getCuinaProject().getService(CommandLibrary.class);
		this.tree = new CommandTree(list, library);
	}

	@Override
	protected boolean applySave()
	{
		list.commands = tree.toArray();
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
	
	private CommandNode getSelectedNode()
	{
		Object item = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
		if (item instanceof Item)
			return ((Item) item).node;
		else
			return null;
	}
	
	private class EventHandler implements IDoubleClickListener, KeyListener
	{
		@Override
		public void doubleClick(DoubleClickEvent event)
		{
			CommandNode node = getSelectedNode();
			if (node == null) return;
			
			CommandDialog dialog = new CommandDialog(FlowEditor.this, node.getCommand());
			int result = dialog.open();
			if (result == TitleAreaDialog.OK)
			{
//					System.out.println("Command erstellt: " + dialog.getCommand());
				viewer.refresh();
				setDirty(true);
			}
		}

		@Override
		public void keyPressed(KeyEvent e)
		{
			if (e.keyCode == SWT.DEL)
			{
				CommandNode node = getSelectedNode();
				if (node == null) return;
				
				node.getParent().removeChild(node);
				viewer.refresh();
				setDirty(true);
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {}
	}

	@Override
	public void addCommand(Command cmd)
	{
		CommandNode node = getSelectedNode();
		if (node != null)
			node.getParent().insertBefore(node, cmd);
		else
			tree.addChild(new CommandNode(tree, tree, cmd));
		viewer.refresh();
		setDirty(true);
	}
}