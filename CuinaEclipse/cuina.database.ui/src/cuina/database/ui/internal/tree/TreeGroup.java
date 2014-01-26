package cuina.database.ui.internal.tree;

import cuina.database.DataTable;
import cuina.database.DatabaseObject;
import cuina.database.ui.DatabaseUtil;
import cuina.database.ui.tree.DataNode;
import cuina.database.ui.tree.TreeNode;
import cuina.database.ui.tree.TreeRoot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.ui.model.IWorkbenchAdapter;

public class TreeGroup implements TreeNode
{
	transient private TreeGroup parent;
    transient protected TreeRoot root;
    private String name;
    private ArrayList<TreeNode> children;

    public TreeGroup(String name)
    {
        this.name = name;
    }

	public TreeGroup addGroup(String name)
	{
		if (name == null || name.isEmpty()) throw new IllegalArgumentException("name=" + name);

		TreeGroup group = new TreeGroup(name);
		addChild(group, -1);
		return group;
	}
	
    public TreeDataNode addObject(DatabaseObject obj)
    {
        return addObject(obj.getKey(), obj);
    }

	public TreeDataNode addObject(String key, DatabaseObject obj)
	{
		TreeDataNode leaf = new TreeDataNode(key);
		addChild(leaf, -1);
		getTable().put(key, obj);
		return leaf;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void addChild(TreeNode node, int index)
	{
		if (children == null) children = new ArrayList<TreeNode>(8);
		if (index == -1) index = children.size();
		children.add(index, node);
		node.setParent(this);
	}

	public void removeChild(TreeNode node)
	{
		if (children == null) return;
		
		children.remove(node);
		node.setParent(null);
	}

	@Override
	public void remove()
	{
//		if (children != null)
//			for (TreeNode node : children) node.remove();

		if (parent == null) return;
		parent.removeChild(this);
	}

	@Override
	public void move(TreeGroup newParent, int index)
	{
		if (newParent == null) throw new NullPointerException("newParent must not be null.");
		if (newParent.getTable() != getTable())
			throw new IllegalArgumentException("Can only be moved within a table.");

		if (newParent == parent)
		{
			int i = parent.indexOf(this);
			if (i == index) return;
			if (i < index) index--;
		}
		if (parent != null) parent.removeChild(this);
		newParent.addChild(this, index);
	}

	@Override
	public void copy(TreeGroup newParent)
	{
		if (newParent == null) throw new NullPointerException("newParent must not be null.");
		
		TreeGroup copy = newParent.addGroup(getName());
		if (children == null) return;
		
		for (TreeNode node : children)
			node.copy(copy);
	}

	public TreeGroup getChildGroup(String name)
	{
		if (children == null) return null;
		
		for (int i = 0; i < children.size(); i++)
		{
			TreeNode child = children.get(i);
			if ((child instanceof TreeGroup) && ((TreeGroup) child).getName().equals(name)) return (TreeGroup) child;
		}
		return null;
	}
	
	public DataNode findLeaf(String key)
	{
		TreeNode child = null;
		for (int i = 0; i < children.size(); i++)
		{
			child = children.get(i);
			if (child instanceof TreeGroup)
			{
				DataNode leaf = ((TreeGroup) child).findLeaf(key);
				if (leaf != null) return leaf;
			}
			else if (child instanceof TreeDataNode)
			{
				TreeDataNode leaf = (TreeDataNode) child;
				if (leaf.getKey().equals(key)) return leaf;
			}
		}
		return null;
	}

	@Override
	public List<TreeNode> getChildren()
	{
		if (children == null) return Collections.EMPTY_LIST;
				
		return Collections.unmodifiableList(children);
	}

	@Override
	public TreeGroup getParent()
	{
		return parent;
	}

	@Override
	public void setParent(TreeNode parent)
	{
		if (parent != null && !(parent instanceof TreeGroup))
			throw new IllegalArgumentException("parent must be an Instance of TreeGroup.");
		
		this.parent = (TreeGroup) parent;
		this.root = (parent != null) ? parent.getRoot() : null;
	}

	@Override
	public String toString()
	{
		return "Group: '" + getName() + "' (" + children + ")";
	}

	@Override
	public Object getAdapter(Class adapter)
	{
		if (adapter == IWorkbenchAdapter.class)
            return DatabaseUtil.getWorkbenchAdapter();
		// if (adapter == IPropertySource.class) return new
		// GroupPropertySource(this);

		return null;
	}

	@Override
	public DataTable getTable()
	{
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

	public int indexOf(TreeNode child)
	{
		if (children == null) return -1;
		
		return children.indexOf(child);
	}

	@Override
	public boolean hasChildren()
	{
		return children != null && children.size() > 0;
	}
	
	@Override
	public TreeRoot getRoot()
	{
		return root;
	}
}
