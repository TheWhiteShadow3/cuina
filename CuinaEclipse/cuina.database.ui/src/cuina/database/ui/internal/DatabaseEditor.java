package cuina.database.ui.internal;

import cuina.database.DataTable;
import cuina.database.Database;
import cuina.database.DatabaseInput;
import cuina.database.DatabaseObject;
import cuina.database.DatabasePlugin;
import cuina.database.IDatabaseDescriptor;
import cuina.database.ui.DatabaseViewer;
import cuina.database.ui.TreeListener;
import cuina.database.ui.internal.tree.TreeDataNode;
import cuina.database.ui.internal.tree.TreeGroup;
import cuina.database.ui.tree.DataNode;
import cuina.database.ui.tree.TreeNode;
import cuina.database.ui.tree.TreeRoot;
import cuina.editor.core.CuinaCore;
import cuina.editor.core.CuinaProject;
import cuina.resource.ResourceException;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.osgi.framework.Bundle;

public class DatabaseEditor extends EditorPart implements IPropertyListener, TreeListener
{
	private DataTable<?> table;
	private String key;
	private DatabaseViewer viewer;
	private TreeDataNode currentLeaf;
	private boolean dirty;
	private boolean update;
	private Composite editorBlock;
	private Map<String, EditorEntry> editors = new HashMap<String, EditorEntry>();
	private EditorPart currentEditor;
	private Class<EditorPart> editorClass;
	
	private void setDirty(boolean value)
	{
		if (dirty == value) return;
		
		this.dirty = value;
		firePropertyChange(PROP_DIRTY);
	}
	
	@Override
	public void doSave(IProgressMonitor monitor)
	{
		dirty = false;
		for(EditorEntry entry : editors.values())
		{
			entry.editor.doSave(monitor);
			dirty |= entry.editor.isDirty();
		}
		setDirty(dirty);
		viewer.refresh();
	}

	@Override
	public void doSaveAs()
	{
		currentEditor.doSaveAs();
		viewer.refresh();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		setSite(site);
		setInput(input);
		readInput(input);
		loadEditor();
	}

	private void loadEditor() throws PartInitException
	{
		try
		{
			IDatabaseDescriptor<?> descriptor = DatabasePlugin.getDescriptor(table.getName());
			if (descriptor.getEditorID() != null)
			{
		        IConfigurationElement[] elements = Platform.getExtensionRegistry().
		        		getConfigurationElementsFor("org.eclipse.ui.editors");
				for(IConfigurationElement conf : elements)
				{
					if (conf.getAttribute("id").equals(descriptor.getEditorID()))
					{
						Bundle plugin = Platform.getBundle(conf.getContributor().getName());
						
						this.editorClass = (Class<EditorPart>) plugin.loadClass(conf.getAttribute("class"));
						return;
					}
				}
			}
		}
		catch (Exception e)
		{
			throwPartInitException(e);
		}
	}
	
	private EditorEntry getEditorEntry(DatabaseInput input)
	{
		EditorEntry entry = editors.get(input.getKey());
		if (entry == null)
		{
			entry = new EditorEntry(editorBlock, input);
			Assert.isNotNull(entry);
			editors.put(input.getKey(), entry);
		}
		return entry;
	}

	@Override
	public boolean isDirty()
	{
		return dirty & ((currentEditor != null) ? currentEditor.isDirty() : false);
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		return (currentEditor != null) ? currentEditor.isSaveAsAllowed() : false;
	}

	@Override
	public void createPartControl(Composite parent)
	{
		SashForm splitter = new SashForm(parent, SWT.HORIZONTAL | SWT.SMOOTH);
		createNavigator(splitter);
		createEditorBlock(splitter);
		splitter.setWeights(new int[] {25, 75});
	
		String metaKey = TreeNode.TREE_META_KEY + '.' + table.getName();
		TreeRoot root = (TreeRoot) table.getDatabase().getMetaData(metaKey);
		if (key == null)
		{
			TreeDataNode node = findFirstLeaf(root);
			if (node != null) this.key = node.getKey();
		}
		
		if (key != null)
		{
			DataNode dataNode = root.findLeaf(key);
			if (dataNode != null)
				viewer.setSelection(new StructuredSelection(dataNode), true);
		}
	}
	
