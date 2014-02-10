package cuina.editor.core.engine;


import cuina.editor.core.CuinaCore;
import cuina.editor.core.CuinaProject;
import cuina.editor.core.internal.Util;
import cuina.editor.core.internal.engine.EngineClassLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.variables.VariablesPlugin;

/**
 * Stellt eine Referenz zur Cuina-Engine da, die vom Projekt benutzt wird.
 * @author TheWhiteShadow
 */
public class EngineReference
{
	/** Name der System-Variable zum Cuina-Engine Homeverzeichnis */
	public static final String CUINA_SYSTEM_VARIABLE = "CUINA_HOME";
	
	private static final String ENGINE_JAR = "cuina.engine.jar";
	private static final String ENGINE_KEY = "cuina.engine.path";
	private static final String PLUGIN_KEY = "cuina.plugin.path";

	private CuinaProject project;
	private IEclipsePreferences prefs;
	private EngineClassLoader classLoader;
	private PluginManager pluginManager;
	
	private Path projectPath;

	public EngineReference(CuinaProject project) throws CoreException
	{
		try
		{
			this.project = project;
			this.prefs = Util.getProjectPreference(project);
			/*
			 * Preferenzen werden nur geladen, wenn der Workspace aktualisiert wurde, nach dem die Preferenz-Datei angelegt wurde.
			 */
			this.projectPath = Paths.get(getProject().getProject().getLocation().toString());
			this.classLoader = new EngineClassLoader(this);
		}
		catch (Exception e)
		{
			new CoreException(new Status(IStatus.ERROR, CuinaCore.PLUGIN_ID, e.getMessage(), e));
		}
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
		return resolve(prefs.get(ENGINE_KEY, "${env_var:CUINA_HOME}"));
    }

	/**
	 * Gibt den Pfad zu den Plugins zurück, die vom Projekt benutzt werden.
	 * @return Pfad zu den Plugins.
	 */
	public String getPluginPath()
	{
    	return resolve(prefs.get(PLUGIN_KEY, "plugins"));
	}
	
	private String resolve(String str)
	{
		try
		{
			return VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(str);
		}
		catch (CoreException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Gibt den Pfad zur Engine zurück, wenn er gefunden wurde.
	 * <p>
	 * Die Implementation sucht zuerst im in den Projekt-Eigenschaften angegebenen Pfad.
	 * Wenn keine Eigenschaft angegeben ist wird die Umgebungsvariable CUINA_HOME genommen.
	 * Ist der Pfad relativ, wird er zum Projekt aufgelöst.
	 * Bei einem Verzeichnis wird der Pfad um <i>cuina.engine.jar</i> erweitert.
	 * Existiert die Datei nicht wird eine <code>FileNotFoundException</code> geworfen.
	 * </p>
	 * @return Den Pfad zur Engine. Niemals <code>null</code>.
	 * @throws FileNotFoundException, wenn der Pfad nicht gefunden wurde.
	 */
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
			path = path.resolve(ENGINE_JAR);
			if (Files.notExists(path))
				throw new FileNotFoundException(path.toString());
		}
    	return path;
    }
	
	/**
	 * Gibt das Plugin-Verzeichniss zurück, wenn Eins gefunden wurde.
	 * <p>
	 * Die Implementation sucht zuerst im in den Projekt-Eigenschaften angegebenen Pfad.
	 * Wenn keine Eigenschaft angegeben ist wird der Pfad <i>plugins</i> genommen.
	 * Ist der Pfad relativ, wird es zum Projekt aufgelöst.
	 * Existiert der Pfad nicht, wird das Verzeichnis zur Engine aufgelöst.
	 * Existiert er auch nicht wird eine <code>FileNotFoundException</code> geworfen.
	 * </p>
	 * @return Das Plugin-Verzeichniss. Niemals <code>null</code>.
	 * @throws FileNotFoundException, wenn kein Verzeichnis gefunden wurde.
	 */
	public Path resolvePluginPath() throws FileNotFoundException
    {
		Path path = Paths.get(getPluginPath());
		if (!path.isAbsolute())
		{
			Path absolutePath = projectPath.resolve(path);
			if (Files.exists(absolutePath)) return absolutePath;
			
			path = resolveEnginePath().getParent().resolve(path);
		}
		if (Files.notExists(path)) throw new FileNotFoundException(path.toString());
		
		return path;
    }
	
//	private String getDefaultPath()
//	{
//		String value = System.getenv(CUINA_SYSTEM_VARIABLE);
//		return (value == null) ? "." : value;
//	}
	
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
			
			pluginManager.clear();
			pluginManager.findPlugins(file);
		}
	}
}