package cuina.editor.script.ruby.ast;

import cuina.editor.script.internal.ruby.SourceData;

import java.util.List;

public class FixNumNode extends AbstractNode
{
	private Number value;
	
	public FixNumNode(SourceData position, String sourceValue)
	{
		super(position);
//		String sourceValue = position.getToken().getValue();
		if (sourceValue.contains("."))
			this.value = new Double(Double.parseDouble(sourceValue));
		else
			this.value = new Long(Long.parseLong(sourceValue));
	}
	
	public FixNumNode(Long value)
	{
		super(null);
		this.value = value;
	}
	
	public FixNumNode(Double value)
	{
		super(null);
		this.value = value;
	}

	public Number getValue()
	{
		return value;
	}

	public void setValue(Number value)
	{
		this.value = value;
	}

	@Override
	public NodeType getNodeType()
    {
        return NodeType.FIXNUM_NODE;
    }

	@Override
	public List<Node> getChilds()
	{
		return AbstractNode.EMPTY_LIST;
	}
	
	@Override
	public String toString()
	{
		return value.toString();
	}
}
