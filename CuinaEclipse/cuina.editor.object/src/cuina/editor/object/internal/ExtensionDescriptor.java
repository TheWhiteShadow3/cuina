package cuina.editor.object.internal;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class ExtensionDescriptor
{
	private final String name;
	private final Class<?> editor;
	
	public ExtensionDescriptor(IConfigurationElement conf) throws Exception
	{
		this.name = conf.getAttribute("name");
		Bundle plugin = Platform.getBundle(conf.getContributor().getName());
		this.editor = plugin.loadClass(conf.getAttribute("editorClass"));
	}

	public String getName()
	{
		return name;
	}

	public Class<?> getEditor()
	{
		return editor;
	}
}
