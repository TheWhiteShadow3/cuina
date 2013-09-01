package cuina.database.ui.internal.tree;



class FixDataNode extends AbstractDataNode
{
	transient private Object data; 
	
	public FixDataNode(TreeDataNode dataRoot, Object data)
	{
		this.dataRoot = dataRoot;
		this.data = data;
	}
	
	@Override
	public Object getData()
	{
		return data;
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void move(TreeGroup group, int index)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void copy(TreeGroup group)
	{
		throw new UnsupportedOperationException();
	}
}