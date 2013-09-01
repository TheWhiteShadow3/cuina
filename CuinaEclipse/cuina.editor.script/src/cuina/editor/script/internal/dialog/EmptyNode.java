package cuina.editor.script.internal.dialog;

import cuina.editor.script.ruby.ast.AbstractNode;
import cuina.editor.script.ruby.ast.Node;
import cuina.editor.script.ruby.ast.NodeType;

import java.util.List;

/**
 * Der EmptyNode steht für ein nicht gesetztes, aber vorhandenes Element im Syntax-Baum.
 * Wird verwendet um im Editor fehlende Einträge anzuzeigen,
 * da <code>null</code>-Elemente im Baum nicht erlaubt sind.
 * @author TheWhiteShadow
 */
public class EmptyNode extends AbstractNode
{
	public EmptyNode()
	{
		super(null);
	}

	@Override
	public List<Node> getChilds()
	{
		return AbstractNode.EMPTY_LIST;
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.OTHER;
	}

	@Override
	public String toString()
	{
		return "???";
	}
}
