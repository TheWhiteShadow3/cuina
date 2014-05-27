package cuina.editor.eventx.internal.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;

import cuina.editor.eventx.internal.CommandEditorContext;

public class IntegerEditor implements TypeEditor<Integer>
{
	private int value;
	private Spinner inValue;
	private int max;
	
	public IntegerEditor(int max)
	{
		this.max = max;
	}
	
	@Override
	public void init(CommandEditorContext context, String type, Object value)
	{
		if (value != null)
			this.value = (Integer) value;
	}
	
	@Override
	public void createComponents(Composite parent)
	{
//		new Label(parent, SWT.NONE).setText("Value");
		inValue = new Spinner(parent, SWT.BORDER);
		inValue.setValues(value, -max-1, max, 0, 1, 100);
	}
	
	@Override
	public boolean apply()
	{
		value = inValue.getSelection();
		
		return true;
	}

	@Override
	public Integer getValue()
	{
		return value;
	}
}