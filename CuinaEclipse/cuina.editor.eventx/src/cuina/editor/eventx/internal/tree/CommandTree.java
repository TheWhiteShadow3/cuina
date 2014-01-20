package cuina.editor.eventx.internal.tree;

import cuina.editor.eventx.internal.CommandLibrary;
import cuina.eventx.Command;
import cuina.eventx.CommandList;

import java.util.ArrayList;
import java.util.List;

public class CommandTree extends CommandNode
{
	private CommandList list;
	private CommandLibrary library;
	
	public CommandTree(CommandList list, CommandLibrary library)
	{
		this.list = list;
		this.library = library;
		createTree();
	}

	private void createTree()
	{
		int indent = 0;
		CommandNode parent = this;
		CommandNode lastNode = null;
		for(Command cmd : list.commands)
		{
			if (cmd.indent > indent) parent = lastNode;
			if (cmd.indent < indent) parent = parent.getParent();
			indent = cmd.indent;
			
			lastNode = new CommandNode(this, parent, cmd);
			parent.addChild(lastNode);
		}
	}
	
	public Command[] toArray()
	{
		List<Command> commands = new ArrayList<Command>();
		fillList(this, 0, commands);
		return commands.toArray(new Command[commands.size()]);
	}
	
	private void fillList(CommandNode parent, int indent, List<Command> items)
	{
		for(CommandNode child : parent.getChildren())
		{
			Command cmd = child.getCommand();
			cmd.indent = indent;
			items.add(cmd);
			
			fillList(child, indent+1, items);
		}
	}

	public CommandLibrary getLibrary()
	{
		return library;
	}
}
