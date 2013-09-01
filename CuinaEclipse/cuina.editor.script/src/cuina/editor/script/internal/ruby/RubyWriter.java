package cuina.editor.script.internal.ruby;

import cuina.editor.script.ruby.ast.*;

import java.io.FileInputStream;
import java.io.IOException;

public class RubyWriter
{
	private StringBuilder builder;
	private String indent = "\t";
	private int indentLevel;
	
	public RubyWriter()
	{
		
	}
	
	public String write(Node node)
	{
		builder = new StringBuilder(1024);
		indentLevel = 0;
		write0(node);
		return builder.toString();
	}
	
	private void writeIndent()
	{
		for (int i = 0; i < indentLevel; i++) builder.append(indent);
	}
	
	private void write0(Node node)
	{
		if (node instanceof StrNode) 			writeString((StrNode) node);
		else if (node instanceof CommentNode) 	writeComment((CommentNode) node);
		else if (node instanceof FixNumNode) 	writeNumber((FixNumNode) node);
		else if (node instanceof ConstNode) 	writeConstant((ConstNode) node);
		else if (node instanceof ExpNode) 		writeExpression((ExpNode) node);
		else if (node instanceof CallNode) 		writeCall((CallNode) node);
		else if (node instanceof IfNode) 		writeIf((IfNode) node);
		else if (node instanceof CaseNode) 		writeCase((CaseNode) node);
		else if (node instanceof WhenNode) 		writeWhen((WhenNode) node);
		else if (node instanceof ElseNode) 		writeElse(node);
		else if (node instanceof WhileNode) 	writeWhile((WhileNode) node);
		else if (node instanceof AsgNode) 		writeAssignment((AsgNode) node);
		else if (node instanceof DefNode) 		writeMethod((DefNode) node);
		else if (node instanceof ClassNode) 	writeClass((ClassNode) node);
		else if (node instanceof AliasNode) 	writeAlias((AliasNode) node);
		else if (node instanceof RootNode)
		{
			for (Node child : node.getChilds())
			{
				write0(child);
				builder.append('\n');
			}
		}
		else if (node instanceof ArrayNode)
		{
			for (int i = 0; i < node.getChilds().size(); i++)
			{
				if (i > 0) builder.append(", ");
				write0(((ArrayNode) node).getChild(i));
			}
		}
		else if (node instanceof INamed) writeFullName((INamed) node);
		else
			System.err.println("Nicht schreibbarer Knoten: " + node);
	}
	
	private void writeFullName(final INamed node)
	{
		if (node instanceof IHasNext)
		{
			Node reciver = ((IHasNext) node).getNextNode();
			if (reciver != null)
			{
				if (reciver instanceof INamed) 
					writeFullName((INamed) reciver);
				
				if (reciver instanceof ConstNode && node instanceof ConstNode)
					builder.append("::");
				else
					builder.append('.');
			}
		}
		builder.append(node.getName());
	}

	private void writeComment(CommentNode cNode)
	{
		builder.append(cNode.getValue());
	}

	private void writeIf(IfNode ifNode)
	{
		builder.append("if ");
		write0(ifNode.getArgument());
		builder.append(" then\n");
		writeBlock(ifNode, true);
		if (ifNode.getElseNode() != null)
		{
			writeIndent();
			writeElse(ifNode.getElseNode());
		}
		writeIndent();
		builder.append("end");
	}
	
	private void writeElse(Node node)
	{
		if (node instanceof IfNode)
		{
			writeElseIf((IfNode) node);
		}
		else if (node instanceof ElseNode)
		{
			builder.append("else\n");
			writeBlock((ElseNode) node, true);
		}
	}
	
	private void writeElseIf(IfNode ifNode)
	{
		builder.append("elsif ");
		write0(ifNode.getArgument());
		builder.append(" then\n");
		writeBlock(ifNode, true);
		if (ifNode.getElseNode() != null)
		{
			writeIndent();
			writeElse(ifNode.getElseNode());
		}
	}
	
	private void writeCase(CaseNode caseNode)
	{
		builder.append("case ");
		write0(caseNode.getArgument());
		builder.append('\n');
		writeBlock(caseNode, false);
		writeIndent();
		builder.append("end");
	}
	
	private void writeWhen(WhenNode whenNode)
	{
		builder.append("when ");
		write0(whenNode.getArgument());
		builder.append(": ");
		if (whenNode.getChilds().size() == 1)
		{
			write0(whenNode.getChild(0));
		}
		else
		{
			builder.append('\n');
			writeBlock(whenNode, true);
		}
	}

