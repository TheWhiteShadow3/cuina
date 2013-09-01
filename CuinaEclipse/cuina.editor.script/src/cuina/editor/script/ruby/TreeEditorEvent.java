package cuina.editor.script.ruby;

import cuina.editor.script.internal.ScriptPosition;
import cuina.editor.script.ruby.ast.Node;

public class TreeEditorEvent
{
	private Object source;
	private ScriptPosition position;
	private Node node;

	public TreeEditorEvent(Object source, ScriptPosition position, Node node)
	{
		this.source = source;
		this.position = position;
		this.node = node;
	}

	public Object getSource()
	{
		return source;
	}

	public Node getParent()
	{
		return position.getParent();
	}
	
	public ScriptPosition getPosition()
	{
		return position;
	}

	public int getIndex()
	{
		return position.getIndex();
	}

	public Node getNode()
	{
		return node;
	}
}
