package cuina.editor.script.ruby.ast;

import cuina.editor.script.internal.ruby.SourceData;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Abstrakte Klasse repräsentiert einen Knoten im Syntaxbaum innerhalb eines Ruby Skriptes.
 * 
 * @author TheWhiteShadow
 */
public abstract class AbstractNode implements Node
{
	/**
	 * Leere Liste, falls ein Knoten keine Kinder besitzt.
	 */
	protected static final List<Node> EMPTY_LIST = new ArrayList<Node>(0);
	
	protected Node parent;
	private SourceData position;
	
    protected AbstractNode(SourceData position)
    {
        this.position = position;
    }
	
	@Override
	public SourceData getPosition()
	{
		return position;
	}
	
	@Override
	public Node getParent()
	{
		return parent;
	}

	@Override
	public String getNodeName()
    {
        return getClass().getSimpleName();
    }

	@Override
	public String toString()
	{
        StringBuilder builder = new StringBuilder(64);

        builder.append("(").append(getNodeName());

        builder.append(" ").append(getPosition().getToken());

        for (Node child : getChilds())
        {
            builder.append(", ").append(child);
        }
        builder.append(")");

        return builder.toString();
	}
	
    protected static List<Node> createList(Node... nodes)
    {
        ArrayList<Node> list = new ArrayList<Node>(nodes.length);
        
        for (Node node: nodes)
        {
            if (node != null) list.add(node);
        }
        
        return list;
    }
    
    protected static List<Node> createList(boolean firstList, List<Node> childs, Node... nodes)
    {
        ArrayList<Node> list = new ArrayList<Node>(nodes.length + childs.size());
        
        if (firstList) list.addAll(childs);
        for (Node node: nodes)
        {
            if (node != null) list.add(node);
        }
        if (!firstList) list.addAll(childs);
        
        return list;
    }
	
	@Override
	public abstract List<Node> getChilds();
	
	@Override
	public abstract NodeType getNodeType();
}
