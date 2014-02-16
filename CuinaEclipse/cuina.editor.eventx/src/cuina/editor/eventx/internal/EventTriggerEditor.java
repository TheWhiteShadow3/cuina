package cuina.editor.eventx.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import cuina.database.DataTable;
import cuina.database.Database;
import cuina.database.ui.DatabaseComboViewer;
import cuina.editor.core.IEditorContext;
import cuina.editor.event.EventRegistry;
import cuina.editor.event.IEventDescriptor;
import cuina.editor.event.ui.ITriggerEditor;
import cuina.editor.ui.DefaultComboViewer;
import cuina.editor.ui.WidgetFactory;
import cuina.event.Event;
import cuina.event.Trigger;
import cuina.eventx.CommandList;
import cuina.eventx.InterpreterTrigger;
import cuina.resource.ResourceException;

public class EventTriggerEditor implements ITriggerEditor
{
	private IEditorContext context;
	private InterpreterTrigger trigger;
	
	private DefaultComboViewer<IEventDescriptor> eventViewer;
	private Text inArg;
	private DatabaseComboViewer<CommandList> keyViewer;
	private boolean update;
	
	@Override
	public Trigger getTrigger()
	{
		trigger.setEvent(eventViewer.getSelectedElement().getEvent());
		trigger.setArgument(parseArgument(inArg.getText()));
		trigger.setKey(getCommandListKey());
		
		return trigger;
	}

	@Override
	public void setTrigger(Trigger trigger)
	{
		update = true;
		if (trigger == null) trigger = new InterpreterTrigger(Event.NEVER);
		this.trigger = (InterpreterTrigger) trigger;
		
		eventViewer.setSelectedElement( EventRegistry.getEventDescriptor(this.trigger.getEvent()) );
		
		Object arg = this.trigger.getArgument();
		inArg.setText(arg != null ? arg.toString() : "");
		keyViewer.setSelection(this.trigger.getKey());
		update = false;
	}

	@Override
	public void init(IEditorContext context)
	{
		this.context = context;
	}

	@Override
	public void createComponents(Composite parent)
	{
		EditorHandler handler = new EditorHandler();
		
		parent.setLayout(new GridLayout(2, false));
		this.eventViewer = WidgetFactory.createComboViewer(
				parent, "Ereignis", EventRegistry.getEventDescriptors(), false);
		eventViewer.getControl().addListener(SWT.Selection, handler);
		this.inArg = WidgetFactory.createText(parent, "Argument");
		inArg.addListener(SWT.Modify, handler);
		try
		{
			DataTable<CommandList> table = context.getCuinaProject().getService(Database.class).loadTable("Event");
			this.keyViewer = WidgetFactory.createDatabaseComboViewer(parent, "Command", table);
			keyViewer.getControl().addListener(SWT.Selection, handler);
		}
		catch (ResourceException e)
		{
			e.printStackTrace();
		}
	}
	
	private Object parseArgument(String text)
	{
		if (text.isEmpty()) return null;
		if ("true".equalsIgnoreCase(text)) return Boolean.TRUE;
		if ("false".equalsIgnoreCase(text)) return Boolean.FALSE;
		
		if (Character.isDigit(text.charAt(0))) try
		{
			if (text.indexOf('.') != -1)
				return Float.valueOf(text);
			else
				return Integer.valueOf(text);
		}
		catch(NumberFormatException e) { /* Keine Zahl */ }
		
		return text;
	}
	
	private String getCommandListKey()
	{
		CommandList list = keyViewer.getSelectedElement();
		return (list != null) ? list.getKey() : null;
	}
	
	private class EditorHandler implements Listener
	{
		@Override
		public void handleEvent(org.eclipse.swt.widgets.Event event)
		{
			if(!update) context.fireDataChanged();
		}
	}
}
