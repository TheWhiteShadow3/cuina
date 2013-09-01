package cuina.editor.script.ruby.ast;

import cuina.editor.script.internal.ruby.SourceData;

public class ElseNode extends BlockNode
{
	public ElseNode(SourceData position)
	{
		super(position);
	}

	public NodeType getNodeType()
    {
        return NodeType.ELSE_NODE;
    }
	
	public String toString()
	{
		return "else " + super.toString();
	}
}
