package cuina.database;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

public class DatabaseDescriptor<E extends DatabaseObject>
{
	private String name;
	private Class<E> dataClass;
	private Image image;
	private Class editorClass;
	private Class contentProviderClass;
	private IConfigurationElement configuration;
	
	public DatabaseDescriptor(String name, Class<E> dataClass)
	{
		this.name = name;
		this.dataClass = dataClass;
	}
	
	DatabaseDescriptor(IConfigurationElement conf) throws Exception
	{
		Bundle plugin = Platform.getBundle(conf.getContributor().getName());
		
		this.configuration = conf;
		this.dataClass = (Class<E>) plugin.loadClass(conf.getAttribute("class"));
		
		this.name = conf.getAttribute("name");
		if (name == null) name = dataClass.getSimpleName();
		String editorAttribut = conf.getAttribute("editor");
		if (editorAttribut != null) this.editorClass = plugin.loadClass(editorAttribut);
		
		String imagePath = conf.getAttribute("image");
		if (imagePath != null)
		try {
			this.image = new Image(Display.getDefault(), FileLocator.resolve(plugin.getEntry(imagePath)).getPath());
		} catch(Exception e) { e.printStackTrace(); }
		
		IConfigurationElement[] childConf = conf.getChildren("TreeContentProvider");
		if (childConf.length == 1)
		{
			this.contentProviderClass = plugin.loadClass(childConf[0].getAttribute("class"));
		}
	}
	
	public String getName()
	{
		return name;
	}
	
	public IConfigurationElement getConfiguration()
	{
		return configuration;
	}
	
	public Class<E> getDataClass()
	{
		return dataClass;
	}
	
	public Image getImage()
	{
		return image;
	}
	
	public void setImage(Image image)
	{
		this.image = image;
	}

	public void setEditorClass(Class editorClass)
	{
		this.editorClass = editorClass;
	}

	public Class getEditorClass()
	{
		return editorClass;
	}

	public Class getContentProviderClass()
	{
		return contentProviderClass;
	}
}
