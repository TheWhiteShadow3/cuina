package cuina.editor.script.internal.ruby;

import cuina.editor.script.ruby.ast.*;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;

public class RubyParser
{
	public static final int MODE_DEFAULT = 0;
	public static final int MODE_STRICT = 1;
	
	private int pos;
	private Token nextToken;
	private RubyParser parent;
	private RootNode root;
	private final Stack<ListNode> nodeStack = new Stack<ListNode>();
	private ListNode currentList;
	/** Aktueller Knoten */
	private Node current;
	private RubySource source;
	private TreeEditor treeEditor;
	private int mode = MODE_DEFAULT;
	private int braces;
	Token[] tokens;
	
	public RubyParser(int mode)
	{
		this.mode = mode;
	}
	
	public RootNode getRoot()
	{
		return root;
	}
	
	public int getMode()
	{
		return mode;
	}

	public void setMode(int mode)
	{
		this.mode = mode;
	}

	/**
	 * Gibt den TreeEditor zurück.
	 * @return
	 */
	public TreeEditor getTreeEditor()
	{
		if (treeEditor == null) treeEditor = new TreeEditor(root);
		return treeEditor;
	}

	public RootNode parse(RubySource source) throws ParseException
	{
		if (source == null || source.getTokens().length == 0) return null;
		this.source = source;
		this.tokens = source.getTokens();
		
		// leere Identifier-Liste aus vorherigem Run, wenn Parser nicht als
		// Child agiert.
		if (parent == null)
		{
			root = new RootNode(newSD(null));
			currentList = root;
		}

		braces = 0;
		pos = 0;
		current = null;
		try
		{
			startBlock(null);
		}
		catch (ParseException e)
		{
			throw e;
		}
		catch (Exception e) // Sicherheits-Fänger
		{
			throw new ParseException(e);
		}

//		System.out.println("\nParsing-Result:\n" + root);
		
//		if (parent == null)
//		{
//			parent = null;
//////			RubyEditor.addCode( new RubyWriter().write(root) );
//			RubyEditor.addCode( new CommandLineInterface(root).treeToString() );
		
//		RubyEditor.codeField.setScript(root);
		if (treeEditor != null)
			treeEditor.setRoot(root);
		
//		}
		return root;
	}
	
	private void startBlock(ListNode block) throws ParseException
	{
		if (block != null) pushNode(block);
		// Hauptschleife
		Token token;
		while (pos < tokens.length - 1)
		{
//			if (nextToken != null) System.out.println("Last Token: " + nextToken);
			token =  tokens[pos++];
			try
			{
				if (!findNextNode(token))
				{
					popNode();
					return;
				}
				if (current == null) continue;
				if (current instanceof ListNode)
				{
					addNode(current);
					if (current instanceof BlockNode)
						startBlock((ListNode)current);
					else
						System.err.println("Änderung für Node: " + current);
				}
				else
				{
					addNode(current);
				}
			}
			catch (ParseException e)
			{
				if ((mode & MODE_STRICT) != 0) throw e;
				e.printStackTrace();
			}
		}
		if (braces < 0) throw new ParseException("unexpected token ')'");
		if (braces > 0) throw new ParseException("missing token ')'");
	}
	
	private boolean findNextNode(Token token) throws ParseException
	{
		while(token != null && token.getGroup() == Tokenizer.RETURN)
		{
//			System.out.println("neue Zeile");
			token = nextToken();
		}
		if (token == null) return false;
		
		if ( "end".equals(token.getValue()) || token.charAt(0) == '}' )
		{
			if (currentList instanceof CaseNode) popNode();
//			System.out.println("beende Block " + currentList);
			return false;
		}
		else if ( "when".equals(token.getValue()) || "else".equals(token.getValue()) )
		{
			if (currentList instanceof WhenNode)
			{
				pos--;
				return false;
			}
		}
		
		if (token.charAt(0) == '(') { braces++; return findNextNode(nextToken()); }
		if (token.charAt(0) == ')') { braces--; return findNextNode(nextToken()); }
		
		char c = token.charAt(0);
		if (c == '#')
		{
			 current = new CommentNode(newSD(token));
			 return true;
		}
		else if (token.getGroup() == Tokenizer.IDENTIFIER)
		{
			if (c == '"' || c == '\'')
			{
				current =  new StrNode(newSD(token));
				return true;
			}
			else
			{
				return getIdentifierNode(token);
			}
		}
		else if (token.getGroup() == Tokenizer.NUMBER)
		{
			if ( isNumeric(token.getValue()) )
			{
				current = new FixNumNode(newSD(token), token.getValue());
				nextToken = getNextToken();
				if (isExpressionOperation(nextToken))
				{
					current = getExpression(token, current);
				}
				return true;
			}
		}
//		else if (token.charAt(0) == '-' && getNextToken().getGroup() == Tokenizer.NUMBER)
//		{
//			nextToken = getNextToken();
//			current = new FixNumNode(newSD(token), token.getValue() + nextToken.getValue());
//			if (isExpression(nextToken))
//			{
//				current = getExpression(token, current);
//			}
//			return true;
//		}

		switch(token.getGroup())
		{
			case Tokenizer.OPERAND:
				System.err.println("Operation außerhalb einer Expression!"); 
				break;
			case Tokenizer.BRACE:
				current = checkBrace(token);
				return current != null;
			case Tokenizer.SPECIAL: 
				checkSpecial(token);
				return current != null;
		}
		System.err.println("Kein Node mehr gefunden!");
		return false;
	}
	
