package cuina.editor.eventx.internal.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;

public class IntegerEditor implements TypeEditor<Integer>
{
	private int value;
	private Spinner inValue;

	@Override
	public void init(Object value)
	{
		if (value != null)
			this.value = (Integer) value;
	}
	
	@Override
	public void createComponents(Composite parent)
	{
//		new Label(parent, SWT.NONE).setText("Value");
		inValue = new Spinner(parent, SWT.BORDER);
		inValue.setValues(value, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 1, 100);
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