package cuina.editor.script.ruby.ast;

import cuina.editor.script.internal.ruby.SourceData;

public class ArrayNode extends ListNode
{
	public ArrayNode(SourceData position)
	{
		super(position);
	}

	public NodeType getNodeType()
    {
        return NodeType.ARRAY_NODE;
    }
	
	public String toString()
	{
        StringBuilder builder = new StringBuilder(256);
        builder.append("[");

        for (Node child : getChilds())
        {
            builder.append(child).append(", ");
        }
        builder.append("]");

        return builder.toString();
	}
}
