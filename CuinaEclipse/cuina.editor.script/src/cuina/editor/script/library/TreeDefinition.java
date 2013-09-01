package cuina.editor.script.library;

import cuina.editor.script.ruby.ast.INamed;

public interface TreeDefinition extends Definition
{
	public INamed getNode();
	public int getScope();
}