package cuina.eventx;

import cuina.database.DatabaseObject;
import cuina.database.ui.TreeItem;
import cuina.database.ui.properties.Property;
import cuina.database.ui.tree.TreeRoot;

public class CommandList implements DatabaseObject, TreeItem
{
	private static final long serialVersionUID = -7940627382487928432L;

	public Command[] commands;
	
	@Property(name="Key", readonly=true)
	private String key;
	@Property(name="Name")
	private String name;
	
	public CommandList()
	{
		this.commands = new Command[0];
	}
	
	public CommandList(String key, Command... cmds)
	{
		this.key = key;
		this.commands = cmds;
	}
	
	@Override
	public String getKey()
	{
		return key;
	}

	@Override
	public void setKey(String key)
	{
		this.key = key;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}
	
	@Override
	public boolean hasChildren()
	{
		return commands.length > 0;
	}
	
	@Override
	public Object[] getChildren(TreeRoot root)
	{
		return commands;
	}
}
