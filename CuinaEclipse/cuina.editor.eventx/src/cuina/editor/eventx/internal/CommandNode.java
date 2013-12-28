package cuina.editor.eventx.internal;

import cuina.eventx.Command;
import cuina.eventx.CommandList;

public class CommandNode
{
	public static final int COMMAND = 1;
	public static final int MARK = 2;
	public static final int BLOCK_END = 3;
	
	private CommandList list;
	private int index;
	private int type;
	private String colorKey;
	
	/**
	 * Erstellt einen neuen Command-Knoten.
	 * <p>
	 * Abhängig vom Typ hat der Parameter index unterschiedliche Bedeutung:
	 * <dl>
	 * <dt>COMMAND</dt>
	 * <dd>Index des Knotens zum zugehörigen Command.</dd>
	 * <dt>MARK</dt>
	 * <dd>Index des Knotens, der unmittelbar vor der Markierung steht.</dd>
	 * <dt>BLOCK_END</dt>
	 * <dd>Index des Knotens, der den Block geöffnet hat.</dd>
	 * </dl>
	 * </p>
	 * @param list CommandList.
	 * @param index Index des Knoten, oder des zugehörigen Knotens.
	 * @param type Typ, des Knotens.
	 */
	public CommandNode(CommandList list, int index, String colorKey, int type)
	{
		if (list == null) throw new NullPointerException("list is null.");
		if (colorKey == null) throw new NullPointerException("colorKey is null.");
		if (index < 0 || index >= list.commands.length) throw new IndexOutOfBoundsException();
		
		this.colorKey = colorKey;
		this.list = list;
		this.index = index;
		this.type = type;
	}
	
	public CommandList getList()
	{
		return list;
	}
	
	public int getType()
	{
		return type;
	}
	
	public int getIndex()
	{
		return index;
	}
	
	public Command getCommand()
	{
		return get(index);
	}
	
//	public Object getParent()
//	{
//		return parent;
//	}
//	
//	public List<CommandNode> getChildren()
//	{
//		if (type == MARK || type == BLOCK_END) return Collections.EMPTY_LIST;
//		
//		if (children == null)
//		{
//			if (type == ROOT)
//			{
//				this.children = getListChildren(0);
//			}
//			else
//			{
//				Command cmd = get(index);
//				this.children = getListChildren(cmd.indent + 1);
//			}
//		}
//
//		return children;
//	}
	
	private Command get(int index)
	{
		if (index < 0 || index >= list.commands.length) return null;
		
		return list.commands[index];
	}

	public String getColorKey()
	{
		return colorKey;
	}
	
//	private List<CommandNode> getListChildren(int indent)
//	{
//		List<CommandNode> childList = new ArrayList();
//		
//		Command next;
//		int i = 0;
//		while(i < list.commands.length)
//		{
//			next = get(i);
//			if (next.indent == indent) childList.add(new CommandNode(this, i));
//			i++;
//		}
//		return childList;
//	}
}