package cuina.editor.script.ruby.ast;

import cuina.editor.script.internal.ruby.SourceData;

import java.util.HashMap;

public class DefNode extends BlockNode implements INamed, IHasNext, IParameter, IScope
{
	private final HashMap<String, Node> localVars = new HashMap<String, Node>();
	
	private String name;
	private Node next;
//	private BlockNode body;
	private ArrayNode arguments;
	
	public DefNode(SourceData position)
	{
		super(position);
		this.name = position.getToken().getValue();
		this.arguments = new ArrayNode(position);
		arguments.parent = this;
	}
	
	public DefNode(String name)
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
	public NodeType getNodeType()
    {
        return NodeType.DEF_NODE;
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
	public ArrayNode getArgument()
	{
		return arguments;
	}
	
	@Override
	public void setArgument(Node arg)
	{
		this.arguments = (ArrayNode) arg;
		arguments.parent = this;
	}
	
	@Override
	public void addArgument(Node e)
	{
		arguments.add(e);
		if (e instanceof INamed)
			getLocalVars().put(((INamed) e).getName(), e);
	}
	
	@Override
	public void removeArgument(int index)
	{
		ArgNode argNode = (ArgNode) arguments.remove(index);
		getLocalVars().remove(argNode.getName());
	}
	
	@Override
	public String toString()
	{
		if (next != null)
			return "def " + next + "." + name + "(" + arguments + ")" + "\n" + super.toString() + "\nend\n";
		else
			return "def " + name + "(" + arguments + ")\n" + super.toString() + "\nend\n";
	}
	
	@Override
	public HashMap<String, Node> getLocalVars()
	{
		return localVars;
	}
}
