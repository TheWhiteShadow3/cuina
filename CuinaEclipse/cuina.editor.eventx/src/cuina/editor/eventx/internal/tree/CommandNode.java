package cuina.editor.eventx.internal.tree;

import cuina.editor.eventx.internal.FunctionEntry;
import cuina.eventx.Command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandNode
{
	private CommandTree tree;
	private Command command;
	private CommandNode parent;
	private List<CommandNode> children;
	private boolean valid = true;
	
	CommandNode() {}
	
	public CommandNode(CommandTree tree, CommandNode parent, Command command)
	{
		if (tree == null || parent == null || command == null) throw new NullPointerException();
		this.tree = tree;
		this.parent = parent;
		this.command = command;
	}

	public CommandNode getParent()
	{
		return parent;
	}

	public Command getCommand()
	{
		return command;
	}
	
	public void addChild(CommandNode node)
	{
		if (children == null)
			children = new ArrayList<CommandNode>();
		children.add(node);
	}
	
	public void insertBefore(CommandNode node, Command cmd)
	{
		int index;
		if (children == null || (index = children.indexOf(node)) == -1)
			throw new NullPointerException("Child does not exists.");
		
		children.add(index, new CommandNode(tree, this, cmd));
	}
	
	public void removeChild(CommandNode node)
	{
		if (children == null) return;
		
		children.remove(node);
	}
	
	public List<CommandNode> getChildren()
	{
		if (children == null) return Collections.EMPTY_LIST;
		
		return children;
	}

	public FunctionEntry getFunction()
	{
		if (command == null) return null;
		
		return tree.getLibrary().getFunction(command);
	}

	public void setValid(boolean value)
	{
		this.valid = value;
	}

	public boolean isValid()
	{
		return valid;
	}
}
