package cuina.editor.eventx.internal.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import cuina.editor.eventx.internal.CommandEditorContext;

public class UndefinedTypeEditor implements TypeEditor<Object>
{
	private Object value;

	@Override
	public void init(CommandEditorContext context, Object obj)
	{
		this.value = obj;
	}
	
	@Override
	public void createComponents(Composite parent)
	{
		Text label = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
		label.setText(value != null ? value.toString() : "null");
	}

	@Override
	public Object getValue()
	{
		return value;
	}

	@Override
	public boolean apply()
	{
		return true;
	}
}
