package cuina.editor.core;


import java.io.FileNotFoundException;
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

//	private CuinaProject project;
	private IEclipsePreferences prefs;

	public EngineReference(CuinaProject project) throws FileNotFoundException
	{
//		this.project = project;
		this.prefs = new ProjectScope(project.getProject()).getNode("cuina.editor.core");
		
		validateEnginePath();
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
    	return prefs.get(PLUGIN_KEY, getDefaultPath() + "/plugins");
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
	 * wird im Verzeichnis der System-Variablen CUINA_HOME das Verzeichnis plugin gesucht.
	 * @param path Pfad zu den Plugins.
	 */
	public void setPluginPath(String path)
	{
		prefs.put(PLUGIN_KEY, path);
	}
	
	private void validateEnginePath() throws FileNotFoundException
	{
		Path path = Paths.get(getEnginePath());
		if (Files.notExists(path))
			throw new FileNotFoundException(path.toString());
		
		if ( !path.toString().endsWith(".jar") && Files.notExists(path.resolve("cuina.engine.jar")) )
		{
			throw new FileNotFoundException(path.toString());
		}
	}
}