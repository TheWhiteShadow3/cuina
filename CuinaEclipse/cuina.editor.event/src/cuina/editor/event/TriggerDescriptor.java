package cuina.editor.event;

import cuina.event.Trigger;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

public class TriggerDescriptor implements ITriggerDescriptor
{
	private String name;
	private Class triggerClass;
	private Class editorClass;
	private String description;
	private Image image;
	
	TriggerDescriptor(IConfigurationElement conf)
	{
		Bundle plugin = Platform.getBundle(conf.getContributor().getName());
		
		this.name = conf.getAttribute("name");
		this.description = conf.getAttribute("description");
		try
		{
			this.triggerClass = plugin.loadClass(conf.getAttribute("class"));
			this.editorClass = plugin.loadClass(conf.getAttribute("editorClass"));
		}
		catch (ClassNotFoundException | InvalidRegistryObjectException e1)
		{
			e1.printStackTrace();
		}
		
		String imagePath = conf.getAttribute("image");
		if (imagePath != null) try 
		{
			this.image = new Image(Display.getDefault(), FileLocator.resolve(plugin.getEntry(imagePath)).getPath());
		} 
		catch(Exception e) { e.printStackTrace(); }
	}
	
	@Override
	public Class<? extends Trigger> getTriggerClass()
	{
		return triggerClass;
	}
	
	@Override
	public Class<? extends ITriggerEditor> getEditorClass()
	{
		return editorClass;
	}

	@Override
	public String getDescription()
	{
		return description;
	}

	@Override
	public Image getImage()
	{
		return image;
	}

	@Override
	public String getName()
	{
		return name;
	}
}
