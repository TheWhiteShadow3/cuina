package cuina.editor.script.ruby.ast;

import cuina.editor.script.internal.ruby.SourceData;

public class IfNode extends BlockNode implements IParameter
{
	private Node argument;
	private Node elseNode;
	
	public IfNode(SourceData position)
	{
		super(position);
	}

	@Override
	public NodeType getNodeType()
    {
        return NodeType.IF_NODE;
    }

	@Override
	public void setArgument(Node arg)
	{
		this.argument = arg;
		((AbstractNode) arg).parent = this;
	}

	@Override
	public Node getArgument()
	{
		return argument;
	}

	public Node getElseNode()
	{
		return elseNode;
	}

	public void setElseNode(Node elseNode)
	{
		this.elseNode = elseNode;
		((AbstractNode) elseNode).parent = this;
	}
	
	@Override
	public String toString()
	{
        StringBuilder builder = new StringBuilder(128);
        builder.append("if ").append(argument).append(" then ");

        for (Node child : super.getChildren())
        {
            builder.append("\n\t").append(child);
        }
        
        if (elseNode != null)
        {
        	builder.append("\nelse " + elseNode);
        }
        else
        	builder.append("\nend\n");

        return builder.toString();
	}

	@Override
	public void addArgument(Node e)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void removeArgument(int index)
	{
		throw new UnsupportedOperationException();
	}
}
