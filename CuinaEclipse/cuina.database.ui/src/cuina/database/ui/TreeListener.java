package cuina.database.ui;

import cuina.database.ui.tree.TreeNode;
import cuina.database.ui.tree.TreeRoot;


public interface TreeListener
{
	public void nodesChanged(Object source, TreeRoot root, TreeNode[] nodes);
	public void nodesAdded(Object source, TreeRoot root, TreeNode[] nodes);
	public void nodesRemoved(Object source, TreeRoot root, TreeNode[] nodes);
}
