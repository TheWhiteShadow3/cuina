package cuina.editor.script.ruby.ast;

import cuina.editor.script.internal.ruby.SourceData;

import java.util.HashMap;

public class ModuleNode extends BlockNode implements INamed, IScope
{
	private final HashMap<String, Node> localVars = new HashMap<String, Node>();
	private final HashMap<String, Node> staticVars = new HashMap<String, Node>();
	private final HashMap<String, DefNode> staticFunctions = new HashMap<String, DefNode>();
	private String name;
	
	public ModuleNode(SourceData position)
	{
		super(position);
		this.name = position.getToken().getValue();
	}
	
	public ModuleNode(String name)
	{
		super(null);
		this.name = name;
	}

	@Override
	public NodeType getNodeType()
    {
        return NodeType.MODULE_NODE;
    }
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public void setName(String name)
	{
		this.name = name;
	}
	
	@Override
	public String toString()
	{
		return "module " + name + "\n" + blocktToString() + "\nend\n";
	}
	
	public HashMap<String, Node> getStaticVars()
	{
		return staticVars;
	}

	public HashMap<String, DefNode> getStaticFunctions()
	{
		return staticFunctions;
	}
	
	@Override
	public HashMap<String, Node> getLocalVars()
	{
		return localVars;
	}
}
