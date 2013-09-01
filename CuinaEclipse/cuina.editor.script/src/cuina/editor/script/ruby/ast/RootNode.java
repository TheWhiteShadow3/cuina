package cuina.editor.script.ruby.ast;

import java.util.HashMap;

import cuina.editor.script.internal.ruby.SourceData;

public class RootNode extends BlockNode implements IScope
{
	private final HashMap<String, Node> localVars = new HashMap<String, Node>();
	private final HashMap<String, Node> globalVars = new HashMap<String, Node>();
	private final HashMap<String, DefNode> globalFunctions = new HashMap<String, DefNode>();
	
	public RootNode(SourceData position)
	{
		super(position);
	}
	
	@Override
	public NodeType getNodeType()
	{
		return NodeType.ROOT_NODE;
	}

	public HashMap<String, Node> getGlobalVars()
	{
		return globalVars;
	}

	public HashMap<String, DefNode> getGlobalFunctions()
	{
		return globalFunctions;
	}
	
	@Override
	public HashMap<String, Node> getLocalVars()
	{
		return localVars;
	}
}
