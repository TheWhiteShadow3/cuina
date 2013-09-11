package cuina.plugin;

public class DependencyException extends Exception
{
	private static final long serialVersionUID = 1L;

	public DependencyException(String name)
	{
		this(name, null);
	}

	public DependencyException(String name, Throwable cause)
	{
		super("Plugin-Dependency not fullfiled. Plugin " + name + " could not be loaded.", cause);
	}
}