	// Sucht nach einfachen Elementen wie Strings, Zahlen, Variablen und Funktionen
	private boolean findNextSimpleNode(Token token) throws ParseException
	{
		if (token == null) return false;
		
		if (token.charAt(0) == '(') { braces++; return findNextNode(nextToken()); }
		if (token.charAt(0) == ')') { braces--; return findNextNode(nextToken()); }
		
		char c = token.charAt(0);
		if (token.getGroup() == Tokenizer.IDENTIFIER)
		{
			if (c == '"' || c == '\'')
			{
				current = new StrNode(newSD(token));
				return true;
			}
			else
			{
				return getIdentifierNode(token);
			}
		}
		else if (token.getGroup() == Tokenizer.NUMBER)
		{
			if ( isNumeric(token.getValue()) )
			{
				current = new FixNumNode(newSD(token), token.getValue());
				nextToken = getNextToken();
				return true;
			}
		}
		
		switch(token.getGroup())
		{
			case Tokenizer.OPERAND:
				System.err.println("Operation außerhalb einer Expression!"); 
				break;
			case Tokenizer.BRACE:
				current = checkBrace(token);
				return false;
				
			case Tokenizer.SPECIAL: 
				checkSpecial(token);
				break;
		}
		return false;
	}
	
	private boolean isNumeric(String numStr)
	{
		char c = numStr.charAt(0);
		if ((c < 48 || c > 57) && c != '-') return false;
		
		try { Double.parseDouble(numStr.replaceAll("_", "")); // if
			return true; }
		catch(NumberFormatException e) { // else
			return false; }
	}
	
	private void popNode() throws ParseException
	{
		try
		{
			currentList = nodeStack.pop();
		}
		catch(EmptyStackException e)
		{
			throw new ParseException("unexpected end of block.", e);
		}
	}
	
	private void pushNode(ListNode node)
	{
		nodeStack.push(currentList);
		currentList = node;
	}
	
	private void addNode(Node node)
	{
//		if (node == null) return;
		
		currentList.add(node);
	}
	
	private SourceData newSD(Token token)
	{
		return new SourceData(source, token);
	}
	
	private void checkSpecial(Token token) throws ParseException
	{
		if ("::".equals( token.getValue()) )
		{
			if (!(current instanceof ConstNode))
			{
				String str = current.getPosition().getToken().getValue();
				throw new ParseException(str + " must be a Class or Module!");
			}
			
			nextToken = getNextToken();
			if (nextToken.getGroup() != Tokenizer.IDENTIFIER)
				throw new ParseException("Identifier expected!");
			ConstNode constNode = (ConstNode) getSimpleNode(nextToken);
			constNode.setNextNode(current);
			return;
		}
		else if (token.charAt(0) == ':')
		{
			current = new AliasNode(newSD(nextToken()));
			return;
		}
		System.err.println("Spezial-Token gefunden! " + token);
	}

	private Node checkBrace(Token token) throws ParseException
	{
		System.err.println("Klammer-Token gefunden! " + token);
		if (token.charAt(0) == '(') braces++;
		else if (token.charAt(0) == ')') braces--;
		
		if ( findNextNode(nextToken()) )
			return current;
		else
			return null;
	}
	
	private Node getSimpleNode(Token token)
	{
		int scope = getVarNodeScope(token.getValue());
		
		if (scope == Node.LOCAL_SCOPE)
		{	// Ohne Präfix kann es sich um mehrere Typen handeln.
			if (token.charAt(0) < 'a')
				return new ConstNode(newSD(token));
			else
			{
				Node node = findDefinitionNode(token);
				if (node == null)
				{
					if (nextToken.charAt(0) == '(')
						return new CallNode(newSD(token));
					else
						return new VarNode(newSD(token), Node.UNKNOWN_SCOPE);
				}
				else
					return new VarNode(newSD(token), scope);
			}
		}
		else
		{
			return new VarNode(newSD(token), scope);
		}
	}
	
