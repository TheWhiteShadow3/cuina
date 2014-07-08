package cuina.database;


import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

public class DatabaseDescriptor<E extends DatabaseObject> implements IDatabaseDescriptor<E>
{
	private String name;
	private Class<E> dataClass;
	private Image image;
	private IConfigurationElement configuration;
	private String editorID;
	private DatabaseObjectValidator validator;
	public IConfigurationElement conf;
	
	public DatabaseDescriptor(String name, Class<E> dataClass)
	{
		this.name = name;
		this.dataClass = dataClass;
	}
	
	DatabaseDescriptor(IConfigurationElement conf) throws Exception
	{
		this.conf = conf;
		Bundle plugin = Platform.getBundle(conf.getContributor().getName());
		
		this.configuration = conf;
		this.dataClass = (Class<E>) plugin.loadClass(conf.getAttribute("class"));
		
		this.name = conf.getAttribute("name");
		if (name == null) name = dataClass.getSimpleName();
		
		String imagePath = conf.getAttribute("image");
		if (imagePath != null) try
		{
			this.image = new Image(Display.getDefault(), plugin.getEntry(imagePath).openStream());
		}
		catch(Exception e) { e.printStackTrace(); }

		this.editorID = conf.getAttribute("editorID");
		
		IConfigurationElement[] children = conf.getChildren("validator");
		if (children.length == 1)
		{
			this.validator = (DatabaseObjectValidator) children[0].createExecutableExtension("class");
		}
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public IConfigurationElement getConfiguration()
	{
		return configuration;
	}
	
	@Override
	public Class<E> getDataClass()
	{
		return dataClass;
	}
	
	@Override
	public Image getImage()
	{
		return image;
	}
	
	public void setImage(Image image)
	{
		this.image = image;
	}

	@Override
	public String getEditorID()
	{
		return editorID;
	}

	public void setEditorID(String editorID)
	{
		this.editorID = editorID;
	}

	@Override
	public DatabaseObjectValidator getValidator()
	{
		return validator;
	}
}
