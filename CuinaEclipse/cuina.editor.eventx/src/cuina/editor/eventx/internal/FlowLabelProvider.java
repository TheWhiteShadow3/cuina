package cuina.editor.eventx.internal;

import cuina.editor.eventx.internal.FlowContentProvider.Item;
import cuina.editor.eventx.internal.prefs.EventPreferences;
import cuina.eventx.Command;

import java.lang.reflect.Array;
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
		if (element instanceof Item)
		{
			Item item = (Item) element;
			
			String indentStr = getIndentString(item.indent);
			if (item.type == Item.BLOCK_END) return indentStr + "end";
			if (item.type == Item.MARK) return indentStr + "+";
			
			Command cmd = ((Item) element).getCommand();
			assert cmd != null;

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
				for (int i = 0; i < cmd.args.length; i++)
				{
					if (i > 0) builder.append(", ");
					Object arg = cmd.args[i];
					if (arg.getClass().isArray())
						appendArray(builder, arg);
					else
						builder.append(arg);
				}
				builder.append(')');
			}
			
			return builder.toString();
		}
		return super.getText(element);
	}
	
	private void appendArray(StringBuilder builder, Object array)
	{
		builder.append('[');
		for (int i = 0, n = Array.getLength(array); i < n; i++)
		{
			if (i > 0) builder.append(", ");
			Object arg = Array.get(array, i);
			if (arg.getClass().isArray())
				appendArray(builder, arg);
			else
				builder.append(arg);
		}
		builder.append(']');
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
		if (element instanceof Item)
		{
			String key = ((Item) element).colorKey;
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