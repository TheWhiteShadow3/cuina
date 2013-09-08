package cuina.editor.script.internal;

import cuina.editor.script.ruby.ast.ListNode;
import cuina.editor.script.ruby.ast.Node;

/**
 * Stellt eine Position im Skript da.
 * Die Position wird durch ein Knoten und den Index eines Kindknoten definiert.
 * Der entsprechende Knoten muss nicht vorhanden sein.
 * @author TheWhiteShadow
 */
public class ScriptPosition
{
	private ListNode parent;
	private int index;
	
	public ScriptPosition(ListNode parent, int index)
	{
		this.parent = parent;
		this.index = index;
	}
	
	public ScriptPosition(Node node)
	{
		this.parent = (ListNode) node.getParent();
		if (parent == null)
			this.index = -1;
		else
			this.index = parent.getChildren().indexOf(node);
	}
	
	public ListNode getParent()
	{
		return parent;
	}

	public int getIndex()
	{
		return index;
	}
	
	/**
	 * Gibt den Knoten zu diser Skript-Position zurück.
	 * Kann <code>null</code> sein.
	 * @return Der Knoten oder null.
	 */
	public Node getNode()
	{
		if (index < 0 || index >= parent.getChildren().size()) return null;
		return parent.getChild(index);
	}

	@Override
	public String toString()
	{
		return "ScriptPosition [parent=" + parent.getNodeName() + ", index=" + index + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ScriptPosition other = (ScriptPosition) obj;
		if (index != other.index) return false;
		if (parent == null)
		{
			if (other.parent != null) return false;
		}
		else if (!parent.equals(other.parent)) return false;
		return true;
	}
}
