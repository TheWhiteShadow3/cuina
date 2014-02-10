package cuina.editor.eventx.internal;

import cuina.database.DataTable;
import cuina.database.Database;
import cuina.database.ui.properties.DataReferencePropertyDescriptor;
import cuina.editor.core.CuinaProject;
import cuina.editor.event.EventPropertyDescriptor;
import cuina.editor.ui.BeanPropertyDescriptor;
import cuina.event.Event;
import cuina.eventx.CommandList;
import cuina.eventx.InterpreterTrigger;
import cuina.resource.ResourceException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

public class EventTriggerPropertySource implements IPropertySource
{
	private CuinaProject project;
	private InterpreterTrigger trigger;
	private IPropertyDescriptor[] descriptors;
	
	public EventTriggerPropertySource(CuinaProject project, InterpreterTrigger trigger)
	{
		this.project = project;
		this.trigger = trigger;
	}

	@Override
	public Object getEditableValue()
	{
		return trigger;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		if (descriptors != null) return descriptors;
		
		descriptors = new IPropertyDescriptor[4];
		descriptors[0] = new EventPropertyDescriptor("event.id", "Event");
		
		try
		{
			DataTable<CommandList>  table = project.getService(Database.class).loadTable("Event");
			descriptors[1] = new DataReferencePropertyDescriptor("eventx.key", table, "Event");
		}
		catch (ResourceException e1)
		{
			e1.printStackTrace();
		}
		try
		{
			descriptors[2] = new BeanPropertyDescriptor(InterpreterTrigger.class, "eventArg", "Argument");
			descriptors[3] = new BeanPropertyDescriptor(InterpreterTrigger.class, "active", "Aktiv");
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			Assert.isTrue(false);
		}
		return descriptors;
	}

	@Override
	public Object getPropertyValue(Object id)
	{
		switch((String) id)
		{
			case "event.id": return trigger.getEvent();
			case "eventx.key": return trigger.getKey();
			case "eventArg": return trigger.getArgument();
			case "active": return trigger.isActive();
		}
		return null;
	}
	
	@Override
	public void setPropertyValue(Object id, Object value)
	{
		switch((String) id)
		{
			case "event.id": trigger.setEvent((Event) value); break;
			case "eventx.key": trigger.setKey((String) value); break;
			case "eventArg": trigger.setArgument(value); break;
			case "active": trigger.setActive((boolean) value); break;
		}
	}
	
	@Override
	public boolean isPropertySet(Object id)
	{
		return false;
	}

	@Override
	public void resetPropertyValue(Object id) {}
}
