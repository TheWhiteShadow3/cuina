package cuina.editor.eventx.internal.tree;

import cuina.editor.eventx.internal.CommandLibrary;
import cuina.eventx.Command;
import cuina.eventx.CommandList;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class CommandTree extends CommandNode
{
	private CommandList list;
	private CommandLibrary library;
	private IFile file;
	
	public CommandTree(IFile file, CommandList list, CommandLibrary library)
	{
		this.file = file;
		this.list = list;
		this.library = library;
		createTree();
	}

	private void createTree()
	{
		int indent = 0;
		CommandNode parent = this;
		CommandNode lastNode = null;
		List<CommandNode> nodes = new ArrayList<CommandNode>();
		for(int i = 0; i < list.commands.length; i++)
		{
			Command cmd = list.commands[i];
			
			if (cmd.indent > indent) parent = lastNode;
			if (cmd.indent < indent) parent = parent.getParent();
			indent = cmd.indent;
			
			lastNode = new CommandNode(this, parent, cmd);
			nodes.add(lastNode);
			parent.addChild(lastNode);
		}
		
		try
		{
			IMarker[] markers = file.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_ZERO);
			for(IMarker m : markers)
			{
				if (!list.getKey().equals(m.getAttribute(IMarker.LOCATION))) continue;

				Integer i = (Integer) m.getAttribute("index");
				if (i != null)
					nodes.get(i).setValid(false);
			}
		}
		catch (CoreException e)
		{
			e.printStackTrace();
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
