package cuina.editor.script.ruby.ast;

import cuina.editor.script.internal.ruby.SourceData;

import java.util.List;

public class AsgNode extends AbstractNode implements IParameter
{
	private VarNode acceptor;
	private Node argument;
	
	public AsgNode(SourceData position, VarNode acceptor)
	{
		super(position);
		this.acceptor = acceptor;
	}

	@Override
	public List<Node> getChildren()
	{
		return AbstractNode.createList(acceptor, argument);
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.ASSIGMENT_NODE;
	}

	public VarNode getAcceptor()
	{
		return acceptor;
	}
	
	@Override
	public void addArgument(Node node)
	{
		if (argument instanceof ListNode)
			((ListNode) argument).add(node);
		else
			throw new UnsupportedOperationException("argument is not a ListNode");
	}
	
	@Override
	public void removeArgument(int index)
	{
		if (argument instanceof ListNode)
			((ListNode) argument).remove(index);
		else
			throw new UnsupportedOperationException("argument is not a ListNode");
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
		return acceptor + " = " + argument;
	}
}
