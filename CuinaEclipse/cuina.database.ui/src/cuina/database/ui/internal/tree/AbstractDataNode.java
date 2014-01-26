package cuina.database.ui.internal.tree;

import cuina.database.DataTable;
import cuina.database.NamedItem;
import cuina.database.ui.tree.DataNode;
import cuina.database.ui.tree.TreeNode;
import cuina.database.ui.tree.TreeRoot;

import java.util.Collections;
import java.util.List;

public abstract class AbstractDataNode implements DataNode
{
	transient protected TreeDataNode dataRoot;
	transient private TreeRoot root;
	transient private TreeNode parent;

	@Override
	public TreeNode getParent()
	{
		return parent;
	}

	@Override
	public void setParent(TreeNode parent)
	{
		if (parent != null && !(parent instanceof TreeNode))
			throw new IllegalArgumentException("parent must be an Instance of TreeGroup.");
		
		this.parent = parent;
		this.root = (parent != null) ? parent.getRoot() : null;
	}
	
	@Override
	public DataTable getTable()
	{
		if (root == null) return null;
		
		return root.getTable();
	}

	@Override
	public boolean isAncestor(TreeNode child)
	{
		return child.isDescendant(this);
	}

	@Override
	public boolean isDescendant(TreeNode parent)
	{
		if (this.parent == null) return false;
		return (this.parent == parent) ? true : this.parent.isDescendant(parent);
	}

	@Override
	public Object getAdapter(Class adapter)
	{
		if (adapter == TreeNode.class || adapter == DataNode.class) return this;
		
		Object data = getData();
		if (adapter == data.getClass()) return data;
		return null;
	}
	
	@Override
	public TreeRoot getRoot()
	{
		return root;
	}

	@Override
	public String getName()
	{
		Object data = getData();
		if (data instanceof NamedItem) return ((NamedItem) data).getName();
		return data.toString();
	}
	
	@Override
	public String toString()
	{
		return getName();
	}

	@Override
	public boolean hasChildren()
	{
		return false;
	}

	@Override
	public List<TreeNode> getChildren()
	{
		return Collections.EMPTY_LIST;
	}

	@Override
	public TreeDataNode getDataRoot()
	{
		return dataRoot;
	}
}
