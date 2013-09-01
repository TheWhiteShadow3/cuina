package cuina.database.ui.internal.tree;

import cuina.database.DataTable;
import cuina.database.DatabaseObject;
import cuina.database.ui.DatabaseUtil;
import cuina.database.ui.properties.DataPropertySource;
import cuina.editor.core.ObjectUtil;

import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertySource;

public class TreeDataNode extends AbstractDataNode
{
	private String key;
	
	public TreeDataNode(String key)
	{
		this.dataRoot = this;
		this.key = key;
	}

	@Override
	public DatabaseObject getData()
	{
		DataTable<?> table = getTable();
		
		if (table == null)
			return null;
		else
			return table.get(key);
	}

	public void updateData(DatabaseObject obj)
	{
		getTable().update(key, obj);
	}

	public void changeKey(String newKey)
	{
		this.key = getTable().changeKey(key, newKey);
	}

	@Override
	public String getName()
	{
		DatabaseObject obj = getData();
		if (obj == null) return key + " (invalid!)";
		
		return obj.getName();
	}
	
    public String getKey()
    {
        return key;
    }

	@Override
	public void remove()
	{
		getTable().remove(key);
		TreeGroup parent = (TreeGroup) getParent();
		if (parent != null) parent.removeChild(this);
	}

	@Override
	public void move(TreeGroup newParent, int index)
	{
		if (newParent == null) throw new NullPointerException("Parent must not be null.");
		if (newParent.getTable() != getTable())
			throw new IllegalArgumentException("Can only be moved within a table.");

		TreeGroup parent = (TreeGroup) getParent();
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
		if (newParent == null) throw new NullPointerException("Parent must not be null.");
		
		DatabaseObject copy = ObjectUtil.clone(getData());
		copy.setName(copy.getName() + "_Copy");
		newParent.addObject(newParent.getTable().createAviableKey(key), copy);
	}

	@Override
	public Object getAdapter(Class adapter)
	{
		if (getRoot() == null) return null;
		
		if (adapter == getTable().getElementClass() || adapter == DatabaseObject.class)
			return getData();
		else if (adapter == IPropertySource.class)
			return new DataPropertySource(getData());
		else if (adapter == IWorkbenchAdapter.class)
            return DatabaseUtil.getWorkbenchAdapter();
		return null;
	}
}
