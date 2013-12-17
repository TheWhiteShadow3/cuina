package cuina.editor.eventx.internal;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;

import cuina.eventx.Command;

public class FlowLabelProvider extends LabelProvider implements IColorProvider
{
	@Override
	public String getText(Object element)
	{
		if (element instanceof CommandNode)
		{
			CommandNode node = (CommandNode) element;
			if (node.getType() == CommandNode.BLOCK_END) return "end";
			if (node.getType() == CommandNode.MARK) return "+";
			
			Command cmd = node.getCommand();
			if (cmd == null)
			{
				throw new NullPointerException("Node hast no command.");
			}

			StringBuilder builder = new StringBuilder(32);
			
			builder.append(cmd.target).append('.').append(cmd.name);
			
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

	@Override
	public Color getForeground(Object element)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getBackground(Object element)
	{
		// TODO Auto-generated method stub
		return null;
	}
}