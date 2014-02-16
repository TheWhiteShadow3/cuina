package cuina.editor.script.internal;

import cuina.editor.script.Parameters;
import cuina.editor.script.internal.ruby.TreeEditor;
import cuina.editor.script.library.ClassDefinition;
import cuina.editor.script.library.FunctionDefinition;
import cuina.editor.script.library.IScriptLibrary;
import cuina.editor.script.library.StaticScriptLibrary;
import cuina.editor.script.library.StaticScriptLibrary.ScriptType;
import cuina.editor.script.library.ValueDefinition;
import cuina.editor.script.ruby.ast.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Stellt statische High-Level-Methoden für Abfragen und Änderungen am Syntax-Baum zur Verfügung.
 * @author TheWhiteShadow
 */
public class ScriptUtil
{
	private ScriptUtil() {};
	

	public static ClassNode getScriptClass(RootNode root)
	{
		List<Node> nodes = root.getChildren();
		
		Node node = nodes.get(0);
		if (node instanceof ClassNode)
		{
			return (ClassNode) node;
		}
		
		return null;
	}
	
	/**
	 * Gibt das Klassen-Interface zurück.
	 * <P>
	 * Minimal-Beispiel mit dem Interface <code>Bar</code>:
	 * <pre>
	 * class S_Fuu
	 *   include Bar
	 * end
	 * </pre>
	 * </p>
	 * @param classNode Klasse.
	 * @return Den Interface Klassen-Name.
	 */
	public static String getInterface(ClassNode classNode)
	{
		if (classNode.size() == 0) return null;
		
		Node child = classNode.getChild(0);
		if (child instanceof CallNode)
		{
			CallNode callNode = (CallNode) child;
			if ("include".equals(callNode.getName()) )
			{
				Node arg =  callNode.getArgument().getChild(0);
				if (arg instanceof ConstNode)
				{
					return getFullName((INamed) arg);
				}
			}
		}
		return null;
	}
	
	
	public static Class getInterfaceClass(StaticScriptLibrary libary, ClassNode classNode)
	{
		String rubyType = getInterface(classNode);
		if (rubyType == null) return null;
		
		for (ScriptType type : libary.getScriptTypes())
		{
			if (type.getFullPath().equals(rubyType)) return type.clazz;
		}
		return null;
	}
	
	public static void setInterface(TreeEditor treeEditor, ClassNode classNode, Class clazz, String call)
	{
		ConstNode node = null;
		if (clazz != null)
		{
			if (call == null) throw new NullPointerException("call is null");
			node = new ConstNode(call);
		}
		
		if (classNode.size() == 0)
		{
			CallNode callNode = new CallNode("include");
			callNode.addArgument(node);
			treeEditor.insertChild(new ScriptPosition(classNode, 0), callNode);
		}
		else
		{
			Node child = classNode.getChild(0);
			if (child instanceof CallNode)
			{
				CallNode callNode = (CallNode) child;
				if ("include".equals(callNode.getName()) )
				{
					if (clazz == null)
					{
						treeEditor.removeChild(new ScriptPosition(classNode, 0));
						return;
					}
					else
						treeEditor.setParam(callNode, 0, node);
				}
				else if (clazz != null)
				{
					callNode = new CallNode("include");
					callNode.addArgument(node);
					treeEditor.insertChild(new ScriptPosition(classNode, 0), callNode);
				}
				else return;
			}
			else return;
		}
		// Validiere die Interface-Methoden
		List<DefNode> rubyMethods = getClassMethods(classNode);
		Method[] interfaceMethods = clazz.getMethods();
		JLoop:
		for (Method jMethod : interfaceMethods)
		{
			for (DefNode rMethod : rubyMethods)
			{
				if (compareMethods(jMethod, rMethod)) continue JLoop;
			}
			// Methode existiert nicht
			treeEditor.insertChild(new ScriptPosition(classNode, -1), createMethode(jMethod));
		}
	}
	
