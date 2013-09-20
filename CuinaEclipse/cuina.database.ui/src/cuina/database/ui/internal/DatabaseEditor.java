package cuina.database.ui.internal;

import cuina.database.DataTable;
import cuina.database.Database;
import cuina.database.DatabaseInput;
import cuina.database.DatabaseObject;
import cuina.database.DatabasePlugin;
import cuina.database.IDatabaseDescriptor;
import cuina.database.ui.DataEditorPage;
import cuina.database.ui.DatabaseViewer;
import cuina.database.ui.IDatabaseEditor;
import cuina.database.ui.Toolbox;
import cuina.database.ui.TreeListener;
import cuina.database.ui.internal.tree.TreeDataNode;
import cuina.database.ui.tree.DataNode;
import cuina.database.ui.tree.TreeNode;
import cuina.database.ui.tree.TreeRoot;
import cuina.editor.core.CuinaCore;
import cuina.editor.core.CuinaProject;
import cuina.resource.ResourceException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class DatabaseEditor extends EditorPart implements IDatabaseEditor, TreeListener
{
	private DataTable table;
	private DatabaseViewer viewer;
	private Toolbox<?> toolbox;
	private DataEditorPage page;
	private TreeDataNode currentLeaf;
	private boolean dirty;
	private boolean update;
	
	private void setDirty(boolean value)
	{
		this.dirty = value;
		firePropertyChange(PROP_DIRTY);
	}
	
	@Override
	public void doSave(IProgressMonitor monitor)
	{
		DatabaseObject obj = page.getValue();
		if (obj != null) table.update(obj.getKey(), obj);
		try
		{
			table.getDatabase().saveTable(table);
			table.getDatabase().saveMetaData();
			setDirty(false);
		}
		catch (ResourceException e)
		{
			e.printStackTrace();
		}
		viewer.refresh();
	}

	@Override
	public void doSaveAs()
	{
		doSave(null);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		setSite(site);
		setInput(input);
		readInput(input);
		initPage();
	}

	private void initPage()
	{
		if (table != null) try
		{
			IDatabaseDescriptor descriptor = DatabasePlugin.getDescriptor(table.getName());
			if (descriptor.getEditorClass() != null)
			{
				this.page = (DataEditorPage) descriptor.getEditorClass().newInstance();
			}
			if (descriptor.getToolboxClass() != null)
			{
				this.toolbox = (Toolbox<?>) descriptor.getToolboxClass().newInstance();
			}
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean isDirty()
	{
		return dirty;
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	@Override
	public void createPartControl(Composite parent)
	{
		SashForm splitter = new SashForm(parent, SWT.HORIZONTAL | SWT.SMOOTH);
		createNavigationArea(splitter);
		createEditorPage(splitter);
		splitter.setWeights(new int[] {25, 75});
		
		// wähle das erste Item aus
		if (page != null && table.size() > 0)
		{
			Tree tree = viewer.getTree();
			TreeItem leaf = findFirstLeaf(tree, tree.getItem(0));
			if (leaf != null)
			{
				tree.showItem(leaf);
				tree.select(leaf);
				DatabaseObject obj = ((TreeDataNode) leaf.getData()).getData();
				page.setValue(obj);
			}
		}
	}
	
	private void createNavigationArea(Composite parent)
	{
		Composite navigationComposite = new Composite(parent, SWT.NONE);
		navigationComposite.setLayout(new FillLayout());
		if (toolbox != null)
		{
			SashForm splitter = new SashForm(navigationComposite, SWT.VERTICAL | SWT.SMOOTH);
			this.viewer = createDatabaseViewer(splitter);
			Composite toolboxComposite = new Composite(splitter, SWT.BORDER);
			toolboxComposite.setLayout(new FillLayout());
			toolbox.createToolboxControl(toolboxComposite, page);
			splitter.setWeights(new int[] {40, 60});
		}
		else
		{
			this.viewer = createDatabaseViewer(parent);
		}
	}
	
	private void createEditorPage(Composite parent)
	{
		Composite navigationComposite = new Composite(parent, SWT.NONE);
		navigationComposite.setLayout(new FillLayout());
		
		if (page != null)
		{
			page.createEditorPage(navigationComposite, this);
		}
		else
		{
			Label error = new Label(navigationComposite, SWT.NONE);
			error.setText("No Editor for " + table.getName() + " available.");
		}
	}
	
	private TreeItem findFirstLeaf(Tree tree, TreeItem item)
	{
		if (item.getData() instanceof TreeDataNode)
		{
			return item;
		}
		else
		{
			for(TreeItem i : item.getItems())
			{
				TreeItem found = findFirstLeaf(tree, i);
				if (found != null) return found;
			}
		}
		return null;
	}
	
	private DatabaseViewer createDatabaseViewer(Composite parent)
	{
		DatabaseViewer viewer = new DatabaseViewer(parent, table.getName(), false);
		viewer.addDataChangeListener(this);
		viewer.setInput(getEditorInput());
		viewer.expandToLevel(2);
		viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				TreeNode node = (TreeNode) ((TreeSelection) event.getSelection()).getFirstElement();
				
				if (node instanceof DataNode)
				{
					DataNode dataNode = (DataNode) node;
					changeElement(dataNode.getDataRoot());
					if (dataNode instanceof TreeDataNode) return;
					
					page.setChildValue(dataNode.getData());
				}
			}
		});
		return viewer;
	}
	
	private void changeElement(TreeDataNode node)
	{
		if (node == currentLeaf) return;

		currentLeaf = node;
		DatabaseObject obj = (currentLeaf == null) ? null : currentLeaf.getData();
		if (obj != null)
		{
			update = true;
			page.setValue(obj);
			update = false;
		}
		else
		{	// Ungültiger Schlüssel im Tree
			Shell shell = viewer.getControl().getShell();
			MessageBox msg = new MessageBox(shell, SWT.ICON_ERROR | SWT.YES | SWT.NO);
			msg.setText("invalid Key");
			msg.setMessage("Der Schlüssel " + currentLeaf.getKey() +
					" existiert nicht in der Tabelle.\nSoll der Schlüssel gelöscht werden?");
			if (msg.open() == SWT.YES) try
			{
				currentLeaf.remove();
				currentLeaf = null;
				viewer.refresh();
				table.getDatabase().saveMetaData();
			}
			catch (ResourceException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void readInput(IEditorInput input)
	{
		if (input instanceof DatabaseInput)
		{
			DatabaseInput dbInput = (DatabaseInput) input;
			this.table = (DataTable) dbInput.getAdapter(DataTable.class);
		}
		else if (input instanceof IAdaptable)
		{
			IFile file = (IFile) input.getAdapter(IFile.class);
			if (file == null) return;
			
			setPartName(file.getName());
			try
			{
				CuinaProject cuinaProject = CuinaCore.getCuinaProject(file.getProject());
				this.table = cuinaProject.getService(Database.class).loadTable(file);
			}
			catch (ResourceException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public Toolbox<?> getToolbox()
	{
		return toolbox;
	}

	@Override
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}
	
	@Override
	public void addDataChangeListener(TreeListener l)
	{
		viewer.addDataChangeListener(l);
	}

	@Override
	public void removeDataChangeListener(TreeListener l)
	{
		viewer.removeDataChangeListener(l);
	}

	@Override
	public DataTable getTable()
	{
		return table;
	}

	@Override
	public CuinaProject getProject()
	{
		return table.getDatabase().getProject();
	}
	
	@Override
	public void fireDataChanged(Object source, DatabaseObject item)
	{
		if (update) return;
		
		viewer.refresh();
		if (currentLeaf != null && !getTable().containsKey(currentLeaf.getKey()))
		{
			changeElement(null);
		}
		setDirty(true);
	}

	@Override
	public void nodesChanged(Object source, TreeRoot root, TreeNode[] nodes)
	{
		fireDataChanged(source, null);
	}

	@Override
	public void nodesAdded(Object source, TreeRoot root, TreeNode[] nodes)
	{
		if (nodes.length == 0) return;
		
		for(TreeNode node : nodes)
		{
			if (node instanceof TreeDataNode)
			{
				changeElement((TreeDataNode) node);
				break;
			}
		}
		setDirty(true);
	}

	@Override
	public void nodesRemoved(Object source, TreeRoot root, TreeNode[] nodes)
	{
		if (nodes.length == 0) return;
		
		for(TreeNode node : nodes)
		{
			if (currentLeaf == node)
			{
				changeElement(null);
				break;
			}
		}
		setDirty(true);
	}
}
