package cuina.editor.eventx.internal.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import cuina.editor.eventx.internal.CommandEditorContext;
import cuina.util.Vector;

public class VectorEditor implements TypeEditor<Vector>
{
	private static final float DIGIT_FACTOR = 1000;
	
	private Vector vector;
	private Spinner inX;
	private Spinner inY;
	private Spinner inZ;

	@Override
	public void init(CommandEditorContext context, Object vector)
	{
		if (vector != null)
			this.vector = (Vector) vector;
		else
			this.vector = new Vector();
	}
	
	@Override
	public void createComponents(Composite parent)
	{
		parent.setLayout(new GridLayout(2, false));
		
		new Label(parent, SWT.NONE).setText("X");
		inX = new Spinner(parent, SWT.NONE);
		
		new Label(parent, SWT.NONE).setText("Y");
		inY = new Spinner(parent, SWT.NONE);
		
		new Label(parent, SWT.NONE).setText("Z");
		inZ = new Spinner(parent, SWT.NONE);
		
		inX.setValues(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 3, (int) DIGIT_FACTOR, 10000);
		inY.setValues(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 3, (int) DIGIT_FACTOR, 10000);
		inZ.setValues(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 3, (int) DIGIT_FACTOR, 10000);
		
		if (vector != null)
		{
			inX.setSelection((int) (vector.x * DIGIT_FACTOR));
			inY.setSelection((int) (vector.y * DIGIT_FACTOR));
			inZ.setSelection((int) (vector.z * DIGIT_FACTOR));
		}
	}
	
	@Override
	public boolean apply()
	{
		vector.x = inX.getSelection() / DIGIT_FACTOR;
		vector.y = inY.getSelection() / DIGIT_FACTOR;
		vector.z = inZ.getSelection() / DIGIT_FACTOR;
		
		return vector.lenght() > 1;
	}

	@Override
	public Vector getValue()
	{
		return vector;
	}
}