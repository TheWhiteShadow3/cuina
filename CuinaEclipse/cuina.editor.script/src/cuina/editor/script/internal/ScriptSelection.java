package cuina.editor.script.internal;

import cuina.editor.script.internal.RubyNodeConverter.CommandLine;
import cuina.editor.script.internal.ScriptEditor.ScriptPage;
import cuina.editor.script.ruby.ast.ListNode;
import cuina.editor.script.ruby.ast.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;

public class ScriptSelection implements IStructuredSelection
{
	private final ScriptPage scriptPage;
	private final ScriptPosition position;
	
	public ScriptSelection(ScriptPage scriptPage, CommandLine commandLine)
	{
		if (scriptPage == null) throw new NullPointerException("scriptPage is null");
		
		this.scriptPage = scriptPage;
		this.position = (commandLine != null) ? commandLine.position : null;
	}
	
	public ScriptSelection(ScriptPage scriptPage, ListNode parent, int index)
	{
		this.scriptPage = scriptPage;
		this.position = new ScriptPosition(parent, index);
	}

	public ScriptPage getScriptPage()
	{
		return scriptPage;
	}

	public ScriptPosition getPosition()
	{
		return position;
	}
	
//	public Node getNode()
//	{
//		if (parent == null || index < 0 || index >= parent.getChilds().size()) return null;
//		return parent.getChilds().get(index);
//	}
	
	@Override
	public boolean isEmpty()
	{
		return position == null || position.getIndex() == -1;
	}

	@Override
	public Object getFirstElement()
	{
		if (position == null) return null;
		
		return position.getNode();
	}

	@Override
	public Iterator iterator()
	{
		return new Iterator()
		{
			int pos = 0;
			@Override public boolean hasNext() 	{ return pos == 0; }
			@Override public Object next() 		{ pos++; return getFirstElement(); }
			@Override public void remove() 		{ throw new UnsupportedOperationException(); }
		};
	}

	@Override
	public int size()
	{
		if (position == null) return 0;
		
		return position.getNode() != null ? 1 : 0;
	}

	@Override
	public Object[] toArray()
	{
		return new Object[] { getFirstElement() };
	}

	@Override
	public List toList()
	{
		List l = new ArrayList<Node>(1);
		l.add(getFirstElement());
		return l;
	}
	

	@Override
	public String toString()
	{
		return scriptPage.getName() + " -> " + position;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + scriptPage.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ScriptSelection other = (ScriptSelection) obj;
		if (position == null)
		{
			if (other.position != null) return false;
		}
		else if (!position.equals(other.position)) return false;
		if (!scriptPage.equals(other.scriptPage)) return false;
		return true;
	}
}