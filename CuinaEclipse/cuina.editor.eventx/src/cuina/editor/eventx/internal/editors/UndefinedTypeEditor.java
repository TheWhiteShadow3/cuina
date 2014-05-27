package cuina.editor.eventx.internal.editors;

import cuina.editor.eventx.internal.CommandEditorContext;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class UndefinedTypeEditor implements TypeEditor<Object>
{
	private Class<?> clazz;
	private Object value;

	@Override
	public void init(CommandEditorContext context, String type, Object obj)
	{
		try
		{
			this.clazz = context.getCommandLibrary().getClass(type);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		this.value = obj;
	}
	
	@Override
	public void createComponents(Composite parent)
	{
		Button button = new Button(parent, SWT.NONE);
		button.setText(value != null ? value.toString() : "null");
		button.addListener(SWT.Selection, new Listener()
		{
			@Override
			public void handleEvent(Event event)
			{
				System.out.println("Klasse: " + clazz);
			}
		});
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