	public static List<DefNode> getClassMethods(RootNode root)
	{
		return getClassMethods(getScriptClass(root));
	}
	
	public static List<DefNode> getClassMethods(ClassNode classNode)
	{
		ArrayList<DefNode> list = new ArrayList<DefNode>();
		for (Node node : classNode.getChildren())
		{
			if (node instanceof DefNode) list.add((DefNode) node);
		}
		return list;
	}
	
	public static boolean compareMethods(Method jMethod, DefNode rMethod)
	{
		if (jMethod == null || rMethod == null) return false;
		if (!jMethod.getName().equals(rMethod.getName())) return false;
		
		Class[] jArgs = jMethod.getParameterTypes();
		List<Node> rArgs = rMethod.getArgument().getChildren();
		if (jArgs.length != rArgs.size()) return false;
		
		// Typen können nicht vergleicht werden und die Namen sind egal.
		return true;
	}
	
	/**
	 * Gibt den Vollqualifizierten Namen eines Ruby-Elements zurück.
	 * Der Konten muss ein Benamtes Element sein.
	 * <p>
	 * Mögliche Knoten sind:
	 * <ul>
	 * <li>DefNode</li>
	 * <li>CallNode</li>
	 * <li>ConstNode</li>
	 * <li>keyCallNode</li>
	 * <li>VarNode</li>
	 * </ul>
	 * Bei allen anderen INamed-Knoten wird lediglich der Name zurückgegeben.
	 * </p>
	 * @param node Element-Konten.
	 * 
	 * @return Vollqualifizierter Name des Ruby-Elements.
	 */
	public static String getFullName(final INamed node)
	{
		StringBuilder builder = new StringBuilder(node.getName());
		if (!(node instanceof IHasNext))
		{
			return builder.toString();
		}
		
		IHasNext last = (IHasNext) node;
		Node reciver;
		while ((reciver = last.getNextNode()) instanceof INamed)
		{
			if (reciver instanceof ConstNode && last instanceof ConstNode)
				builder.insert(0, "::");
			else
				builder.insert(0, '.');
			builder.insert(0, ((INamed) reciver).getName());
			if (!(reciver instanceof IHasNext)) break;
			
			last = (IHasNext) reciver;
		}
		return builder.toString();
	}
	
	public static Node getReciver(Node node)
	{
		if (node instanceof AsgNode)
			node = ((AsgNode) node).getAcceptor();
		
		if (node instanceof IHasNext)
			return ((IHasNext) node).getNextNode();
		return null;
	}
	
//	public static String getAssingmentType(ScriptLibrary library, AsgNode asgNode)
//	{
//		Node node;
//		if (asgNode.getArgument() instanceof ArrayNode)
//			node = ((ArrayNode) asgNode.getArgument()).getChild(0);
//		else
//			node = asgNode.getArgument();
//		
//		if (node instanceof CallNode)
//		{
//			FunctionDefinition def = _findLibraryFunction(library, node);
//			if (def != null) return def.returnType;
//		}
//		else if (node instanceof StrNode)
//			return "string";
//		else if (node instanceof FixNumNode)
//			return "number";
//		
//		return null;
//	}
	
	public static ClassDefinition findClassDefinition(IScriptLibrary library, ConstNode node)
	{
		return library.getClassDefinition(node.getName());
	}
	
	public static FunctionDefinition findLibraryFunction(IScriptLibrary library, DefNode node)
	{
		return _findLibraryFunction(library, node);
	}
	
	public static FunctionDefinition findLibraryFunction(IScriptLibrary library, CallNode node)
	{
		return _findLibraryFunction(library, node);
	}
	
	private static FunctionDefinition _findLibraryFunction(IScriptLibrary library, INamed node)
	{
		Node next = ((IHasNext) node).getNextNode();
		String clazz = null;
		if (next instanceof INamed)
		{
			clazz = ((INamed) next).getName();
		}
		return library.getFunction(clazz, node.getName());
	}
	
