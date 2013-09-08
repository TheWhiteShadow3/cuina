package cuina.editor.script.ruby.ast;

import java.util.List;

import cuina.editor.script.internal.ruby.SourceData;

public class CommentNode extends AbstractNode
{
	private String value;
	
	public CommentNode(SourceData position)
	{
		super(position);
		this.value = position.getToken().getValue();
	}
	
	public CommentNode(String value)
	{
		super(null);
		this.value = value;
	}
	
	public void setValue(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.COMMENT_NODE;
	}

	@Override
	public List<Node> getChildren()
	{
		return AbstractNode.EMPTY_LIST;
	}

	@Override
	public String toString()
	{
		return "# " + value;
	}
}
