package cuina.editor.core.engine;

import cuina.editor.core.engine.CuinaPlugin.State;
import cuina.plugin.DependencyException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginManager
{
	public static final String CUINA_PLUGINPATH_KEY = "cuina.plugin.path";
	public static final String CUINA_PLUGINLIST_KEY = "cuina.plugin.list";
	
	private final HashMap<String, File> fileList = new HashMap<String, File>();
	private final HashMap<String, CuinaPlugin> jarFiles = new HashMap<String, CuinaPlugin>();
	
	private File directory;

	/**
	 * Erstellt einen neuen PluginManager.
	 */
	public PluginManager() {}
	
	/**
	 * Sucht nach Plugins und ladet diese in den Cache.
	 * @param directory Verzeichnis für die Suche.
	 */
	public void findPlugins(File directory)
	{
		clear();
		this.directory = directory;
		
		createPluginList(directory);
		loadPluginsFromList();
	}
	
	public File getDirectory()
	{
		return directory;
	}
	
	public void clear()
	{
		directory = null;
		fileList.clear();
		jarFiles.clear();
	}

	/**
	 * Gibt eine Liste aller geladenen Plugins zurück.
	 * @return Die geladenen Plugins.
	 */
	public Map<String, CuinaPlugin> getPluginFiles()
	{
		return jarFiles;
	}

	public List<CuinaPlugin> getAviableDependencies(CuinaPlugin plugin)
	{
		Map<String, String> dependencies = plugin.getDependencies();
		List<CuinaPlugin> list = new ArrayList<CuinaPlugin>();
		
		for (String name : dependencies.keySet())
		{
			CuinaPlugin depentant = jarFiles.get(name);
			if (checkVersion(depentant.getVersion(), plugin.getVersion()))
				list.add(depentant);
		}
		return list;
	}
	
	private void createPluginList(File rootFile)
	{
		File[] files = rootFile.listFiles();
		if (files == null) return;
		for(File file : files)
		{
			String name = file.getName();
			if (name.endsWith(".jar"))
			{
				fileList.put(name, file);
			}
			else if (file.isDirectory())
			{
				createPluginList(file);
			}
		}
	}
	
	private void loadPluginsFromList()
	{
		for (String name : fileList.keySet())
		{
			try
			{
				loadPlugin(name, null);
			}
			catch (IOException | DependencyException e)
			{
				jarFiles.get(name).state = State.MISSING_DEPENDENCY;
				e.printStackTrace();
			}
		}
	}
	
	private void loadPlugin(String name, String minVersion) throws IOException, DependencyException
	{
		CuinaPlugin plugin = jarFiles.get(name);
		if (plugin != null)
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
			return;
		}
		
		File file = fileList.get(name);
		if (file == null || !file.exists()) throw new FileNotFoundException(name);
		plugin = new CuinaPlugin(file);
		jarFiles.put(file.getName(), plugin);
		
		try
		{
			if (!checkVersion(plugin.getVersion(), minVersion))
				throw new DependencyException(name);
		}
		catch (NumberFormatException e)
		{
			throw new DependencyException(name, e);
		}
		loadDependencies(plugin);
		try
		{
			plugin.load();
			plugin.state = State.LOADED;
//			String[] classNames = plugin.getClassNames();
//			for(String str : classNames)
//			{
//				Class c = classLoader.loadClass(str);
//				if (isPluginClass(c)) classes.add(c);
//			}
		}
		catch (ClassNotFoundException e)
		{
			throw new DependencyException(name, e);
		}
	}
	
	private void loadDependencies(CuinaPlugin plugin) throws IOException, DependencyException
	{
		Map<String, String> dependencies = plugin.getDependencies();
		for (String name : dependencies.keySet())
		{
			loadPlugin(name, dependencies.get(name));
		}
	}
	
	public static boolean checkVersion(String cur, String min)
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
//	
//	private boolean isPluginClass(Class<?> cl)
//	{
//		if (cl == null || cl == Object.class) return false;
//		
//		Class<?>[] infs = cl.getInterfaces();
//		for(Class<?> inf : infs)
//		{
//			if (inf == Plugin.class) return true;
//		}
//		return isPluginClass(cl.getSuperclass());
//	}
}