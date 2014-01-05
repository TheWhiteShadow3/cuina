package cuina.editor.eventx.internal;

import cuina.editor.eventx.internal.prefs.EventPreferences;
import cuina.eventx.Command;

import java.util.HashMap;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;

public class FlowLabelProvider extends LabelProvider implements IColorProvider
{
	private static String INDENT;

	private HashMap<String, Color> colors = new HashMap<String, Color>();
	
	static
	{
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < EventPreferences.getIndentWidth(); i++) builder.append(' ');
		INDENT = builder.toString();
	}
	
	private CommandLibrary library;
	
	public FlowLabelProvider(CommandLibrary library)
	{
		this.library = library;
	}
	
	
	@Override
	public String getText(Object element)
	{
		if (element instanceof CommandNode)
		{
			CommandNode node = (CommandNode) element;
			Command cmd = node.getCommand();
			if (cmd == null)
			{
				throw new NullPointerException("Node hast no command.");
			}
			
			String indentStr = getIndentString(cmd.indent);
			if (node.getType() == CommandNode.BLOCK_END) return indentStr + "end";
			if (node.getType() == CommandNode.MARK) return indentStr + "+";

			String name;
			FunctionEntry func = library.getFunction(cmd);
			if (func != null)
				name = func.label != null ? func.label : func.name;
			else
				name = cmd.name;
			
			StringBuilder builder = new StringBuilder(32);
			builder.append(indentStr).append(name);
			if (cmd.args != null)
			{
				builder.append('(');
				
				if (cmd.args instanceof Object[])
				{
					Object[] args = cmd.args;
					for (int i = 0; i < args.length; i++)
					{
						if (i > 0) builder.append(", ");
						builder.append(args[i]);
					}
				}
				else
				{
					builder.append(cmd.args);
				}
				builder.append(')');
			}
			
			return builder.toString();
		}
		return super.getText(element);
	}

	private String getIndentString(int indent)
	{
		if (indent == 0) return "";
		
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < indent; i++)
		{
			builder.append(INDENT);
		}
		return builder.toString();
	}
	
	@Override
	public Color getForeground(Object element)
	{
		if (element instanceof CommandNode)
		{
			String key = ((CommandNode) element).getColorKey();
			Color color = colors.get(key);
			if (color == null || !EventPreferences.getColor(key).equals(color))
			{
				color = EventPreferences.getColor(key);
				Color oldColor = colors.put(key, color);
				if (oldColor != null) oldColor.dispose();
			}
			return color;
		}
		return null;
	}

	@Override
	public Color getBackground(Object element)
	{
		return null;
	}
	
	@Override
	public void dispose()
	{
		for (Color c : colors.values()) c.dispose();
		super.dispose();
	}
}