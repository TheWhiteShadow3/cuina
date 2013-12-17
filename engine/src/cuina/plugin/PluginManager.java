/*
 * Cuina Engine
 * Copyright (C) 2011 - 2012 by Cuina Team (http://www.cuina.byethost12.com/)
 *
 * see license.txt for more info
 */

package cuina.plugin;

import cuina.Logger;
import cuina.util.CuinaClassLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
	 * @param file Verzeichnis für die Suche.
	 */
	public static void findPlugins(File file)
	{
		PluginManager.pluginDirectory = file;
		Logger.log(PluginManager.class, Logger.DEBUG, "Plugindirectory: " + file.getAbsolutePath());
		
		List<String> plugins = null;
		String pluginListString = System.getProperty(CUINA_PLUGINLIST_KEY);
		if (pluginListString != null)
		{
			plugins = Arrays.asList(pluginListString.split(";"));
		}
		createPluginList(PluginManager.pluginDirectory, plugins);
		loadPluginsFromList();
		
		Logger.log(PluginManager.class, Logger.INFO, jarFiles.size() + " plugins loaded.");
	}
	
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
	 * @return Die geladenen Plugins.
	 */
	public static HashMap<String, CuinaPlugin> getPluginFiles()
	{
		return jarFiles;
	}

	/**
	 * Gibt eine Liste der Klassen zurück, die das Plugin-Interface implementieren.
	 * @return Plugin-Klassen.
	 */
	public static ArrayList<Class<?>> getPluginsClasses()
	{
		return classes;
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
		if (file == null || !file.exists()) throw new FileNotFoundException(name);
		
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
		loadDependencies(plugin);
		try
		{
			plugin.load();
			String[] classNames = plugin.getClassNames();
			for(String str : classNames)
			{
				Class c = CuinaClassLoader.getInstance().loadClass(str);
				if (isPluginClass(c)) classes.add(c);
			}
		}
		catch (ClassNotFoundException e)
		{
			throw new DependencyException(name, e);
		}
	}
	
	private static void loadDependencies(CuinaPlugin plugin) throws IOException, DependencyException
	{
		Map<String, String> dependencies = plugin.getDependencies();
		for (String name : dependencies.keySet())
		{
			loadPlugin(name, dependencies.get(name));
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