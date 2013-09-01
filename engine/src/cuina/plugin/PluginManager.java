/*
 * Cuina Engine
 * Copyright (C) 2011 - 2012 by Cuina Team (http://www.cuina.byethost12.com/)
 *
 * see license.txt for more info
 */

package cuina.plugin;

import cuina.Game;
import cuina.Logger;
import cuina.util.CuinaClassLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ladet die Plugins für den Editor oder die Engine.
 * <p>
 * Plugins müssen innerhalb des Pluginverzeichnisses als Jar-Datei vorliegen.
 * Neben normalen Javaklassen können Plugins folgende Komponentne enthalten:
 * <p>
 * <ul>
 * 	<li>Pluginklassen (*.class)</li>
 * 	<li>Funktions-Bibliothek (Abhängig von der benutzten Skriptsprache. Default JRuby: *.rb)</li>
 * 	<li>Funktionsbeschreibung (*.xml)</li>
 * </ul>
 * Wenn ein Manifesteintrag vorhanden ist, wird dieser bevorzugt verwendet.
 * Ansonsten wird die ganze Jardatei nach den entsprechenden Komponentne durchsucht.
 * Pro Plugin ist nur eine Biliothek und eine Funktionsbeschreibung erlaubt.
 * </p>
 * <p>
 * Einträge in der Manifest:
 * <table border="1" summary="Manifest-Einträge">
 * <tr>
 * 		<th>Name</th>
 * 		<th>Beschreibung</th>
 * </tr>
 * <tr>
 * 		<td>Plugin-Classes</td>
 * 		<td>Liste von Javaklassen, die das Interface {@link cuina.plugin.Plugin} implementieren. Trennzeichen: ';'</td>
 * </tr>
 * <tr>
 * 		<td>Plugin-Version</td>
 * 		<td>Versions-Nummer des Plugins.</td>
 * </tr>
 * <tr>
 * 		<td>Plugin-Dependency</td>
 * 		<td>Liste von Abhängigkeit zu anderen Plugins.
 * Die Einträge müssen dem Muster folgen: <code>&lt;NAME&gt;-&lt;VERSION&gt;</code>  Trennzeichen: ';'</td>
 * </tr>
 * <tr>
 * 		<td>Script-Library</td>
 * 		<td>Bibliothek an Funktionen, welche für Skripte benutzt werden können um mit dem Plugin zu interagieren.</td>
 * </tr>
 * <tr>
 * 		<td>Script-Description</td>
 * 		<td>Beschreibung der Funktionen in der Bibliothek um diese in den Editor zu integrieren.</td>
 * </tr>
 * </table>
 * </p>
 * @author TheWhiteShadow
 * @see cuina.plugin.Plugin
 */
public class PluginManager
{
	public static final String CUINA_PLUGINPATH_KEY = "cuina.plugin.path";
	public static final String CUINA_PLUGINLIST_KEY = "cuina.plugin.list";
	
	private static final HashMap<String, File> fileList = new HashMap<String, File>();
	private static final ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
	private static final HashMap<String, CuinaPlugin> jarFiles = new HashMap<String, CuinaPlugin>();
	
	private static File pluginDirectory;
	
	private PluginManager() {}
	
	/**
	 * Sucht nach Plugins und ladet diese in den Cache.
	 * Wenn die Systemproperty "cuina.plugin.path" angegeben wurde, wird diese standardmäßig benutzt.
	 * Nur wenn diese <code>null</code> ist oder ein leerer String, wird das angegebene Verzeichnis benutzt.
	 * @param file Alternatives Verzeichnis für die Suche,
	 * wenn "cuina.plugin.path" <code>null</code> oder ein leerer String ist.
	 */
	public static void findPlugins(File file)
	{
		PluginManager.pluginDirectory = file;
		Logger.log(PluginManager.class, Logger.DEBUG, "Plugindirectory: " + file.getAbsolutePath());
		
		List<String> plugins = null;
		String pluginListString = Game.getProperty(CUINA_PLUGINLIST_KEY, null);
		if (pluginListString != null)
		{
			plugins = Arrays.asList(pluginListString.split(";"));
		}
		createPluginList(PluginManager.pluginDirectory, plugins);
		loadPluginsFromList();
		
		Logger.log(PluginManager.class, Logger.INFO, jarFiles.size() + " plugins loaded.");
//		String paths = "";
//		for(File f : jarFiles)
//		{
//			paths += ";" + f.toString();
//		}
//		
//		System.setProperty("java.class.path", System.getProperty("java.class.path") + paths);
//		System.out.println(System.getProperty("java.class.path"));
	}
	
