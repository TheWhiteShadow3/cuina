package cuina.editor.script.internal.dialog;

import cuina.editor.core.CuinaProject;
import cuina.editor.script.internal.ScriptPosition;
import cuina.editor.script.internal.ScriptSelection;
import cuina.editor.script.internal.ruby.TreeEditor;
import cuina.editor.script.library.TreeLibrary;
import cuina.editor.script.ruby.ast.Node;

import org.eclipse.swt.widgets.Shell;

public interface ScriptDialogContext
{
	public void valueChanged(Object source, Node node);
	public TreeEditor getTreeEditor();
	public TreeLibrary getTreeLibrary();
	public ScriptSelection getSelection();
	public CuinaProject getCuinaProject();
	public Shell getShell();
	public Node getNode();
	public ScriptPosition getPosition();
}
