package cuina.database.ui;

import cuina.database.DataTable;
import cuina.database.ui.DatabaseUtil.ActionProvider;
import cuina.database.ui.tree.TreeNode;
import cuina.database.ui.tree.TreeRoot;
import cuina.resource.ResourceException;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.navigator.CommonActionProvider;

public class DatabaseActionProvider extends CommonActionProvider implements TreeListener
{
	private ActionProvider provider;
	
	public DatabaseActionProvider()
	{
	}

	@Override
	public void fillContextMenu(IMenuManager menu)
	{
		fill(menu);
	}

	@Override
	public void fillActionBars(IActionBars actionBars)
	{
		fill(actionBars.getMenuManager());
	}
	
	private void fill(IMenuManager menu)
	{
		if (provider == null)
		{
			provider = DatabaseUtil.getDefaultActions(getActionSite().getStructuredViewer());
			provider.addDataChangeListener(this);
			provider.enableEditorOpenAction();
			provider.enableEditorActions();
			provider.enableClipboardActions();
			provider.enableDragAndDrop();
		}
		
		provider.fillActions(menu);
	}

	private void treeChanged(TreeRoot root)
	{
		DataTable<?> table = root.getTable();
		try
		{
			table.getDatabase().saveTable(table);
			root.saveTree();
		}
		catch (ResourceException e)
		{
			e.printStackTrace();
		}
		getActionSite().getStructuredViewer().refresh();
	}

	@Override
	public void nodesChanged(Object source, TreeRoot root, TreeNode[] nodes)
	{
		treeChanged(root);
	}

	@Override
	public void nodesAdded(Object source, TreeRoot root, TreeNode[] nodes)
	{
		treeChanged(root);
		getActionSite().getStructuredViewer().setSelection(new StructuredSelection(nodes[0]), true);
	}

	@Override
	public void nodesRemoved(Object source, TreeRoot root, TreeNode[] nodes)
	{
		// Einfachheitshalber wird die Auswahl ganz entfernt, für den Fall das ein Element dort gelöscht wird.
		getActionSite().getStructuredViewer().setSelection(StructuredSelection.EMPTY);
		treeChanged(root);
	}
}
