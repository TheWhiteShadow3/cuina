package cuina.editor.script.ruby.ast;

import cuina.editor.script.internal.ruby.SourceData;

public class CaseNode extends BlockNode implements IParameter
{
	private Node argument;
	
	public CaseNode(SourceData position)
	{
		super(position);
	}

	@Override
	public NodeType getNodeType()
    {
        return NodeType.CASE_NODE;
    }
	
	public void setElseNode(ElseNode elseNode)
	{
		if (getElseNode() != null) remove(size() - 1);
		
		add(elseNode);
	}
	
	public ElseNode getElseNode()
	{
		if (size() == 0) return null;
		
		Node node = getChild(size() - 1);
		if (node instanceof ElseNode)
			return (ElseNode) node;
		else
			return null;
	}
	
	@Override
	public Node getArgument()
	{
		return argument;
	}

	@Override
	public String toString()
	{
        StringBuilder builder = new StringBuilder(128);
        builder.append("case ").append(argument);

        for (int i = 0; i < size()-1; i++)
        {
            builder.append("\n").append(getChild(i));
        }
        
        ElseNode elseNode = getElseNode();
        if (elseNode != null)
        {
        	builder.append("\nelse " + elseNode);
        }
        builder.append("\nend\n");

        return builder.toString();
	}

	@Override
	public void addArgument(Node arg)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeArgument(int index)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setArgument(Node arg)
	{
		this.argument = arg;
		((AbstractNode) arg).parent = this;
	}
}
