package cuina.editor.script.library;

import cuina.editor.script.Parameters;
import cuina.editor.script.internal.ScriptPosition;
import cuina.editor.script.internal.ScriptUtil;
import cuina.editor.script.ruby.ast.ArgNode;
import cuina.editor.script.ruby.ast.AsgNode;
import cuina.editor.script.ruby.ast.CallNode;
import cuina.editor.script.ruby.ast.ClassNode;
import cuina.editor.script.ruby.ast.ConstNode;
import cuina.editor.script.ruby.ast.DefNode;
import cuina.editor.script.ruby.ast.FixNumNode;
import cuina.editor.script.ruby.ast.INamed;
import cuina.editor.script.ruby.ast.Node;
import cuina.editor.script.ruby.ast.RootNode;
import cuina.editor.script.ruby.ast.StrNode;
import cuina.editor.script.ruby.ast.VarNode;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.viewers.ColumnLabelProvider;

/**
 * Eine dynamische Library für Abfragen am Syntax-Baum.
 * @author TheWhiteShadow
 */
public class TreeLibrary implements IScriptLibrary
{
	public static final String DEFAULT_GROUP = "default";
	private StaticScriptLibrary library;
	private ClassDefinition treeClassDef;
	private RootNode root;
	
	public TreeLibrary(StaticScriptLibrary library)
	{
		this.library = library;
	}
	
	public RootNode getRoot()
	{
		return root;
	}

	public void setRoot(RootNode root)
	{
		this.root = root;
		if (root != null) scanSyntaxTree();
	}

	private void scanSyntaxTree()
	{
		ClassNode classNode = ScriptUtil.getScriptClass(root);
		treeClassDef = new ClassDefinition(null, classNode.getName());
		
		Class clazz = ScriptUtil.getInterfaceClass(library, classNode);
		for(DefNode defNode : ScriptUtil.getClassMethods(classNode))
		{
			addFunction(defNode, clazz);
		}
	}
	
	public void addFunction(DefNode defNode, Class scriptType)
	{
		FunctionDefinition funcDef = new FunctionDefinition();
		funcDef.id = defNode.getName();
		if (scriptType != null)
		{
			for (Method m : scriptType.getMethods())
			{
				// Prüfe nur Name und Anzahl der Argumente, da Ruby keine Typen besitzt.
				if (m.getName().equals(defNode.getName()) &&
					m.getParameterTypes().length == defNode.getArgument().size())
				{
					Parameters params = m.getAnnotation(Parameters.class);
					if (params != null)
					{
						funcDef.returnType = params.returnType();
						for (int i = 0; i < params.names().length; i++)
						{
							ValueDefinition paramDef = new ValueDefinition();
							paramDef.id = params.names()[i];
							paramDef.type = params.types()[i];
							funcDef.params.add(paramDef);
						}
					}
					else
					{
						addParamDefinitions(defNode, funcDef);
					}
				}
			}
		}
		else
		{
			addParamDefinitions(defNode, funcDef);
		}
		treeClassDef.add(funcDef);
	}
	
	private void addParamDefinitions(DefNode defNode, FunctionDefinition funcDef)
	{
		// Definitionen aus den Knoten erstellen.
		for(Node arg : defNode.getArgument().getChildren())
		{
			ValueDefinition paramDef = new ValueDefinition();
			paramDef.id = ((INamed) arg).getName();
			funcDef.params.add(paramDef);
		}
	}
	
	@Override
	public HashMap<String, ClassDefinition> getClassDefinitions()
	{
		HashMap<String, ClassDefinition> definitions =
				(HashMap<String, ClassDefinition>)library.getClassDefinitions().clone();
		// füge Definition unter default hinzu.
		definitions.put(null, treeClassDef);
		return definitions;
	}
	
	/**
	 * Gibt eine Liste mit allen Variablen- und Konstanten-Definitionen zurück,
	 * die an der angegebenen Position gültig sind.
	 * @param position Position im Skript.
	 * @return Eine hierarische Liste der Variablen.
	 */
	public LibraryTree findVariables(ScriptPosition position, boolean includeStatic)
	{
		LibraryTree tree = new LibraryTree(library);

		if (position.getParent() instanceof RootNode) return tree;
		
		fillStaticVariables(tree);

		DefNode defNode = ScriptUtil.getAncestor(position.getParent(), DefNode.class);
		if (defNode != null)
		{
			addArguments(tree, defNode);
			addLocalScope(tree, defNode);
		}
		
		return tree;
	}

	private void fillStaticVariables(LibraryTree tree)
	{
		HashMap<String, ClassDefinition> classes = library.getClassDefinitions();
		
		for (ClassDefinition clazz : classes.values())
		{
			Reciver r = getReciver(tree, clazz.id);
			for (ValueDefinition var : clazz.fields.values())
			{
				r.addVariable(var);
			}
		}
	}

	private void addArguments(LibraryTree tree, DefNode defNode)
	{
		for (Node arg : defNode.getArgument().getChildren())
		{
			getReciver(tree, null).addVariable((ArgNode) arg, null);
		}
	}

	private void addLocalScope(LibraryTree tree, DefNode defNode)
	{
		String next;
		for (Node node : defNode.getChildren())
		{
			if (node instanceof AsgNode)
			{
				Node nextNode = ((AsgNode) node).getAcceptor().getNextNode();
				if (nextNode != null)
					next = ((INamed) nextNode).getName();
				else
					next = null;
				getReciver(tree, next).addVariable((AsgNode) node);
			}
		}
	}
	