	private int getVarNodeScope(String name)
	{
		switch(name.charAt(0))
		{
			case '$': return Node.GLOBAL_SCOPE;
			case '@': return name.charAt(1) == '@' ?  Node.CLASS_SCOPE : Node.INST_SCOPE;
			default:  return Node.LOCAL_SCOPE;
		}
	}
	
	private Node findDefinitionNode(Token token)
	{
		int scope = getVarNodeScope(token.getValue());
		
		HashMap<String, Node> list;
		switch(scope)
		{
			case Node.LOCAL_SCOPE:
				list = getAncestor(IScope.class).getLocalVars();
				break;
			case Node.CLASS_SCOPE:
				list = getAncestor(ModuleNode.class).getStaticVars();
				break;
			case Node.INST_SCOPE:
				list = getAncestor(ClassNode.class).getInstanceVars();
				break;
			default:
				list = root.getGlobalVars();
				break;
		}
		
		return list.get(token.getValue());
	}
	
	private void addVariable(AsgNode asgNode)
	{
		String name = asgNode.getAcceptor().getName();
		int scope = getVarNodeScope(name);
		
		HashMap<String, Node> list;
		switch(scope)
		{
			case Node.LOCAL_SCOPE:
				list = getAncestor(IScope.class).getLocalVars();
				break;
			case Node.CLASS_SCOPE:
				list = getAncestor(ModuleNode.class).getStaticVars();
				break;
			case Node.INST_SCOPE:
				list = getAncestor(ClassNode.class).getInstanceVars();
				break;
			default:
				list = root.getGlobalVars();
				break;
		}

		list.put(name, asgNode);
	}
	
	private boolean getIdentifierNode(Token token) throws ParseException
	{
		if (checkKeyword(token)) return true;
		
		Node node;
		Node prevNode = null;
		nextToken = getNextToken();
		while (nextToken.charAt(0) == '.' || "::".equals( nextToken.getValue()) )
		{
			node = getSimpleNode(token);
//				if (current != null) ((IHasNext) current).setNextNode(node);
//				current = node;
			((IHasNext)node).setNextNode(prevNode);
			prevNode = node;
			token = nextToken(2);
			nextToken = getNextToken();
		}
//			current = new ConstNode(newSD(token));
//			findNextNode(nextToken(2));
//			return true;
//		}
		if (nextToken.getValue().endsWith("="))
		{
			char c = nextToken.charAt(0);
			if (nextToken.getValue().length() == 1 ||
				c == '+' || c == '-' || c == '*' || c == '/' || c == '|' || c == '&' || c == '%')
			{
				node = new VarNode(newSD(token), getVarNodeScope(token.getValue()) );
				AsgNode asgNode = new AsgNode(newSD(nextToken), (VarNode)node);
				addVariable(asgNode);
				if (prevNode != null) ((VarNode)node).setNextNode(prevNode);
				
				if (nextToken.getValue().length() == 2)
				{
					ExpNode expNode = new ExpNode(newSD(token));
					expNode.add(node);
					expNode.addOperator(nextToken.getValue().substring(0, 1));
					findNextSimpleNode(nextToken(2));
					expNode.add(current);
					nextToken = nextToken();
					
					// behandle Syntax wie: a | if b then c
					if (current instanceof BlockNode)
					{
						startBlock((BlockNode)current);
						asgNode.addArgument(expNode);
						current = asgNode;
						return true;
					}
					
					fillExpression(expNode);
					asgNode.addArgument(expNode);
				}
				else
				{
					nextToken();
				}
				
				node = arrayFillElements(asgNode);
			}
			else
			{
				node = getSimpleNode(token);
			}
		}
		else if ( "end".equals(nextToken.getValue()) )
		{
			nextToken(); // end verwerten.
			node = new CallNode(newSD(token));
			addNode(node);
			return false;
		}
		else
		{
			Node callNode = null;
			if (nextToken.getGroup() == Tokenizer.IDENTIFIER
			 || nextToken.getGroup() == Tokenizer.NUMBER
			 || nextToken.charAt(0) == '(')
			{
				if (callNode == null) callNode = new CallNode(newSD(token));
				node = getParameterNode(callNode);
			}
			else
			{
				if (callNode == null) callNode = getSimpleNode(token);
				node = callNode;
			}
			if (prevNode != null) ((IHasNext)callNode).setNextNode(prevNode);
		}
		
		if (isExpressionOperation(nextToken))
		{
			node = getExpression(token, node);
		}
		else if (nextToken != null && nextToken.charAt(0) == '{' || "do".equals(nextToken.getValue()) )
		{
			appendMethodCallBlock(nextToken);
		}
		
		current = node;
		return true;
	}
	