	/**
	 * Löscht alle geladenen Plugins aus dem Cache.
	 * <p>
	 * Nach dem Aufruf gibt {@link #getPluginFiles()} und {@link #getPluginsClasses()} eine leere Liste zurück.
	 * </p>
	 */
	public static void clear()
	{
		classes.clear();
		jarFiles.clear();
	}
	
	public static File getPluginDirectory()
	{
		return pluginDirectory;
	}

	/**
	 * Gibt eine Liste aller Plugins zurück.
	 * @return Unveränderbare Liste aller geladenen Plugins. 
	 */
	public static Map<String, CuinaPlugin> getPluginFiles()
	{
		return Collections.<String, CuinaPlugin>unmodifiableMap(jarFiles);
	}

	/**
	 * Gibt eine Liste der Klassen zurück, die das Plugin-Interface implementieren.
	 * @return Plugin-Klassen.
	 */
	public static List<Class<?>> getPluginsClasses()
	{
		return Collections.<Class<?>>unmodifiableList(classes);
	}
	
	private static void createPluginList(File rootFile, List<String> list)
	{
		File[] files = rootFile.listFiles();
		if (files == null) return;
		for(File file : files)
		{
			String name = file.getName();
			if (name.endsWith(".jar"))
			{
				if (list != null && list.indexOf(name) == -1) continue;
				fileList.put(name, file);
			}
			else if (file.isDirectory())
			{
				createPluginList(file, list);
			}
		}
	}
	
	private static void loadPluginsFromList()
	{
		for (String name : fileList.keySet())
		{
			Logger.log(PluginManager.class, Logger.DEBUG, "load plugin: " + name);
			try
			{
				loadPlugin(name, null);
			}
			catch (IOException | DependencyException e)
			{
				jarFiles.remove(name);
				Logger.log(PluginManager.class, Logger.ERROR, e);
			}
		}
	}
	
	private static void loadPlugin(String name, String minVersion)
			throws IOException, DependencyException
	{
		CuinaPlugin plugin = jarFiles.get(name);
		if (plugin != null)
		{
			if (!checkVersion(plugin.getVersion(), minVersion))
				throw new DependencyException(name);
			return;
		}
		
		File file = fileList.get(name);
		if (file == null) throw new FileNotFoundException(name);
		
		CuinaClassLoader.getInstance().addURL(file.toURI().toURL());
		plugin = new CuinaPlugin(file);
		jarFiles.put(file.getName(), plugin);
		if (minVersion != null)
		{
			try
			{
				if (!checkVersion(plugin.getVersion(), minVersion))
					throw new DependencyException(name);
			}
			catch (NumberFormatException e)
			{
				throw new DependencyException(name, e);
			}
		}
		checkDependencies(plugin);
		try
		{
			plugin.load();
			if (plugin.classList != null)
			{
				for(Class c : plugin.classList)
					if (isPluginClass(c)) addClass(c);
			}
		}
		catch (ClassNotFoundException e)
		{
			throw new DependencyException(name, e);
		}
	}
	
	private static void checkDependencies(CuinaPlugin plugin) throws IOException, DependencyException
	{
		String dependencies = plugin.dependencies;
		if (dependencies != null && !dependencies.isEmpty())
		{
			String[] plugins = dependencies.split("[;-]");
			if (plugins.length % 2 != 0)
			{
				Logger.log(PluginManager.class, Logger.WARNING,
						"Invalid attribut-format in Plugin: " + plugin.getJar().getName());
				return;
			}
			
			for(int i = 0; i < plugins.length; i+=2)
			{
				String name = plugins[i];
				String ver = plugins[i+1];
				
				if (!name.endsWith(".jar")) name = name + ".jar";
				loadPlugin(name, ver);
			}
		}
	}
	
	private static boolean checkVersion(String cur, String min)
	{
		if (min == null) return true;
		if (cur == null) return false;
		
		String[] n1 = cur.split("\\.");
		String[] n2 = min.split("\\.");
		int minLenght = Math.min(n1.length, n2.length);
		if (minLenght == 0) return false;
		
		int v1, v2;
		for (int i = 0; i < minLenght; i++)
		{
			v1 = Integer.parseInt(n1[i]);
			v2 = Integer.parseInt(n2[i]);
			if (v1 < v2) return false;
		}
		return true;
	}
	
	static void addClass(Class<?> cl)
	{
		classes.add(cl);
		Logger.log(PluginManager.class, Logger.DEBUG, "load plugin-class: " + cl.getName());
	}
	
	static boolean isPluginClass(Class<?> cl)
	{
		if (cl == null || cl == Object.class) return false;
		
		Class<?>[] infs = cl.getInterfaces();
		for(Class<?> inf : infs)
		{
			if (inf == Plugin.class) return true;
		}
		return isPluginClass(cl.getSuperclass());
	}
}
