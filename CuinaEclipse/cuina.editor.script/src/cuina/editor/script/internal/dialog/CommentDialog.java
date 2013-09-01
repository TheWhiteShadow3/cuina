package cuina.editor.script.internal.dialog;

import cuina.editor.script.ruby.ast.CommentNode;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CommentDialog extends Dialog
{
	private CommentNode node;
	private Text inText;

	public CommentDialog(Shell parent, CommentNode node)
	{
		super(parent);
		this.node = (node != null) ? node : new CommentNode("");
	}
	
	@Override
	protected void configureShell(Shell shell)
	{
		super.configureShell(shell);
		shell.setText("Script-Dialog");
		shell.setSize(640, 480);
	}
	
	@Override
	protected Control createDialogArea(Composite parent)
	{
		inText = new Text(parent, SWT.BORDER);
		
		return parent;
	}
	
	public CommentNode getNode()
	{
		node.setValue(inText.getText());
		return node;
	}
}
