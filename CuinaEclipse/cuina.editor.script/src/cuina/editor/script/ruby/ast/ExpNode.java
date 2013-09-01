package cuina.editor.script.ruby.ast;

import java.util.ArrayList;

import cuina.editor.script.internal.ruby.SourceData;

public class ExpNode extends ListNode
{
	public static final int IMPLICIT = 0;
	public static final int EXPLICIT = 1;
	public static final int ARRAY_PARAM = 2;
	public static final int HASH_PARAM = 3;
	
	private ArrayList<String> operators;
	private int expType;
	
	public ExpNode(SourceData position)
	{
		super(position);
		if (position.getToken() != null)
			this.expType = " ([{".indexOf(position.getToken().getValue());
		operators = new ArrayList<>(4);
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.EXPRESSION_NODE;
	}

	public int OperatorCount()
	{
		return operators.size();
	}

	public String getOperator(int index)
	{
		return operators.get(index);
	}

	public boolean addOperator(String op)
	{
		return operators.add(op);
	}
	
	public char getStartLiteral()
	{
		switch (expType)
		{
			case 1:	return '(';
			case 2:	return '[';
			case 3:	return '{';
		}
		return ' ';
	}

	public char getEndLiteral()
	{
		switch (expType)
		{
			case 1:	return ')';
			case 2:	return ']';
			case 3:	return '}';
		}
		return ' ';
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		if (size() > 0)
		{
			builder.append(getStartLiteral());
			for (int i = 0; i < size(); i++)
			{
				if (size() > i) 			builder.append(getChild(i));
				if (OperatorCount() > i) 	builder.append(" " + getOperator(i) + " ");
			}
			if (expType != 0) builder.append(getEndLiteral());
		}
		return builder.toString();
	}
}
