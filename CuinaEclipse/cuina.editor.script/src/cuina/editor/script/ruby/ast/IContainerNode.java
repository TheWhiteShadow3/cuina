package cuina.editor.script.ruby.ast;

public interface IContainerNode extends Node
{
	public void setBody(BlockNode body);
	public BlockNode getBody();
}
