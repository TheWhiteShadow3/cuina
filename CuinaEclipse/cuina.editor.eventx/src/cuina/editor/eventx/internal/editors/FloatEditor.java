package cuina.editor.eventx.internal.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;

import cuina.editor.eventx.internal.CommandEditorContext;

public class FloatEditor implements TypeEditor<Float>
{
	private static final float PRECISION = 1000f;
	
	private float value;
	private Spinner inValue;

	@Override
	public void init(CommandEditorContext context, Object value)
	{
		if (value != null)
			this.value = (Float) value;
	}
	
	@Override
	public void createComponents(Composite parent)
	{
//		new Label(parent, SWT.NONE).setText("Value");
		inValue = new Spinner(parent, SWT.BORDER);
		inValue.setValues((int) (value * PRECISION), Integer.MIN_VALUE, Integer.MAX_VALUE, 3, 1, 100);
	}
	
	@Override
	public boolean apply()
	{
		value = inValue.getSelection() / PRECISION;
		
		return true;
	}

	@Override
	public Float getValue()
	{
		return value;
	}
}
