package cuina.editor.script.ruby.ast;

import cuina.editor.script.internal.ruby.SourceData;

import java.util.List;

public class CallNode extends AbstractNode implements INamed, IContainerNode, IHasNext, IParameter
{
	private String name;
	private Node next;
	private BlockNode body;
	private ArrayNode arguments;
	
	public CallNode(SourceData position)
	{
		super(position);
		this.name = position.getToken().getValue();
		this.arguments = new ArrayNode(position);
		arguments.parent = this;
	}
	
	public CallNode(String name)
	{
		super(null);
		this.name = name;
		this.arguments = new ArrayNode(null);
		arguments.parent = this;
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
	public void addArgument(Node e)
	{
		arguments.add(e);
	}
	
	@Override
	public void removeArgument(int index)
	{
		arguments.remove(index);
	}
	
	@Override
	public void setArgument(Node arg)
	{
		this.arguments = (ArrayNode) arg;
		arguments.parent = this;
	}

	@Override
	public ArrayNode getArgument()
	{
		return arguments;
	}

	@Override
	public NodeType getNodeType()
    {
        return NodeType.CALL_NODE;
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
	public void setBody(BlockNode body)
	{
		this.body = body;
		body.parent = this;
	}

	@Override
	public BlockNode getBody()
	{
		return body;
	}

	@Override
	public List<Node> getChilds()
	{
		return AbstractNode.createList(next, arguments, body);
	}
	
	@Override
	public String toString()
	{
        StringBuilder builder = new StringBuilder(64);

        if (next != null)
        	builder.append(next + ".");
        
        builder.append(name + "(" + arguments + ")");

		if (body != null)
			builder.append("\n" + body);
		
		return builder.toString();
	}
}
