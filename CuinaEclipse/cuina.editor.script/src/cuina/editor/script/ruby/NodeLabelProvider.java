package cuina.editor.script.ruby;

import cuina.editor.script.internal.ScriptUtil;
import cuina.editor.script.ruby.ast.*;

import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;

public class NodeLabelProvider extends LabelProvider
{
	public String getText(Node node)
	{
		if (node == null) return "null";
		if (node instanceof StrNode) 		return getString((StrNode) node);
		if (node instanceof FixNumNode) 	return getNumber((FixNumNode) node);
		if (node instanceof CallNode) 		return getFunction((CallNode) node);
		if (node instanceof IfNode) 		return getIf((IfNode) node);
		if (node instanceof ElseNode) 		return getElse((ElseNode) node);
		if (node instanceof ExpNode) 		return getExpression((ExpNode) node);
		if (node instanceof WhileNode) 		return getWhile((WhileNode) node);
		if (node instanceof CaseNode) 		return getCase((CaseNode) node);
		if (node instanceof WhenNode) 		return getWhen((WhenNode) node);
		if (node instanceof CommentNode) 	return getCommand((CommentNode) node);
		if (node instanceof ConstNode) 		return getConst((ConstNode) node);
		if (node instanceof AsgNode) 		return getAssignment((AsgNode) node);
		if (node instanceof DefNode) 		return getDefinition((DefNode) node);
		if (node instanceof ArgNode) 		return getArgumentNode((ArgNode) node);
		if (node instanceof INamed) 		return getNamedNode((INamed) node);
		
		return node.toString();
	}

	protected String getArgumentNode(ArgNode node)
	{
		if (node.getDefault() == null)
			return node.getName();
		else
			return node.getName() + " = " + getText(node.getDefault());
	}

	protected String getNamedNode(INamed namedNode)
	{
		return ScriptUtil.getFullName(namedNode);
	}

	protected String getAssignment(AsgNode asgNode)
	{
		StringBuilder builder = new StringBuilder(ScriptUtil.getFullName(asgNode.getAcceptor()));
		builder.append(" = ");
		Node arg = asgNode.getArgument();
		if (arg instanceof ArrayNode)
		{
			List<Node> args = arg.getChildren();
			if (args.size() > 0)
			{
				if (args.size() > 1) builder.append('[');
				for (int i = 0; i < args.size(); i++)
				{
					if (i > 0) builder.append(", ");
					Node node = args.get(i);
					builder.append( getText(node) );
					if (node instanceof ListNode)
						appendBlockChilds(builder, (ListNode) node);
				}
				if (args.size() > 1) builder.append(']');
			}
		}
		else if (arg != null)
		{
			builder.append(getText(arg));
		}
			
		return builder.toString();
	}
	
	private void appendBlockChilds(StringBuilder builder, ListNode list)
	{
		for (Node n : list)
		{
			if (n instanceof BlockNode)
			{
				for (Node child : (BlockNode) n)
				{
					builder.append(child).append(' ');
				}
				builder.append("end");
			}
			else if (n instanceof ListNode)
				appendBlockChilds(builder, (ListNode) n);
		}
	}

	protected String getConst(ConstNode constNode)
	{
		return ScriptUtil.getFullName(constNode);
	}

	protected String getWhen(WhenNode node)
	{
		return "when " + getText(node.getArgument()) + ':';
	}

	protected String getCommand(CommentNode node)
	{
		return "Kommentar: " + node.getValue();
	}

	protected String getElse(ElseNode node)
	{
		return "else";
	}

	protected String getCase(CaseNode caseNode)
	{
		return "case (" + getText(caseNode.getArgument()) + ')';
	}

	protected String getWhile(WhileNode whileNode)
	{
		return "while " + getText(whileNode.getArgument());
	}

	protected String getExpression(ExpNode expNode)
	{
		StringBuilder builder = new StringBuilder();
		if (expNode.getStartLiteral() != ' ')
			builder.append(expNode.getStartLiteral());
		for (int i = 0; i < expNode.size(); i++)
		{
			if (i > 0) builder.append(" " + expNode.getOperator(i-1) + " ");
			builder.append( getText(expNode.getChild(i)) );
		}
		if (expNode.getEndLiteral() != ' ')
		builder.append(expNode.getEndLiteral());
		return builder.toString();
	}

	protected String getIf(IfNode ifNode)
	{
		StringBuilder builder = new StringBuilder();
		if (ifNode.getParent() instanceof IfNode)
			builder.append("elsif ");
		else
			builder.append("if ");
		builder.append('(');
		builder.append(getText(ifNode.getArgument()));
		builder.append(") then ");
		return builder.toString();
	}
	
	protected String getDefinition(DefNode defNode)
	{
		StringBuilder builder = new StringBuilder(ScriptUtil.getFullName(defNode));
		addArguments(builder, defNode.getArgument());
		return builder.toString();
	}

	protected String getFunction(CallNode callNode)
	{
		StringBuilder builder = new StringBuilder(ScriptUtil.getFullName(callNode));
		addArguments(builder, callNode.getArgument());
		if (callNode.getBody() != null) builder.append(" do");
		return builder.toString();
	}
	
	private void addArguments(StringBuilder builder, Node argNode)
	{
		if (argNode instanceof ArrayNode)
		{
			List<Node> args = argNode.getChildren();
//			if (args.size() == 0) return;
			
			builder.append('(');
			for (int i = 0; i < args.size(); i++)
			{
				if (i > 0) builder.append(", ");
				builder.append( getText(args.get(i)) );
			}
			builder.append(')');
		}
		else
		{
			builder.append( getText(argNode) );
		}
	}

	protected String getString(StrNode strNode)
	{
		if (strNode.getValue() == null) return "nil";
		
		return '"' + strNode.getValue() + '"';
	}
	
	protected String getNumber(FixNumNode fixNumNode)
	{
		return fixNumNode.getValue().toString();
	}

	@Override
	public String getText(Object element)
	{
		if (element instanceof Node)
			return getText((Node) element);
		else
			return element.toString();
	}
}
