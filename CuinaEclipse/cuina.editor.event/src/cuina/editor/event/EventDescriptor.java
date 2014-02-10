package cuina.editor.event;

import cuina.event.Event;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

public class EventDescriptor implements IEventDescriptor
{
	private String id;
	private String description;
	private Image image;
	
	EventDescriptor(IConfigurationElement conf)
	{
		this.id = conf.getAttribute("id");
		this.description = conf.getAttribute("description");
		String imagePath = conf.getAttribute("image");
		if (imagePath != null) try 
		{
			Bundle plugin = Platform.getBundle(conf.getContributor().getName());
			this.image = new Image(Display.getDefault(), FileLocator.resolve(plugin.getEntry(imagePath)).getPath());
		} 
		catch(Exception e) { e.printStackTrace(); }
	}

	@Override
	public String getID()
	{
		return id;
	}

	@Override
	public String getDescription()
	{
		return description;
	}
	
	@Override
	public Event getEvent()
	{
		return Event.getEvent(id);
	}
	
	@Override
	public Image getImage()
	{
		return image;
	}

	@Override
	public String getName()
	{
		return id;
	}
}
