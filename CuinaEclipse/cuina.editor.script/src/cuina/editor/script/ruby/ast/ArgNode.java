package cuina.editor.script.ruby.ast;

import java.util.List;

import cuina.editor.script.internal.ruby.SourceData;

public class ArgNode extends AbstractNode implements INamed
{
	private String name;
	private Node defaultNode;
	
	public ArgNode(SourceData position)
	{
		super(position);
		this.name = position.getToken().getValue();
	}
	
	public ArgNode(String name)
	{
		super(null);
		this.name = name;
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
	
	public Node getDefault()
	{
		return defaultNode;
	}

	public void setDefault(Node defaultNode)
	{
		this.defaultNode = defaultNode;
	}

	@Override
	public String toString()
	{
		if (defaultNode == null)
			return name;
		else
			return name + " = " + defaultNode;
	}
}
