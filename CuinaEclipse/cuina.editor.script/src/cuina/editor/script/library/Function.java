package cuina.editor.script.library;

import cuina.editor.script.internal.ScriptUtil;
import cuina.editor.script.ruby.ast.DefNode;
import cuina.editor.script.ruby.ast.Node;

public class Function implements TreeDefinition
{
	private final String id;
	private Reciver reciver;
	private DefNode node;
	private FunctionDefinition funcDefinition;
	private int scope;
	
	public Function(Reciver reciver, DefNode node)
	{
		this.reciver = reciver;
		this.node = node;
		this.id = node.getName();
		this.funcDefinition = ScriptUtil.findLibraryFunction(reciver.tree.library, node);
		
		if ("self".equals(reciver.id))
			this.scope = Node.CLASS_SCOPE;
		else
			this.scope = Node.INST_SCOPE;
	}

	public Function(Reciver reciver, FunctionDefinition def)
	{
		this.reciver = reciver;
		this.id = def.getID();
		this.funcDefinition = def;
		
		if (def.staticFunction)
			this.scope = Node.CLASS_SCOPE;
		else
			this.scope = Node.INST_SCOPE;
	}
	
	@Override
	public String getID()
	{
		return id;
	}

	@Override
	public String getLabel()
	{
		if (funcDefinition != null)
			return funcDefinition.getLabel();
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
		if (funcDefinition != null)
			return funcDefinition.getType();
		else
			return "?";
	}

	@Override
	public DefNode getNode()
	{
		return node;
	}

	@Override
	public int getScope()
	{
		return scope;
	}

	public FunctionDefinition getFunctionDefinition()
	{
		return funcDefinition;
	}
}