	private void createNavigator(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		
		this.viewer = new DatabaseViewer(composite, table.getName(), false);
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
				}
			}
		});
	}
	
	private void createEditorBlock(Composite parent)
	{
		this.editorBlock = new Composite(parent, SWT.NONE);
		editorBlock.setLayout(new StackLayout());
	}
	
	private TreeDataNode findFirstLeaf(TreeGroup group)
	{
		for (TreeNode child : group.getChildren())
		{
			if (child instanceof TreeDataNode)
				return (TreeDataNode) child;
			
			if (child instanceof TreeGroup)
				return findFirstLeaf((TreeGroup) child);
		}
		return null;
	}
	
	private void changeElement(TreeDataNode node)
	{
		if (node == currentLeaf) return;

		currentLeaf = node;
		DatabaseObject obj = (currentLeaf == null) ? null : currentLeaf.getData();
		if (obj != null)
		{
			this.key = currentLeaf.getKey();
			initEditor(new DatabaseInput(table, key));
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
	
	private void initEditor(DatabaseInput input)
	{
		update = true;
		
		System.out.println("[DatabaseEditor] Aktiviere Editor für " + input.getKey());
		EditorEntry entry = getEditorEntry(input);
		this.currentEditor = entry.editor;
		entry.activate();
		
		update = false;
	}

	private void readInput(IEditorInput input) throws PartInitException
	{
		if (input instanceof DatabaseInput)
		{
			DatabaseInput dbInput = (DatabaseInput) input;
			this.table = (DataTable<?>) dbInput.getAdapter(DataTable.class);
			this.key = dbInput.getKey();
		}
		else if (input instanceof IAdaptable)
		{
			IFile file = (IFile) input.getAdapter(IFile.class);
			if (file == null) return;
			
			try
			{
				CuinaProject cuinaProject = CuinaCore.getCuinaProject(file.getProject());
				this.table = cuinaProject.getService(Database.class).loadTable(file);
			}
			catch (ResourceException e)
			{
				throwPartInitException(e);
			}
		}
	}
	
	private void throwPartInitException(Exception cause) throws PartInitException
	{
		throw new PartInitException(
				new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Editor could not be created.", cause));
	}

	@Override
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}
	
	public void addDataChangeListener(TreeListener l)
	{
		viewer.addDataChangeListener(l);
	}

	public void removeDataChangeListener(TreeListener l)
	{
		viewer.removeDataChangeListener(l);
	}

	public DataTable<?> getTable()
	{
		return table;
	}

	public CuinaProject getProject()
	{
		return table.getDatabase().getProject();
	}
	
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
				editors.remove(currentLeaf.getKey()).dispose();
				changeElement(null);
				break;
			}
		}
		setDirty(true);
	}
	@Override
	public void dispose()
	{
		for (EditorEntry entry : editors.values()) entry.dispose();
		super.dispose();
	}

	@Override
	public void propertyChanged(Object source, int propId)
	{
		switch(propId)
		{
			case IEditorPart.PROP_TITLE: setPartName(currentEditor.getPartName()); break;
			case IEditorPart.PROP_DIRTY: setDirty(dirty | currentEditor.isDirty()); break;
		}
	}
	
	private class EditorEntry
	{
		public EditorPart editor;
		
		private Composite parent;
		private Composite control;
		
		public EditorEntry(Composite parent, DatabaseInput input)
		{
			try
			{
				this.editor = editorClass.newInstance();
				editor.init(getEditorSite(), input);
				
				this.parent = parent;
				this.control = new Composite(parent, SWT.NONE);
				control.setLayout(new FillLayout());
				editor.createPartControl(control);
				editor.addPropertyListener(DatabaseEditor.this);
				
				activate();
			}
			catch (InstantiationException | IllegalAccessException | PartInitException e)
			{
				e.printStackTrace();
			}
		}
		
		public void dispose()
		{
			editor.removePropertyListener(DatabaseEditor.this);
			editor.dispose();
			control.dispose();
		}

		public void activate()
		{
			((StackLayout) parent.getLayout()).topControl = control;
			parent.layout(true, true);
//			parent.getParent().l
		}
	}
}
