package cuina.editor.eventx.internal.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import cuina.editor.eventx.internal.CommandEditorContext;

public class BooleanEditor implements TypeEditor<Boolean>
{
	private boolean value;
	private Button inValue;

	@Override
	public void init(CommandEditorContext context, String type, Object value)
	{
		if (value != null)
			this.value = (Boolean) value;
	}
	
	@Override
	public void createComponents(Composite parent)
	{
		inValue = new Button(parent, SWT.CHECK);
		inValue.setSelection(value);
	}
	
	@Override
	public boolean apply()
	{
		value = inValue.getSelection();
		
		return true;
	}

	@Override
	public Boolean getValue()
	{
		return value;
	}
}
