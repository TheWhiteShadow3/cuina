package cuina.editor.script.ruby.ast;

import java.util.List;

import cuina.editor.script.internal.ruby.SourceData;

public class ConstNode extends AbstractNode implements INamed, IHasNext
{
	private String name;
	private Node next;
	
	public ConstNode(SourceData position)
	{
		super(position);
		this.name = position.getToken().getValue();
	}
	
	public ConstNode(String name)
	{
		super(null);
		this.name = name;
	}
	
	@Override
	public void setNextNode(Node next)
	{
		this.next = next;
		if (next != null) ((AbstractNode) next).parent = this;
	}

	@Override
	public Node getNextNode()
	{
		return next;
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.CONSTANT_NODE;
	}

	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public void setName(String name)
	{
		this.name = name;
	}
	
	@Override
	public List<Node> getChilds()
	{
		return AbstractNode.createList(next);
	}
	
	@Override
	public String toString()
	{
        if (next != null)
        	return next + "." + name;
        else
        	return name;
	}
}
