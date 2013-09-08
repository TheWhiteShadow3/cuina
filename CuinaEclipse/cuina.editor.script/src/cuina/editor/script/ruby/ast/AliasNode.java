package cuina.editor.script.ruby.ast;

import java.util.List;

import cuina.editor.script.internal.ruby.SourceData;

public class AliasNode extends AbstractNode implements INamed
{
	private String name;
	
	public AliasNode(SourceData position)
	{
		super(position);
		this.name = position.getToken().getValue();
	}

	@Override
	public NodeType getNodeType()
    {
        return NodeType.ALIAS_NODE;
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
	public List<Node> getChildren()
	{
		return AbstractNode.EMPTY_LIST;
	}

	@Override
	public String toString()
	{
		return ":" + name;
	}
}
