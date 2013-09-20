package cuina.database.ui.internal;

import cuina.database.DataTable;
import cuina.database.Database;
import cuina.database.ui.tree.TreeNode;
import cuina.database.ui.tree.TreeRoot;
import cuina.editor.core.CuinaCore;
import cuina.resource.ResourceException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class DataContentProvider implements ITreeContentProvider
{
	private static final Object[] EMPTY = new Object[0];
	
	private Viewer viewer;
	private TreeRoot root;
	private DataTable table;

	public DataTable getTable()
	{
		return table;
	}
	
	public TreeRoot getRoot()
	{
		return root;
	}

	@Override
	public void dispose()
	{
		this.table = null;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		if (oldInput == newInput) return;
		this.root = null;
		this.viewer = viewer;
	}
	
	private TreeRoot init(IFile file)
	{
		if (file == null) throw new NullPointerException("file is null");

		try
		{
			DataTable<?> oldTable = table;
			
			Database db = getDatabase(file);
			this.table = db.loadTable(file);
			
			String key = TreeNode.TREE_META_KEY + '.' + table.getName();
			this.root = (TreeRoot) db.getMetaData(key);
			if (root != null)
			{
				root.setTable(table);
			}
			else
			{
				root = new TreeRoot(table);
				root.saveTree();
			}
			ITreeContentProvider tcp = root.getContentProvider();
			if (tcp != null) tcp.inputChanged(viewer, oldTable, table);
		}
		catch (ResourceException e)
		{
			e.printStackTrace();
		}
		return root;
	}
	
	private Database getDatabase(IFile file)
	{
		return CuinaCore.getCuinaProject(file.getProject()).getService(Database.class);
	}

	@Override
	public Object[] getElements(Object element)
	{
		return getChildren(element);
	}
	
	@Override
	public Object[] getChildren(Object parent)
	{
		if (parent instanceof TreeNode)
		{
			TreeNode node = (TreeNode) parent;
			TreeNode[] children = node.getChildren();
//			if (children == null)
//			{
//				Object[] childElements = findChildren(node.data);
//				children = new Node[childElements.length];
//				for(int i = 0; i < children.length; i++)
//				{
//					children[i] = new Node(node, childElements[i]);
//				}
//				node.children = children;
//			}
			return children;
		}
		// parent ist ein Wurzel-Element
		if (parent instanceof DataTable)
		{
			this.table = (DataTable) parent;
			this.root = new TreeRoot(table);
		}
		else if (parent instanceof IAdaptable)
		{
			IFile file = (IFile) ((IAdaptable) parent).getAdapter(IFile.class);
			this.root = init(file);
		}
		if (root == null)
			return EMPTY;
		else
			return new Object[] { root };
	}
	
//	private Object[] findChildren(Object element)
//	{
//		try
//		{
//			if (element instanceof TreeGroup)
//			{
//				return ((TreeGroup) element).getChildren();
//			}
//			else if (element instanceof IFolder)
//			{
//				return ((IFolder) element).members();
//			}
//			
//			Object obj = element;
//			if (element instanceof TreeLeaf)
//				obj = ((TreeLeaf) element).getData();
//			
//			if (obj instanceof TreeItem)
//			{
//				Object[] children = ((TreeItem) obj).getChildren(root);
//				if (children != null) return children;
//			}
//		}
//		catch(CoreException e) {}
//		return EMPTY;
//	}

	@Override
	public Object getParent(Object element)
	{
		if (element instanceof TreeNode)
		{
			return ((TreeNode) element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element)
	{
		if (element instanceof TreeNode)
		{
			return ((TreeNode) element).hasChildren();
		}
		return false;
	}
}
