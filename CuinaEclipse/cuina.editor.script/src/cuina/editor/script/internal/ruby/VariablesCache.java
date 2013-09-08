package cuina.editor.script.internal.ruby;

import cuina.editor.script.ruby.ast.AsgNode;
import cuina.editor.script.ruby.ast.BlockNode;
import cuina.editor.script.ruby.ast.ClassNode;
import cuina.editor.script.ruby.ast.DefNode;
import cuina.editor.script.ruby.ast.INamed;
import cuina.editor.script.ruby.ast.IScope;
import cuina.editor.script.ruby.ast.ModuleNode;
import cuina.editor.script.ruby.ast.Node;
import cuina.editor.script.ruby.ast.RootNode;
import cuina.editor.script.ruby.ast.VarNode;

import java.util.HashMap;

@Deprecated
public class VariablesCache
{
	/* Variablen-Cache. Sortiert nach Scope->Name->Node
	 * Scope ist entwerder eine Funktion, eine Klasse, ein Modul oder der RootNode selbst.
	 */
	private final HashMap<IScope, HashMap<String, CacheEntry>> vars =
			new HashMap<IScope, HashMap<String, CacheEntry>>();
	
	private RootNode root;
	
	public VariablesCache(RootNode root)
	{
		this.root = root;
		findVariables(root, null, null, root);
	}
	
	private void findVariables(BlockNode current, ModuleNode module, ClassNode clazz, IScope local)
	{
		for (Node node : current.getChildren())
		{
			if (node instanceof ClassNode)
				findVariables((ClassNode) node, module, (ClassNode) node, (IScope) node);
			else if (node instanceof ModuleNode)
				findVariables((ModuleNode) node, (ModuleNode) node, clazz, (IScope) node);
			else if (node instanceof DefNode)
			{
				DefNode defNode = (DefNode) node;
				for (Node arg : defNode.getArgument().getChildren())
				{
					addVariable(defNode, arg);
				}
				
				findVariables(defNode, module, clazz, (IScope) node);
			}
			else if (node instanceof BlockNode)
				findVariables((BlockNode) node, module, clazz, local);
			
			if (node instanceof AsgNode)
			{
				VarNode varNode = ((AsgNode) node).getAcceptor();
				IScope scope;
				switch(varNode.getScope())
				{
					case VarNode.LOCAL_SCOPE: 	scope = local; break;
					case VarNode.CLASS_SCOPE: 	scope = module; break;
					case VarNode.INST_SCOPE:	scope = clazz; break;
					default: 					scope = root; break;
				}
				if (scope == null) throw new RuntimeException("illegal scope");
				addVariable(scope, varNode);
			}
		}
	}
	
	public void addVariable(IScope scope, Node node)
	{
		if (!(node instanceof INamed)) throw new IllegalArgumentException("node must implements INamed");
		
		String name = ((INamed) node).getName();
		HashMap<String, CacheEntry> table = vars.get(scope);
		if (table == null)
		{
			table = new HashMap<String, CacheEntry>();
			table.put(name, new CacheEntry(node));
			vars.put(scope, table);
		}
		else
		{
			CacheEntry entry = table.get(name);
			if (entry == null)
				table.put(name, new CacheEntry(node));
			else
				entry.countUp(node);
		}
	}
	
	public void removeVariable(IScope scope, String name)
	{
		HashMap<String, CacheEntry> table = vars.get(scope);
		if (table == null) throw new RuntimeException("variable " + name + " not found");
		CacheEntry entry = table.get(name);
		if (entry == null) throw new RuntimeException("variable " + name + " not found");
		
		if (!entry.countDown()) table.remove(entry);
	}
	
	public Node findVariable(BlockNode block, String name)
	{
		if (block instanceof IScope)
		{
			HashMap<String, CacheEntry> table = vars.get(block);
			if (table == null) return null;
			return table.get(name).node;
		}
		else if (block.getParent() instanceof BlockNode)
		{
			return findVariable((BlockNode) block.getParent(), name);
		}
		else return null;
	}
	
	private static class CacheEntry
	{
		public int count;
		public final Node node;
		
		public CacheEntry(Node node)
		{
			this.node = node;
			count = 1;
		}
		
		public void countUp(Node node)
		{
			if (this.node == node) return;
			count++;
		}
		
		public boolean countDown()
		{
			return (--count == 0);
		}
	}
}