	/**
	 * Gibt eine Liste mit allen Functions- und Methoden-Definitionen zurück,
	 * die an der angegebenen Position gültig sind.
	 * @param position Position im Skript.
	 * @return Eine hierarische Liste der Functionen.
	 */
	public LibraryTree createLibraryTree(ScriptPosition position)
	{
		LibraryTree tree = new LibraryTree(library);
		if (position.getParent() instanceof RootNode) return tree;
		
		HashMap<String, ClassDefinition> classes = getClassDefinitions();
		
		for (ClassDefinition clazz : classes.values())
		{
			Reciver r = getReciver(tree, clazz.id);
			for (FunctionDefinition var : clazz.methods.values())
			{
				r.addFunction(var);
			}
		}
		
//		ClassNode classNode = ScriptUtil.getAncestor(position.getParent(), ClassNode.class);
//		if (classNode != null)
//		{
//			addClassFunctions(tree, classNode);
//		}
		
		return tree;
	}
	
//	private void addClassFunctions(LibraryTree tree, ClassNode classNode)
//	{
//		String next;
//		for (Node node : classNode.getChilds())
//		{
//			if (node instanceof DefNode)
//			{
//				Node nextNode = ((DefNode) node).getNextNode();
//				if (nextNode != null)
//					next = ((INamed) nextNode).getName();
//				else
//					next = null;
//				getReciver(tree, next).addFunction((DefNode) node);
//			}
//		}
//	}
	
	private Reciver getReciver(LibraryTree tree, String name)
	{
		Reciver reciver = tree.get(name);
		if (reciver == null)
		{
			reciver = new Reciver(name);
			tree.addReciver(reciver);
		}
		return reciver;
	}
	
	@Override
	public ValueDefinition getClassVariable(String classID, String id)
	{
		return library.getClassVariable(classID, id);
	}

	@Override
	public ArrayList<FunctionDefinition> getFunctions(String returnType)
	{
		return library.getFunctions(returnType);
	}

	@Override
	public FunctionDefinition getFunction(String classID, String functionID)
	{
		if (classID == null || classID.equals("self")) classID = treeClassDef.id;
		
		return library.getFunction(classID, functionID);
	}

	@Override
	public ClassDefinition getClassDefinition(String classID)
	{
		if (classID.equals(treeClassDef.id)) return treeClassDef;
		
		return library.getClassDefinition(classID);
	}

	public static class LibraryTree extends HashMap<String, Reciver>
	{
		private static final long serialVersionUID = 7263235490431968254L;
		
		IScriptLibrary library;
		
		public LibraryTree(IScriptLibrary library)
		{
			this.library = library;
		}

		public void addReciver(Reciver reciver)
		{
			put(reciver.id, reciver);
			reciver.tree = this;
		}
		
		public Definition findVariable(String reciverName, String id)
		{
			Reciver reciver = get(reciverName);
			
			if (reciver != null)
			{
				Definition def = reciver.entries.get(id);
				if (def != null) return def;
			}
			return library.getClassVariable(reciverName, id);
		}
	}
	
	static String internalFindType(Reciver reciver, Node node)
	{
		if (node instanceof StrNode)
			return "string";
		else if (node instanceof FixNumNode)
		{
			if (((FixNumNode) node).getValue() instanceof Long)
				return "int";
			else
				return "float";
		}
		else if (node instanceof ConstNode)
		{
			String name = ((ConstNode) node).getName();
			if ("true".equals(name) || "false".equals(name))
				return "bool";
			else if ("nil".equals(name))
				return "nil";
			else if ( Character.isUpperCase(name.charAt(0)) )
				return name;
		}
		else if (node instanceof CallNode)
		{
			FunctionDefinition func = ScriptUtil.findLibraryFunction(reciver.tree.library, (CallNode) node);
			if (func != null)
				return func.returnType;
		}
		else if (node instanceof VarNode)
		{
			Node next = ((VarNode) node).getNextNode();
			Definition def;
			if (next == null)
				def = reciver.tree.findVariable(null, ((VarNode) node).getName());
			else
				def = reciver.tree.findVariable(((INamed) next).getName(), ((VarNode) node).getName());
			if (def != null)
			{
				return def.getType();
			}
		}
		return "?";
	}
	
	public static class NameProvider extends ColumnLabelProvider
	{
		@Override
		public String getText(Object element)
		{
			if (element instanceof Definition)
				return ((Definition) element).getLabel();
			else
				return super.getText(element);
		}
	}
	
	public static class TypeProvider extends ColumnLabelProvider
	{
		@Override
		public String getText(Object element)
		{
			if (element instanceof Definition)
			{
				return ((Definition) element).getType();
			}
			return "";
		}
	}
	
	public static class ScopeProvider extends ColumnLabelProvider
	{
		@Override
		public String getText(Object element)
		{
			if (element instanceof TreeDefinition)
			{
				INamed node = ((TreeDefinition) element).getNode();
				
				if (node instanceof ArgNode)
					return "argument";
				else
					return getScopeString( ((TreeDefinition) element).getScope() );
			}
			return "";
		}
		
		protected String getScopeString(int scope)
		{
			switch (scope)
			{
				case Node.LOCAL_SCOPE: return "local";
				case Node.INST_SCOPE: return "instance";
				case Node.CLASS_SCOPE: return "class";
				case Node.GLOBAL_SCOPE: return "global";
				default: return "";
			}
		}
	}
}
