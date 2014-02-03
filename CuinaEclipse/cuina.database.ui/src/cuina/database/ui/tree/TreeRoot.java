package cuina.database.ui.tree;

import cuina.database.DataTable;
import cuina.database.Database;
import cuina.database.DatabasePlugin;
import cuina.database.ui.internal.tree.TreeDataNode;
import cuina.database.ui.internal.tree.TreeGroup;
import cuina.resource.ResourceException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class TreeRoot extends TreeGroup
{
	private transient DataTable<?> table;
//	private transient ITreeContentProvider contentProvider;

	TreeRoot()
	{
		super("");
		root = this;
	}

	public TreeRoot(DataTable<?> table)
	{
		this();
		if (table == null) throw new NullPointerException("table must be not null.");
		setTable(table, true);
	}

	public void setTable(DataTable<?> table)
	{
		setTable(table, false);
	}
	
	private void setTable(DataTable<?> table, boolean noCheck)
	{
		this.table = table;

		for (String key : table.keySet())
		{
			if (noCheck || findLeaf(key) == null)
			{
				addChild(new TreeDataNode(key), -1);
			}
		}
	}

	@Override
	public void move(TreeGroup newParent, int index)
	{
		throw new UnsupportedOperationException("Can not move Root.");
	}

	@Override
	public DataTable<?> getTable()
	{
		return table;
	}
	
//	public ITreeContentProvider getContentProvider()
//	{
//		if (contentProvider == null)
//		{
//			Class clazz = DatabasePlugin.getDescriptor(table.getName()).getContentProviderClass();
//			if (clazz != null) try
//			{
//				contentProvider = (ITreeContentProvider) clazz.newInstance();
//			}
//			catch (InstantiationException | IllegalAccessException e)
//			{
//				throw new RuntimeException(e);
//			}
//		}
//		return contentProvider;
//	}
	
	public void saveTree() throws ResourceException
	{
		if (table == null) throw new IllegalStateException("Tree is not attached to a Table.");
		
		String key = TREE_META_KEY + '.' + table.getName();
		Database db = table.getDatabase();
		db.setMetaData(key, this);
		db.saveMetaData();
	}
	
	public void validate()
	{
		if (table == null) return;
		
		validateChildren(this);
	}
	
	private void validateChildren(TreeGroup group)
	{
		TREE_LOOP:
		for (TreeNode child : group.getChildren())
		{
			if (child instanceof TreeGroup)
				validateChildren((TreeGroup) child);
			
			if (child instanceof TreeDataNode)
			{
				TreeDataNode tdn = (TreeDataNode) child;
				Object data = tdn.getData();
				if (data == null)
				{
					IFile file = DatabasePlugin.getTableFile(table);
					try
					{
						IMarker[] markers = file.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_ZERO);
						for (IMarker m : markers)
						{
							if (m.getAttribute(IMarker.LOCATION).equals(tdn.getKey()))
								continue TREE_LOOP;
						}
						
						IMarker marker = file.createMarker(IMarker.PROBLEM);
						marker.setAttribute(IMarker.LOCATION, tdn.getKey());
						marker.setAttribute(IMarker.MESSAGE,
								"Objekt '" + tdn.getKey() + "' existiert nicht in der Tabelle.");
					}
					catch (CoreException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}
}
