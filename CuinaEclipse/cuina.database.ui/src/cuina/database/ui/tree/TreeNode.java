package cuina.database.ui.tree;

import cuina.database.DataTable;
import cuina.database.NamedItem;
import cuina.database.ui.internal.tree.TreeGroup;

import org.eclipse.core.runtime.IAdaptable;

public interface TreeNode extends IAdaptable, NamedItem
{
	public static final String TREE_META_KEY = "cuina.database.ui.tree";
	
	public TreeNode getParent();

	public void setParent(TreeNode node);

	public DataTable<?> getTable();

	public void remove();

	public void move(TreeGroup group, int index);

	public void copy(TreeGroup group);

	public boolean isAncestor(TreeNode child);

	public boolean isDescendant(TreeNode parent);
	
	public TreeRoot getRoot();
	
	public boolean hasChildren();
	
	public TreeNode[] getChildren();
}
