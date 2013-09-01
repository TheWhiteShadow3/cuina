package cuina.editor.script.ruby.ast;

import cuina.editor.script.internal.ruby.SourceData;

import java.util.List;

public class VarNode extends AbstractNode implements INamed, IHasNext
{
	private String name;
	private int scope;
	private Node next;
	
	public VarNode(SourceData position, int scope)
	{
		super(position);
		this.name = position.getToken().getValue();
		this.scope = scope;
	}
	
	public VarNode(String name, int scope)
	{
		super(null);
		this.name = name;
		this.scope = scope;
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
        return NodeType.ARGUMENT_NODE;
    }
	
	@Override
	public List<Node> getChilds()
	{
		return AbstractNode.EMPTY_LIST;
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

	public int getScope()
	{
		return scope;
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
