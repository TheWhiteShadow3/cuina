package cuina.editor.eventx.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import cuina.eventx.Command;

public class CommandPropertyPanel
{
	private Text inName;
	private Combo inContext;
	private Combo inType;
	private Command cmd;

	public void createControl(Composite parent)
	{
		Composite block = new Composite(parent, SWT.NONE);
		block.setLayout(new GridLayout(2, false));
		
		new Label(block, SWT.NONE).setText("Name");
		inName = new Text(block, SWT.BORDER);
		
		new Label(block, SWT.NONE).setText("Kontext");
		inContext = new Combo(block, SWT.NONE);
		
		new Label(block, SWT.NONE).setText("Typ");
		inType = new Combo(block, SWT.NONE);
	}
	
	public void setCommand(Command cmd)
	{
		this.cmd = cmd;
		setValues();
	}
	
	public Command getCommand()
	{
		apply();
		return cmd;
	}
	
	private void apply()
	{
		if (cmd == null) return;
	}
	
	private void setValues()
	{
		if (cmd == null)
		{
			inName.setText("");
			inContext.setText("");
			inType.setText("");
		}
		else
		{
			inName.setText(cmd.name);
			inContext.setText(cmd.target);
//			inType.setText(cmd.);
		}
	}
}