package cuina.database.ui.tree;

import cuina.database.ui.internal.tree.TreeDataNode;


public interface DataNode extends TreeNode
{
	public TreeDataNode getDataRoot();
	public Object getData();
}
