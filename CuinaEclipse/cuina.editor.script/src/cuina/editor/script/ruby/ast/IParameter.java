package cuina.editor.script.ruby.ast;

public interface IParameter extends Node
{
	public void addArgument(Node arg);
	public void setArgument(Node arg);
	public void removeArgument(int index);
	public Node getArgument();
}
