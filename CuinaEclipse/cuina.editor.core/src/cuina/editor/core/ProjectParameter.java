package cuina.editor.core;

import java.util.Objects;

import org.eclipse.core.runtime.IConfigurationElement;

public final class ProjectParameter implements Comparable<ProjectParameter>
{
	public static final String PROJECT_PARAMETER_ID = "cuina.editor.project.parameter";
	
	private final String group;
	private final String name;
	private final String description;
	private final String defaultValue;
	private final String after;

	ProjectParameter(IConfigurationElement conf)
	{
		this.name = conf.getAttribute("name");
		if (name == null) throw new NullPointerException("name is null");
		this.defaultValue = conf.getAttribute("defaultValue");
		if (name == null) throw new NullPointerException("defaultValue is null");
		this.group = conf.getAttribute("group");
		this.description = conf.getAttribute("description");
		this.after = conf.getAttribute("after");
	}

	public String getGroup()
	{
		return group;
	}

	public String getName()
	{
		return name;
	}
	
	String getKey()
	{
		return (group == null ? "" : group) + name;
	}

	public String getDescription()
	{
		return description;
	}

	public String getDefaultValue()
	{
		return defaultValue;
	}
	
	/**
	 * Gibt den Parameter-Wert für das angegebene Projekt zurück.
	 * @param project Das Projekt.
	 * @return Der Wert des Parameters.
	 */
	public String getValue(CuinaProject project)
	{
		String value = project.getIni().get(group, name);
		if (value == null)
			return defaultValue;
		else
			return value;
	}
	
	/**
	 * Setzt den Parameter-Wert für das angegebene Projekt.
	 * @param project Das Projekt.
	 * @param value Wert des Parameters.
	 */
	public void setValue(CuinaProject project, String value)
	{
		project.getIni().set(group, name, value);
	}
	
	/**
	 * Setzt den default Parameter-Wert für das angegebene Projekt.
	 * @param project Das Projekt.
	 */
	public void setDefaultValue(CuinaProject project)
	{
		project.getIni().set(group, name, defaultValue);
	}

	@Override
	public int hashCode()
	{
		return 491 * getKey().hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ProjectParameter other = (ProjectParameter) obj;
		if (!Objects.equals(getKey(), other.getKey())) return false;
		return true;
	}

	@Override
	public int compareTo(ProjectParameter o)
	{
		if (name.equals(o.after)) return -1;
		if (o.name.equals(after)) return +1;
		
		return 0;
	}
}
