package cuina.editor.object.internal;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import cuina.database.NamedItem;

public class ExtensionDescriptor implements NamedItem
{
	private final String id;
	private final String name;
	private final String dataClassName;
	private final Class<?> editor;
	
	public ExtensionDescriptor(IConfigurationElement conf) throws Exception
	{
		this.id = conf.getAttribute("id");
		this.name = conf.getAttribute("name");
		this.dataClassName = conf.getAttribute("dataClass");
		
		Bundle plugin = Platform.getBundle(conf.getContributor().getName());
		this.editor = plugin.loadClass(conf.getAttribute("editorClass"));
	}

	public String getID()
	{
		return id;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public String getDataClassName()
	{
		return dataClassName;
	}

	public Class<?> getEditor()
	{
		return editor;
	}
}
