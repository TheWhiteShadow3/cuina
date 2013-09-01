package cuina.editor.script.library;

import cuina.editor.script.internal.ScriptUtil;
import cuina.editor.script.ruby.ast.ArgNode;
import cuina.editor.script.ruby.ast.AsgNode;
import cuina.editor.script.ruby.ast.INamed;
import cuina.editor.script.ruby.ast.ListNode;
import cuina.editor.script.ruby.ast.Node;
import cuina.editor.script.ruby.ast.VarNode;

public class Variable implements TreeDefinition
{
	private final String id;
	private Reciver reciver;
	private INamed nameSource;
	private Node definitionSource;
	private ValueDefinition valueDefinition;
	private String type;
	
	public Variable(Reciver reciver, AsgNode node)
	{
		this.reciver = reciver;
		this.nameSource = node.getAcceptor();
		if (node.getArgument() instanceof ListNode)
			this.definitionSource = ((ListNode) node.getArgument()).getChild(0);
		else
			this.definitionSource = node.getArgument();
		this.id = nameSource.getName();
		this.valueDefinition = ScriptUtil.findLibraryAttribut(reciver.tree.library, node.getAcceptor());
	}
	
	public Variable(Reciver reciver, ArgNode node, FunctionDefinition funcDef)
	{
		this.reciver = reciver;
		this.nameSource = node;
		if (node.getParent() instanceof ListNode)
			this.definitionSource = node.getParent().getParent();
		else
			this.definitionSource = node.getParent();
		this.id = nameSource.getName();
		if (funcDef == null) return;
		
		ValueDefinition argDef;
		for (int i = 0; i < funcDef.params.size(); i++)
		{
			argDef = funcDef.params.get(i);
			if (argDef.getID().equals(node.getName()))
			{
				this.valueDefinition = argDef;
				return;
			}
		}
	}
	
	public Variable(Reciver reciver, ValueDefinition def)
	{
		this.reciver = reciver;
		this.id = def.getID();
		this.valueDefinition = def;
	}
	
	@Override
	public String getID()
	{
		return id;
	}

	@Override
	public String getLabel()
	{
		if (valueDefinition != null)
			return valueDefinition.getLabel();
		else
			return id;
	}

	@Override
	public Definition getParent()
	{
		return reciver;
	}

	@Override
	public String getType()
	{
		if (type == null)
		{
			if (valueDefinition != null)
				type = valueDefinition.getType();
			else
				type = TreeLibrary.internalFindType(reciver, definitionSource);
		}
		return type;
	}
	
	@Override
	public INamed getNode()
	{
		return nameSource;
	}

	@Override
	public int getScope()
	{
		if (nameSource instanceof VarNode)
			return ((VarNode) nameSource).getScope();
		else if (nameSource instanceof ArgNode)
			return Node.LOCAL_SCOPE;
		else
			return Node.UNKNOWN_SCOPE;
	}
	
	@Override
	public String toString()
	{
		return getLabel();
	}
}