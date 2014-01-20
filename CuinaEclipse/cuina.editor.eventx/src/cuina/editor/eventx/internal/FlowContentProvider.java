package cuina.editor.eventx.internal;

import cuina.editor.eventx.internal.prefs.EventPreferences;
import cuina.editor.eventx.internal.tree.CommandNode;
import cuina.editor.eventx.internal.tree.CommandTree;
import cuina.eventx.Command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class FlowContentProvider implements IStructuredContentProvider
{
	private static final Object[] EMPTY = new Object[0];
	
	private CommandLibrary library;
	
	public FlowContentProvider(CommandLibrary library)
	{
		this.library = library;
	}
	
	@Override
	public void dispose() {}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
	}

	@Override
	public Object[] getElements(Object element)
	{
		if (element instanceof CommandTree)
		{
			return getFlatRepresentationList((CommandTree) element).toArray();
		}
		return EMPTY;
	}
	
//	private Object[] createEditorCommandList(CommandTree tree)
//	{
//		List<Item> list = new ArrayList<Item>(16);
//		
//		Deque<Item> stack = new LinkedList<>();
//		int indent = 0;
//		for (int i = 0; i < cmdList.commands.length; i++)
//		{
//			Command cmd = cmdList.commands[i];
//			String colorKey = getColor(cmd);
//			CommandNode node = CommandNode.createCommand(cmdList, i, indent, colorKey);
//			
//			list.add(node);
//			
//			if (cmd.indent > indent)
//			{
//				stack.push(node);
//				indent = cmd.indent;
//			}
//			else if (cmd.indent < indent)
//			{
//				CommandNode parent = stack.pop();
//				list.add(CommandNode.createMark(
//						cmdList, i, indent, EventPreferences.CMDLINE_COLOR_DEFAULT));
//				list.add(CommandNode.createBlockEnd(cmdList, parent.getIndex(), indent, colorKey));
//				indent = cmd.indent;
//			}
//		}
//		list.add(CommandNode.createMark(
//				cmdList, cmdList.commands.length - 1, indent, EventPreferences.CMDLINE_COLOR_DEFAULT));
//		
//		return list.toArray();
//	}
	
	public List<Item> getFlatRepresentationList(CommandTree tree)
	{
		List<Item> items = new ArrayList<Item>();
		
		for(CommandNode child : tree.getChildren())
		{
			fillFlatRepresentationList(child, 0, items);
		}
		items.add(new Item(null, 0, EventPreferences.CMDLINE_COLOR_DEFAULT, Item.MARK));
		
		return items;
	}
	
	private void fillFlatRepresentationList(CommandNode node, int indent, List<Item> items)
	{
		String colorKey = getColor(node.getCommand());
		items.add(new Item(node, indent, colorKey, Item.COMMAND));
		
		if (node.getChildren().size() > 0)
		{
			for(CommandNode child : node.getChildren())
			{
				fillFlatRepresentationList(child, indent+1, items);
			}
			items.add(new Item(null, indent+1, EventPreferences.CMDLINE_COLOR_DEFAULT, Item.MARK));
			items.add(new Item(node, indent, colorKey, Item.BLOCK_END));
		}
	}
	
	private String getColor(Command cmd)
	{
		FunctionEntry func = library.getFunction(cmd);
		if (func != null)
		{
			switch(func.category.name)
			{
				case "Demo": return EventPreferences.CMDLINE_COLOR_FUNCTION;
				case "Message": return EventPreferences.CMDLINE_COLOR_FUNCTION;
				case "Default": return EventPreferences.CMDLINE_COLOR_CONTROL;
			}
		}
		return EventPreferences.CMDLINE_COLOR_DEFAULT;
	}
	
	public static class Item
	{
		public static final int COMMAND = 1;
		public static final int MARK = 2;
		public static final int BLOCK_END = 3;
		
		public CommandNode node;
		public int indent;
		public String colorKey;
		public int type;
		
		public Item(CommandNode node, int indent, String colorKey, int type)
		{
			this.node = node;
			this.indent = indent;
			this.colorKey = colorKey;
			this.type = type;
		}
		
		public Command getCommand()
		{
			if (type != COMMAND) return null;
			
			return node.getCommand();
		}
	}
}