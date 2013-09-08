package cuina.editor.script.ruby.ast;

import cuina.editor.script.internal.ruby.SourceData;

/**
 * Stellt ein Block-Element da.
 * @author TheWhiteShadow
 */
public class BlockNode extends ListNode
{
	public BlockNode(SourceData position)
	{
		super(position);
	}
	
	@Override
	public String toString()
	{
		return blocktToString();
	}

	protected String blocktToString()
	{
        StringBuilder builder = new StringBuilder(256);
        builder.append("[");

        for (Node child : super.getChildren())
        {
            builder.append("\n\t").append(child);
        }
        builder.append("\n]");

        return builder.toString();
	}
}
