package cuina.editor.eventx.internal;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import cuina.eventx.Command;
import cuina.eventx.CommandList;

public class FlowContentProvider implements IStructuredContentProvider
{
	private static final Object[] EMPTY = new Object[0];
	
	@Override
	public void dispose() {}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
	}

	@Override
	public Object[] getElements(Object element)
	{
		if (element instanceof CommandList)
		{
			return createEditorCommandList((CommandList) element);
		}
		return EMPTY;
	}
	
	private Object[] createEditorCommandList(CommandList cmdList)
	{
		List<CommandNode> list = new ArrayList<CommandNode>(cmdList.commands.length + 4);
		
		Deque<CommandNode> stack = new LinkedList<>();
		int indent = 0;
		for (int i = 0; i < cmdList.commands.length; i++)
		{
			CommandNode node = new CommandNode(cmdList, i, CommandNode.COMMAND);
			Command cmd = node.getCommand();
			list.add(node);
			
			if (cmd.indent > indent)
			{
				stack.push(node);
				indent = cmd.indent;
			}
			else if (cmd.indent < indent)
			{
				CommandNode parent = stack.pop();
				list.add(new CommandNode(cmdList, i, CommandNode.MARK));
				list.add(new CommandNode(cmdList, parent.getIndex(), CommandNode.BLOCK_END));
				indent = cmd.indent;
			}
		}
		list.add(new CommandNode(cmdList, cmdList.commands.length-1, CommandNode.MARK));
		
		return list.toArray();
	}
}