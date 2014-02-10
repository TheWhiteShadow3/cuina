package cuina.editor.model;

import org.eclipse.core.runtime.IConfigurationElement;

import cuina.database.NamedItem;

public class AnimatorType implements NamedItem
{
	private String name;
	private String description;
	private String className;
	
	AnimatorType(IConfigurationElement conf)
	{
		this.name = conf.getAttribute("name");
		this.description = conf.getAttribute("description");
		this.className = conf.getAttribute("class");
	}

	@Override
	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
	}

	public String getClassName()
	{
		return className;
	}
}