	private Node appendMethodCallBlock(Token token) throws ParseException
	{
		if (current instanceof CallNode)
		{
			CallNode callNode = (CallNode) current;
			DefNode defNode = new DefNode(newSD(token));
			callNode.setBody(defNode);
			
			nextToken = nextToken(2);
			if (nextToken.charAt(0) == '|')
			{
				arrayFillElements(defNode);
				nextToken = nextToken();
				if (nextToken.charAt(0) != '|') throw new ParseException("missing Token '|'");
			}
			
			startBlock(defNode);
			return callNode;
		}
		else System.out.println("'{'-Klammer ohne Funktion!");
		return null;
	}
	
	private Node getParameterNode(Node paramNode) throws ParseException
	{
		paramNode = arrayFillElements(paramNode);
		if (paramNode == null) return null;
		
		nextToken = getNextToken();
		if (nextToken != null && nextToken.charAt(0) == '.')
		{
			getIdentifierNode(nextToken(2));
			Node node = current;
			if (node instanceof ExpNode)
				((IHasNext) node.getChilds().get(0)).setNextNode(paramNode);
			else
			{
				while ( ((IHasNext) node).getNextNode() != null) node = ((IHasNext) node).getNextNode();
				((IHasNext) node).setNextNode(paramNode);
			}
			return current;
		}
		current = paramNode;
		return paramNode;
	}
	
	private boolean isExpressionOperation(Token token)
	{
		if (token == null) return false;
		
		return (token.getGroup() == Tokenizer.OPERAND
				|| "and".equals(token.getValue())
				|| "or".equals(token.getValue()) );
	}
	
	private Node getExpression(Token token, Node firstNode) throws ParseException
	{
		ExpNode expNode = new ExpNode(newSD(token));
		if (firstNode != null) expNode.add(firstNode);
		fillExpression(expNode);
		return expNode;
	}
	
	private void fillExpression(ExpNode expNode) throws ParseException
	{
		while (isExpressionOperation(nextToken))
		{
			expNode.addOperator(nextToken.getValue());
			if ( findNextNode(nextToken(2)) )
			{
				expNode.add(current);
				if (current instanceof BlockNode)
				{
					startBlock((BlockNode)current);
				}
			}
			else throw new ParseException("unexpected end of Expression.");
			nextToken = getNextToken();
		}
	}

	private Node arrayFillElements(Node paramNode) throws ParseException
	{
		ArrayNode array = new ArrayNode((paramNode).getPosition());
		((IParameter) paramNode).setArgument(array);
		nextToken = nextToken();
		if (nextToken == null || nextToken.getGroup() == Tokenizer.RETURN) return paramNode;
		
		int startBraces = braces;
		if (nextToken.getGroup() == Tokenizer.BRACE)
		{	// Verwerte die (-Klammer
			nextToken = nextToken();
			if (nextToken.charAt(0) == ')')
			{	// leere Klammer
				return paramNode;
			}
			braces++;
		}
		
		while (true)
		{
			if (array.getParent().getNodeType() == NodeType.DEF_NODE)
			{
				current = new ArgNode(newSD(nextToken));
			}
			else
			{
				if (!findNextNode(nextToken)) break;
				
				if (startBraces == braces && current instanceof IfNode)
				{
					((IfNode)current).add(paramNode);
					addNode(current);
					return null;
				}
			}
			nextToken = getNextToken();

			if (current != null) array.add(current);
			if (nextToken == null || nextToken.charAt(0) != ',') break;
			nextToken = nextToken(2);
		}
		
		while (startBraces < braces)
		{	// Verwerte die )-Klammer
			if (nextToken == null || nextToken.charAt(0) != ')') throw new ParseException("missing token ')'");
			nextToken = nextToken();
			braces--;
		}
		return paramNode;
	}
	
