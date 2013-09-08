package cuina.editor.script.ruby.ast;

import cuina.editor.script.internal.ruby.SourceData;

import java.util.List;

public class StrNode extends AbstractNode
{
	private String value;
	
	public StrNode(SourceData position)
	{
		super(position);
		pareString(position.getToken().getValue());
	}
	
	public StrNode(String str)
	{
		super(null);
		setValue(str);
	}
	
	private void pareString(String value)
	{
		if (value.charAt(0) == '"')
		{
			//TODO: diverse Ersetzungen und so.
		}
		else if (value.charAt(0) == '\'')
		{
			//XXX: Nichts weiteres oder?
		}
		this.value = value.substring(1, value.length() - 1);
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
		return NodeType.STRING_NODE;
	}
	
	@Override
	public List<Node> getChildren()
	{
		return AbstractNode.EMPTY_LIST;
	}
	
	@Override
	public String toString()
	{
		return '"' + value + '"';
	}
}
