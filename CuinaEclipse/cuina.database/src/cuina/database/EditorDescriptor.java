package cuina.database;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class EditorDescriptor
{
	private Class editorClass;
	private Class toolboxClass;
	
	public EditorDescriptor(IConfigurationElement conf) throws ClassNotFoundException
	{
		Bundle plugin = Platform.getBundle(conf.getContributor().getName());
		
		String editorAttribut = conf.getAttribute("class");
		if (editorAttribut != null) this.editorClass = plugin.loadClass(editorAttribut);

		String toolboxAttribut = conf.getAttribute("toolbox");
		if (toolboxAttribut != null) this.toolboxClass = plugin.loadClass(toolboxAttribut);
	}

	public EditorDescriptor(Class editorClass, Class toolboxClass)
	{
		this.editorClass = editorClass;
		this.toolboxClass = toolboxClass;
	}

	public void setEditorClass(Class editorClass)
	{
		this.editorClass = editorClass;
	}

	public Class getEditorClass()
	{
		return editorClass;
	}

	public Class getToolboxClass()
	{
		return toolboxClass;
	}

	public void setToolboxClass(Class toolboxClass)
	{
		this.toolboxClass = toolboxClass;
	}
}
