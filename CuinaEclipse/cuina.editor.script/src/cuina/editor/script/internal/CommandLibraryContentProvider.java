package cuina.editor.script.internal;

import cuina.editor.script.library.ClassDefinition;
import cuina.editor.script.library.FunctionDefinition;
import cuina.editor.script.library.IScriptLibrary;
import cuina.editor.script.ruby.ast.AsgNode;
import cuina.editor.script.ruby.ast.CaseNode;
import cuina.editor.script.ruby.ast.CommentNode;
import cuina.editor.script.ruby.ast.IfNode;
import cuina.editor.script.ruby.ast.Node;
import cuina.editor.script.ruby.ast.WhileNode;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class CommandLibraryContentProvider implements ITreeContentProvider
{
	private IScriptLibrary library;
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{}

	@Override
	public Object[] getElements(Object element)
	{
		if (element instanceof IScriptLibrary)
		{
			this.library = (IScriptLibrary) element;
			HashMap<String, ClassDefinition> classes = library.getClassDefinitions();
			Object[] elements = classes.values().toArray(new Object[classes.size() + 1]);
			elements[classes.size()] = new ClassDefinition("$control", "Controls");
			return elements;
		}
		return null;
	}
	
	@Override
	public void dispose() {}

	@Override
	public Object[] getChildren(Object element)
	{
		if (element instanceof ClassDefinition)
		{
			ClassDefinition clazz= (ClassDefinition) element;
			Object[] childs;
			if (clazz.id != null && clazz.id.charAt(0) == '$')
			{
				childs = new Object[5];
				childs[0] = new CommandLibraryElement(clazz, CommentNode.class);
				childs[1] = new CommandLibraryElement(clazz, AsgNode.class);
				childs[2] = new CommandLibraryElement(clazz, IfNode.class);
				childs[3] = new CommandLibraryElement(clazz, WhileNode.class);
				childs[4] = new CommandLibraryElement(clazz, CaseNode.class);
			}
			else
			{
				childs = new Object[clazz.methods.size()];
				Iterator<FunctionDefinition> itr = clazz.methods.values().iterator();
				for (int i = 0; itr.hasNext(); i++)
				{
					childs[i] = new CommandLibraryElement(itr.next());
				}
			}
			return childs;
		}
		return null;
	}

	@Override
	public Object getParent(Object element)
	{
		if (element instanceof ClassDefinition) return library;
		if (element instanceof CommandLibraryElement) return ((CommandLibraryElement) element).parent;
		return null;
	}

	@Override
	public boolean hasChildren(Object element)
	{
		return (element instanceof IScriptLibrary || element instanceof ClassDefinition);
	}
	
	public static class CommandLibraryElement
	{
		public final ClassDefinition parent;
		public String name;
		public Class<? extends Node> nodeClass;
		public FunctionDefinition function;
		
		public CommandLibraryElement(FunctionDefinition function)
		{
			this.parent = (ClassDefinition) function.parent;
			this.function = function;
			this.name = function.getLabel();
		}
		
		public CommandLibraryElement(ClassDefinition parent, Class<? extends Node> nodeClass)
		{
			this.parent = parent;
			this.name = ScriptUtil.getTitleForNode(nodeClass);
			this.nodeClass = nodeClass;
		}
	}
}
