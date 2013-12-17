package cuina.editor.eventx.internal.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class StringEditor implements TypeEditor<String>
{
	private String value = "";
	private Text inValue;
	private Button checkNull;

	@Override
	public void init(Object value)
	{
		if (value != null)
			this.value = (String) value;
	}
	
	@Override
	public void createComponents(Composite parent)
	{
		parent.setLayout(new GridLayout(2, false));
		Handler handler = new Handler();
		
		inValue = new Text(parent, SWT.BORDER);
		inValue.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		inValue.setText(value != null ? value : "");
		
		checkNull = new Button(parent, SWT.CHECK);
		checkNull.addListener(SWT.Selection, handler);
		checkNull.setText("null");
		checkNull.setSelection(value == null);
	}
	
	@Override
	public boolean apply()
	{
		if (checkNull.getSelection())
			value = null;
		else
			value = inValue.getText();
		
		return true;
	}

	@Override
	public String getValue()
	{
		return value;
	}
	
	private class Handler implements Listener
	{
		@Override
		public void handleEvent(Event event)
		{
			inValue.setEnabled(!checkNull.getSelection());
		}
	}
}