	private boolean checkKeyword(Token token) throws ParseException
	{
		if ( "true".equals(token.getValue()) ||
			 "false".equals(token.getValue()) ||
			 "nil".equals(token.getValue()) )
		{
			current = new ConstNode(newSD(token));
			return true;
		}
		else if ( "if".equals(token.getValue()) )
		{
			findNextNode(nextToken());
			IfNode ifNode = new IfNode(newSD(token));
			if (current == null) throw new ParseException("invalid syntax in if-statement");
			ifNode.setArgument(current);
			current = ifNode;
			return true;
		}
		else if ( "while".equals(token.getValue()) )
		{
			findNextNode(nextToken());
			WhileNode whileNode =  new WhileNode(newSD(token));
			whileNode.setArgument(current);
			current = whileNode;
			return true;
		}
		else if ( "then".equals(token.getValue()) )
		{
//			if ( !(current instanceof IfNode)) throw new ParseException("unexpected Keyword then.");
			current = null;
			return true;
		}
		else if ( "elsif".equals(token.getValue()) )
		{
			if (!(currentList instanceof IfNode))
				throw new ParseException("unexpected keyword elsif.");

			IfNode ifNode = new IfNode(newSD(token));
			((IfNode)currentList).setElseNode(ifNode);
			findNextNode(nextToken());
			ifNode.setArgument(current);
			currentList = ifNode;
			current = null;
			return true;
		}
		else if ( "else".equals(token.getValue()) )
		{
			if (currentList instanceof WhenNode) popNode();
			if (!(currentList instanceof IfNode) && !(currentList instanceof CaseNode))
				throw new ParseException("unexpected keyword else.");
			
			ElseNode elseNode = new ElseNode(newSD(token));
			if (currentList instanceof CaseNode)
				((CaseNode)currentList).setElseNode(elseNode);
			else
				((IfNode)currentList).setElseNode(elseNode);
			currentList = elseNode;
			current = null;
			return true;
		}
		else if ( "case".equals(token.getValue()) )
		{
			findNextNode(nextToken());
			CaseNode caseNode = new CaseNode(newSD(token));
			caseNode.setArgument(current);
			current = caseNode;
			return true;
		}
		else if ( "when".equals(token.getValue()) )
		{
			if (currentList instanceof WhenNode) popNode();
			if (!(currentList instanceof CaseNode))
				throw new ParseException("unexpected keyword when.");
			
			findNextNode(nextToken());
			WhenNode whenNode = new WhenNode(newSD(token));
			whenNode.setArgument(current);
			if (getNextToken().charAt(0) == ':') nextToken();
			current = whenNode;
			return true;
		}
		else if ( "module".equals(token.getValue()) )
		{
			token = nextToken();
			current =  new ModuleNode(newSD(token));
			return true;
		}
		else if ( "class".equals(token.getValue()) )
		{
			token = nextToken();
			current =  new ClassNode(newSD(token));
			return true;
		}
		else if ( "def".equals(token.getValue()) )
		{
			current = null;
			token = nextToken();
			nextToken = getNextToken();
			
			while (nextToken.charAt(0) == '.')
			{
				ConstNode constNode = new ConstNode(newSD(token));
				constNode.setNextNode(current);
				current = constNode;
				token = nextToken(2);
				nextToken = getNextToken();
			}
			
			DefNode defNode = new DefNode(newSD(token));
			defNode.setNextNode(current);
			arrayFillElements(defNode);
			for (Node node : defNode.getArgument().getChilds())
			{
				defNode.getLocalVars().put(((INamed)node).getName(), node);
			}
			current = defNode;
			return true;
		}
		else if ( "attr_reader".equals(token.getValue()) ||
				  "attr_writer".equals(token.getValue()) ||
				  "attr_accessor".equals(token.getValue()) )
		{
			CallNode callNode = new CallNode(newSD(token));
			getParameterNode(callNode);
			return true;
		}
		return false;
	}

	private Token nextToken()
	{
		return tokens.length > pos ? tokens[pos++] : null;
	}
	
	private Token nextToken(int count)
	{
		if (tokens.length >= pos + count)
		{
			pos += count - 1;
			return tokens[pos++];
		}
		else 
			return null;
	}
	
	private <E> E getAncestor(Class<E> type)
	{
		if (type.isInstance(currentList)) return (E) currentList;
		Node node = currentList;
		while(node != null)
		{
			node = node.getParent();
			if (node != null && type.isInstance(node)) return (E)node;
		}
		return null;
	}
	
//	private boolean isTypeOf(Object obj, Class<?> clazz)
//	{
//		Class<?> c = obj.getClass();
//		do
//		{
//			if (c == clazz) return true;
//			for (Class<?> i : c.getInterfaces())
//			{
//				if (i == clazz) return true;
//			}
//			c = c.getSuperclass();
//		}
//		while (c != Object.class);
//		
//		return false;
//	}
	
	private Token getNextToken()
	{
		return tokens.length > pos ? tokens[pos] : null;
	}

//	public IdentifierList getIdentifier()
//	{
//		throw new UnsupportedOperationException("Implementation von getIdentifier fehlt");
////		return null;
//	}
}
