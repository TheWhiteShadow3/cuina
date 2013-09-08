package cuina.editor.script.ruby.ast;

import cuina.editor.script.internal.ruby.SourceData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListNode extends AbstractNode implements Iterable<Node>
{
	private final ArrayList<Node> childs;

	public ListNode(SourceData position)
	{
		super(position);
		this.childs = new ArrayList<Node>(4);
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.LIST_NODE;
	}

	@Override
	public Iterator<Node> iterator()
	{
		return childs.iterator();
	}

	public int size()
	{
		return childs.size();
	}

	@Override
	public List<Node> getChildren()
	{
		return childs;
	}

	public Node getChild(int index)
	{
		return childs.get(index);
	}

	public void set(int index, Node e)
	{
		childs.set(index, e);
		((AbstractNode) e).parent = this;
	}

	public void add(Node e)
	{
		childs.add(e);
		((AbstractNode) e).parent = this;
	}

	public void add(int index, Node e)
	{
		if (index == -1)
		{
			add(e);
			return;
		}
		childs.add(index, e);
		((AbstractNode) e).parent = this;
	}

	public Node remove(int index)
	{
		Node e = childs.get(index);
		((AbstractNode) e).parent = null;
		childs.remove(index);
		return e;
	}
//	
//	public Node remove(Node e)
//	{
//		return remove(childs.indexOf(e));
//	}
}
