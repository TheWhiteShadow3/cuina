package cuina.editor.script.ruby.ast;

import cuina.editor.script.internal.ruby.SourceData;

import java.util.List;

public class WhileNode extends BlockNode implements IParameter
{
	private Node argument;
	
	public WhileNode(SourceData position)
	{
		super(position);
	}

	@Override
	public NodeType getNodeType()
    {
        return NodeType.IF_NODE;
    }
	
	public List<Node> getBlock()
	{
		return super.getChilds();
	}
	
	@Override
	public void setArgument(Node arg)
	{
		argument = arg;
		((AbstractNode) arg).parent = this;
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
        builder.append("while ").append(argument);

        for (Node child : super.getChilds())
        {
            builder.append("\n\t").append(child);
        }
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