	public static ValueDefinition findLibraryAttribut(IScriptLibrary library, VarNode node)
	{
		String clazz = null;
		if (node instanceof IHasNext)
		{
			Node next = ((IHasNext) node).getNextNode();
			if (next instanceof INamed)
			{
				clazz = ((INamed) next).getName();
			}
		}
		return library.getClassVariable(clazz, node.getName());
	}
	
	public static ValueDefinition findLibraryAttribut(IScriptLibrary library, ArgNode node)
	{
		Node parent = node.getParent();
		if (parent instanceof ListNode) parent = parent.getParent();
		FunctionDefinition func = _findLibraryFunction(library, (INamed) parent);
		if (func == null) return null;
		
		ValueDefinition arg;
		for (int i = 0; i < func.params.size(); i++)
		{
			arg = func.params.get(i);
			if (arg.getID().equals(node.getName())) return arg;
		}
		return null;
	}
	
	public static FunctionDefinition createScriptFunction(IScriptLibrary library, DefNode node)
	{
		FunctionDefinition result = findLibraryFunction(library, node);
		
		if (result == null)
		{
			result = new FunctionDefinition();
			result.id = node.getName();
//			if (node.getNextNode() != null)
//				result.sectionID = ((ConstNode) node.getNextNode()).getName();
			
			List<Node> args = node.getArgument().getChildren();
			for (int i = 0; i < args.size(); i++)
			{
				ValueDefinition param = new ValueDefinition();
				param.id = ((ArgNode) args.get(i)).getName();
				result.params.add(param);
			}
		}
		return result;
	}
	
//	public static Node createDefaultValueNode(ValueDefinition param)
//	{
//		switch(param.type)
//		{
//			case "string":	return new StrNode("");
//			case "int":		return new FixNumNode(0L);
//			case "float":	return new FixNumNode(0D);
//			case "boolean":	return new ConstNode("false");
//			
//			default:
//				if (param.type.startsWith("key:"))
//					return new StrNode("");
//				else
//					return new ConstNode("nil");
//		}
//	}
	
	public static String getTitleForNode(Class<? extends Node> nodeClass)
	{
		if (nodeClass == DefNode.class) 	return "Seiten Definition";
		if (nodeClass == CallNode.class) 	return "Funktion";
		if (nodeClass == AsgNode.class) 	return "Zuweisung";
		if (nodeClass == IfNode.class) 		return "Bedingung";
		if (nodeClass == WhileNode.class) 	return "Schleife";
		if (nodeClass == CaseNode.class) 	return "Auswahl";
		if (nodeClass == CommentNode.class) return "Kommentar";
		return null;
	}
	
	/**
	 * Erstellt eine Methoden-Definition aus einer Java-Methode.
	 * Die Methoden-Signatur darf nur gültige Ruby-Bezeichner besitzen.
	 * @param method
	 * @return Die Methoden-Definition.
	 */
	public static DefNode createMethode(Method method)
	{
		Parameters param = method.getAnnotation(Parameters.class);
		String[] args = (param == null) ? new String[0] : param.names();
		return createMethode(method.getName(), args);
	}
	
	/**
	 * Erstellt eine Methoden-Definition mit dem angegebenen Namen und Argumenten.
	 * @param name
	 * @param args
	 * @return Die Methoden-Definition.
	 */
	public static DefNode createMethode(String name, String[] args)
	{
		DefNode defNode = new DefNode(name);
		for (String arg : args)
		{
			defNode.addArgument(new ArgNode(arg));
		}
		return defNode;
	}
	
	public static <E> E getAncestor(Node node, Class<E> type)
	{
		if (type.isInstance(node)) return (E) node;
		while(node != null)
		{
			node = node.getParent();
			if (node != null && type.isInstance(node)) return (E)node;
		}
		return null;
	}
	
//	public static CallNode createMethodeCall(String name, String[] args)
//	{
//		CallNode callNode = new CallNode(name);
//		for (String arg : args)
//		{
//			callNode.addArgument(new ConstNode(arg));
//		}
//		return callNode;
//	}
	
}
