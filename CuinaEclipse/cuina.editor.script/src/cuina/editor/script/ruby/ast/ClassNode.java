package cuina.editor.script.ruby.ast;

import cuina.editor.script.internal.ruby.SourceData;

import java.util.HashMap;

public class ClassNode extends ModuleNode
{
	private final HashMap<String, Node> instanceVars = new HashMap<String, Node>();
	private final HashMap<String, DefNode> methods = new HashMap<String, DefNode>();
	
	public ClassNode(SourceData position)
	{
		super(position);
	}
	
	public ClassNode(String name)
	{
		super(name);
	}

	@Override
	public NodeType getNodeType()
    {
        return NodeType.CLASS_NODE;
    }

	@Override
	public String toString()
	{
		return "class " + getName() + "\n" + blocktToString() + "\nend\n";
	}
	
	public HashMap<String, Node> getInstanceVars()
	{
		return instanceVars;
	}

	public HashMap<String, DefNode> getMethods()
	{
		return methods;
	}
}
