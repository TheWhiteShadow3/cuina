package cuina.database.ui.tree;

import cuina.database.DataTable;
import cuina.database.Database;
import cuina.database.DatabasePlugin;
import cuina.database.ui.internal.tree.TreeDataNode;
import cuina.database.ui.internal.tree.TreeGroup;
import cuina.resource.ResourceException;

import org.eclipse.jface.viewers.ITreeContentProvider;

public class TreeRoot extends TreeGroup
{
	private transient DataTable<?> table;
	private transient ITreeContentProvider contentProvider;

	TreeRoot()
	{
		super("");
		root = this;
	}

	public TreeRoot(DataTable table)
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
	public DataTable getTable()
	{
		return table;
	}
	
	public ITreeContentProvider getContentProvider()
	{
		if (contentProvider == null)
		{
			Class clazz = DatabasePlugin.getDescriptor(table.getName()).getContentProviderClass();
			if (clazz != null) try
			{
				contentProvider = (ITreeContentProvider) clazz.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e)
			{
				throw new RuntimeException(e);
			}
		}
		return contentProvider;
	}
	
	public void saveTree() throws ResourceException
	{
		if (table == null) throw new IllegalStateException("Tree is not attached to a Table.");
		
		String key = TREE_META_KEY + '.' + table.getName();
		Database db = table.getDatabase();
		db.setMetaData(key, this);
		db.saveMetaData();
	}
}
