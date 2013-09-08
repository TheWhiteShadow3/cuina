package cuina.editor.script.internal.dialog;

import cuina.editor.script.ruby.ast.ArgNode;
import cuina.editor.script.ruby.ast.AsgNode;
import cuina.editor.script.ruby.ast.DefNode;
import cuina.editor.script.ruby.ast.ListNode;
import cuina.editor.script.ruby.ast.Node;
import cuina.editor.script.ruby.ast.RootNode;

import java.util.HashMap;

public class VariablesFinder
{
	private VariablesFinder() {}
	
	public static HashMap<String, Variable> findVariables(ListNode parent, int index)
	{
		HashMap<String, Variable> vars = new HashMap<String, Variable>();
		
		if (parent instanceof DefNode)
		{
			testDefNode(vars, (DefNode) parent);
		}
		
		int lastIndex = (index != -1 ? index : parent.size()) - 1;
		for (int i = lastIndex; i >= 0; i--)
		{
			testNode(vars, parent.getChild(i));
		}
		findVariables(vars, parent);
		return vars;
	}
	
	/**
	 * Gibt eine Liste mit allen Variablen- und Konstanten-Definitionen zurück,
	 * die an dem angegebenen Knoten gültig sind.
	 * @param node
	 * @return
	 */
	public static HashMap<String, Variable> findVariables(Node node)
	{
		HashMap<String, Variable> vars = new HashMap<String, Variable>();
		
		findVariables(vars, node);
		return vars;
	}
	
	public static Variable findVariable(String name, Node node)
	{
		HashMap<String, Variable> vars = findVariables(node);
		return vars.get(name);
	}
	
	private static void findVariables(HashMap<String, Variable> vars, Node node)
	{
		if (node instanceof RootNode) return;
		
		ListNode parent = (ListNode) node.getParent();
		if (parent instanceof DefNode)
		{
			testDefNode(vars, (DefNode) parent);
		}
		
		int lastIndex = getIndex(node) - 1;
		for (int i = lastIndex; i >= 0; i--)
		{
			testNode(vars, parent.getChild(i));
		}
		findVariables(vars, parent);
	}
	
	private static void testDefNode(HashMap<String, Variable> vars, DefNode defNode)
	{
		for (Node arg : defNode.getArgument().getChildren())
		{
			String name = ((ArgNode) arg).getName();
			if (vars.get(name) == null);
				vars.put(name, new Variable(name, arg));
		}
	}

	private static void testNode(HashMap<String, Variable> vars, Node node)
	{
		if (node instanceof AsgNode)
		{
			String name = ((AsgNode) node).getAcceptor().getName();
			if (vars.get(name) == null)
				vars.put(name, new Variable(name, node));
		}
	}
	
	private static int getIndex(Node node)
	{
		ListNode parent = (ListNode) node.getParent();
		if (parent == null) throw new RuntimeException("Syntax-tree is broken! Node has no parent: " + node);
		
		for (int i = 0; i < parent.size(); i++)
		{
			if (parent.getChild(i) == node) return i;
		}
		throw new RuntimeException("Syntax-tree is broken! Node is no child from his parent: " + node); 
	}
	
	public static class Variable
	{
		public String name;
		public Node source;
		
		public Variable(String name, Node source)
		{
			this.name = name;
			this.source = source;
		}

		@Override
		public String toString()
		{
			return source.toString();
		}
	}
}
