package cuina.editor.core.engine;


import cuina.editor.core.CuinaProject;
import cuina.editor.core.internal.engine.EngineClassLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * Stellt eine Referenz zur Cuina-Engine da, die vom Projekt benutzt wird.
 * @author TheWhiteShadow
 */
public class EngineReference
{
	/** Name der System-Variable zum Cuina-Engine Homeverzeichnis */
	public static final String CUINA_SYSTEM_VARIABLE = "CUINA_HOME";
	
	private static final String ENGINE_KEY = "cuina.engine.path";
	private static final String PLUGIN_KEY = "cuina.plugin.path";

	private CuinaProject project;
	private IEclipsePreferences prefs;
	private EngineClassLoader classLoader;
	private PluginManager pluginManager;
	
	private Path projectPath;

	public EngineReference(CuinaProject project) throws IOException
	{
		this.project = project;
		this.prefs = new ProjectScope(project.getProject()).getNode("cuina.editor.core");
		this.projectPath = Paths.get(getProject().getProject().getLocation().toString());
		this.classLoader = new EngineClassLoader(this);
	}
	
	public CuinaProject getProject()
	{
		return project;
	}

	public ClassLoader getClassLoader()
	{
		return classLoader;
	}

	public PluginManager getPluginManager()
	{
		if (pluginManager == null)
		{
			pluginManager = new PluginManager();
			pluginManager.findPlugins(new File(getPluginPath()));
		}
		return pluginManager;
	}

	/**
	 * Gibt den Pfad zur Engine zurück, die vom Projekt benutzt wird.
	 * @return Pfad zur Engine.
	 */
	public String getEnginePath()
    {
    	return prefs.get(ENGINE_KEY, getDefaultPath());
    }

	/**
	 * Gibt den Pfad zu den Plugins zurück, die vom Projekt benutzt werden.
	 * @return Pfad zu den Plugins.
	 */
	public String getPluginPath()
	{
    	return prefs.get(PLUGIN_KEY, "plugins");
	}
	
	public Path resolveEnginePath() throws FileNotFoundException
    {
		Path path = Paths.get(getEnginePath());
		if (!path.isAbsolute())
		{
			path = projectPath.resolve(path);
		}
		if (Files.notExists(path))
			throw new FileNotFoundException(path.toString());
		
		if (Files.isDirectory(path))
		{
			path = path.resolve("cuina.engine.jar");
			if (Files.notExists(path))
				throw new FileNotFoundException(path.toString());
		}
    	return path;
    }
	
	public Path resolvePluginPath()
    {
		Path path = Paths.get(getPluginPath());
		if (!path.isAbsolute())
		{
			path = projectPath.resolve(path);
		}
		if (Files.notExists(path)) return null;
		
    	return path;
    }
	
	private String getDefaultPath()
	{
		String value = System.getenv(CUINA_SYSTEM_VARIABLE);
		return (value == null) ? "." : value;
	}
	
	/**
	 * Setzt den Pfad zur Engine. Wenn der Pfad <code>null</code> ist,
	 * wird im Verzeichnis der System-Variablen CUINA_HOME gesucht.
	 * @param path Pfad zur Engine.
	 */
	public void setEnginePath(String path)
	{
		prefs.put(ENGINE_KEY, path);
	}
	
	/**
	 * Setzt den Pfad zu den Plugins. Wenn der Pfad <code>null</code> ist,
	 * wird im Verzeichnis des Projekts das Verzeichnis <i>plugins</i> gesucht.
	 * @param path Pfad zu den Plugins.
	 */
	public void setPluginPath(String path)
	{
		prefs.put(PLUGIN_KEY, path);
		if (pluginManager != null)
		{
			File file = new File(getPluginPath());
			if (file.equals(pluginManager.getDirectory())) return;
			pluginManager.findPlugins(file);
		}
	}
}