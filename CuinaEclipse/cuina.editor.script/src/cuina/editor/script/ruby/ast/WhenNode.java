package cuina.editor.script.ruby.ast;

import cuina.editor.script.internal.ruby.SourceData;

public class WhenNode extends BlockNode implements IParameter
{
	private Node argument;
	
	public WhenNode(SourceData position)
	{
		super(position);
	}

	@Override
	public NodeType getNodeType()
    {
        return NodeType.WHEN_NODE;
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
	
	@Override
	public String toString()
	{
        StringBuilder builder = new StringBuilder(128);
        builder.append("when ").append(argument).append(":");

        for (Node child : super.getChilds())
        {
            builder.append("\n\t").append(child);
        }

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
}
