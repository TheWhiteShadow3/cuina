package cuina.editor.script.ruby.ast;

import java.util.HashMap;

public interface IScope
{
	public HashMap<String, Node> getLocalVars();
}
