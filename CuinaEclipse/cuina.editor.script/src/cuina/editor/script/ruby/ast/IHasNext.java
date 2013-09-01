package cuina.editor.script.ruby.ast;

public interface IHasNext extends Node
{
	public Node getNextNode();
	public void setNextNode(Node next);
}
