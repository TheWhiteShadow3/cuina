package cuina.editor.eventx.internal;

import cuina.database.DataTable;
import cuina.database.Database;
import cuina.database.ui.DatabaseComboViewer;
import cuina.editor.core.CuinaProject;
import cuina.editor.event.EventRegistry;
import cuina.editor.event.IEventDescriptor;
import cuina.editor.event.ITriggerEditor;
import cuina.editor.ui.DefaultComboViewer;
import cuina.editor.ui.WidgetFactory;
import cuina.event.Trigger;
import cuina.eventx.CommandList;
import cuina.eventx.InterpreterTrigger;
import cuina.resource.ResourceException;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class EventTriggerEditor implements ITriggerEditor
{
	private CuinaProject project;
	private InterpreterTrigger trigger;
	
	private DefaultComboViewer<IEventDescriptor> eventViewer;
	private Text inArg;
	private DatabaseComboViewer<CommandList> keyViewer;
	
	@Override
	public Trigger getTrigger()
	{
		trigger.setEvent(eventViewer.getSelectedElement().getEvent());
		trigger.setArgument(parseArgument(inArg.getText()));
		trigger.setKey(keyViewer.getSelectedElement().getKey());
		
		return trigger;
	}

	@Override
	public void setTrigger(Trigger trigger)
	{
		this.trigger = (InterpreterTrigger) trigger;
		
		eventViewer.setSelectedElement( EventRegistry.getEventDescriptor(this.trigger.getEvent()) );
		
		Object arg = this.trigger.getArgument();
		inArg.setText(arg != null ? arg.toString() : "");
		keyViewer.setSelection(this.trigger.getKey());
	}

	@Override
	public void init(CuinaProject project)
	{
		this.project = project;
	}

	@Override
	public void createComponents(Composite parent)
	{
		parent.setLayout(new GridLayout(2, false));
		this.eventViewer = WidgetFactory.createComboViewer(parent, "Ereignis", EventRegistry.getEventDescriptors(), false);
		this.inArg = WidgetFactory.createText(parent, "Argument");
		
		try
		{
			DataTable<CommandList> table = project.getService(Database.class).loadTable("Event");
			this.keyViewer = WidgetFactory.createDatabaseComboViewer(parent, "Command", table);
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
}