	private void writeWhile(WhileNode whileNode)
	{
		builder.append("while ");
		write0(whileNode.getArgument());
		builder.append('\n');
		writeBlock(whileNode, true);
		writeIndent();
		builder.append("end");
	}
	
	private void writeBlock(BlockNode blockNode, boolean finalReturn)
	{
		indentLevel++;
		for (int i = 0; i < blockNode.size(); i++)
		{
			writeIndent();
			write0(blockNode.getChild(i));
			if (i < blockNode.size()-1 || finalReturn) builder.append('\n');
		}
		indentLevel--;
	}
	
	private void writeCall(CallNode callNode)
	{
		writeFullName(callNode);

		if (callNode.getArgument().size() > 0)
		{
			// Ausnahmen ohne Klammern.
			if ( "include".equals(callNode.getName()) || "return".equals(callNode.getName()) )
			{
				builder.append(' ');
				write0(callNode.getArgument());
			}
			else
			{
				builder.append('(');
				write0(callNode.getArgument());
				builder.append(')');
			}
		}
		else
		{
			//XXX: Klammern müssen momentan geschrieben werden, da der Parser sonst die Funktion nicht erkennt.
			builder.append("()");
		}
		
		if (callNode.getBody() != null)
		{
			write0(callNode.getBody());
		}
//		else
//			builder.append('\n');
	}

	private void writeClass(ClassNode classNode)
	{
		builder.append("class ").append(classNode.getName()).append('\n');
		writeBlock(classNode, true);
		builder.append("end");
	}
	
	private void writeMethod(DefNode defNode)
	{
		String name = defNode.getName();
		if (name.charAt(0) == '{' || "do".equals(name))
		{
			builder.append(' ');
			builder.append(defNode.getName());
			builder.append(" |");
			write0(defNode.getArgument());
			builder.append("|\n");
		}
		else
		{
			builder.append("def ");
			writeFullName(defNode);
			if (defNode.getArgument().size() > 0)
			{
				builder.append('(');
				write0(defNode.getArgument());
				builder.append(')');
			}
			builder.append('\n');
		}

		writeBlock(defNode, true);
		writeIndent();
		if (name.charAt(0) == '{')
			builder.append("}");
		else
			builder.append("end");
	}

	private void writeString(StrNode strNode)
	{
		builder.append('"').append(strNode.getValue()).append('"');
	}
	
	private void writeAlias(AliasNode aliasNode)
	{
		builder.append(':').append(aliasNode.getName());
	}
	
	private void writeNumber(FixNumNode fixNumNode)
	{
		builder.append(fixNumNode.getValue().toString());
	}
	
	private void writeConstant(ConstNode constNode)
	{
		writeFullName(constNode);
	}

	private void writeAssignment(AsgNode asgNode)
	{
		writeFullName(asgNode.getAcceptor());
		builder.append(" =");
		write0(asgNode.getArgument());
	}
	
	private void writeExpression(ExpNode expNode)
	{
		char c = expNode.getStartLiteral();
		if (c != ' ') builder.append(c);
		for (int i = 0; i < expNode.getChilds().size(); i++)
		{
			if (i > 0) builder.append(' ').append(expNode.getOperator(i-1)).append(' ');
			write0(expNode.getChild(i));
		}
		c = expNode.getEndLiteral();
		if (c != ' ') builder.append(c);
	}
	
	private static final String TEST_FILE =
			"G:\\Projekte\\Java\\CuinaProjekt\\CuinaEclipse\\TestWorkspace\\Test\\ruby.rb";
	
	public static void main(String[] args)
	{
		RubyParser parser = new RubyParser(RubyParser.MODE_DEFAULT);
		RubyWriter writer = new RubyWriter();
		try
		{
			parser.parse(new RubySource(new FileInputStream(TEST_FILE), TEST_FILE));
			String code = writer.write(parser.getRoot());
			System.out.println(code);
			
			parser.parse(new RubySource(code, TEST_FILE));
			String code2 = writer.write(parser.getRoot());
			System.out.println(code2);
			if (code.equals(code2))
			{
				System.out.println("--- OK ---");
			}
			else
			{
				System.err.println("Input != Output!");
			}
		}
		catch (ParseException | IOException e)
		{
			e.printStackTrace();
		}
	}
}
