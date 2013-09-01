package cuina.plugin;

import cuina.Logger;
import cuina.util.CuinaClassLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class CuinaPlugin
{
	private File file;
	private JarFile jar;
	private String version;
	private String pluginClass;
	private String scriptLib;
//	private String scriptDesc;
	
	String dependencies;
	Class<?>[] classList;
	
	public CuinaPlugin(File file) throws IOException
	{
		this.file = file;
		this.jar = new JarFile(file);
		scanManifest();
	}

	public String getVersion()
	{
		return version;
	}

	public JarFile getJar()
	{
		return jar;
	}

	public File getFile()
	{
		return file;
	}

	public String getScriptLib()
	{
		return scriptLib;
	}

//	public String getScriptDesc()
//	{
//		return scriptDesc;
//	}

	private void scanManifest() throws IOException
	{
		Manifest manifest = jar.getManifest();
		if (manifest != null)
		{
			Attributes att 	= manifest.getMainAttributes();
			version			= att.getValue("Plugin-Version");
			pluginClass 	= att.getValue("Plugin-Classes");
			scriptLib 		= att.getValue("Script-Library");
//			scriptDesc 		= att.getValue("Script-Description");
			dependencies	= att.getValue("Plugin-Dependency");
		}
	}

	public void load() throws IOException, ClassNotFoundException
	{
		List<String> classNames;
		if (pluginClass != null)
		{
			if (pluginClass.isEmpty()) return;
			
			classNames = Arrays.asList(pluginClass.split(";"));
		}
		else
		{
			classNames = new ArrayList<String>(8);
			Enumeration<JarEntry> entries = jar.entries();
			while(entries.hasMoreElements())
			{
				JarEntry entry = entries.nextElement();
				if (entry.getName().endsWith(".class"))
				{
					classNames.add( getBinaryClassName(entry.getName()) );
				}
				else if (entry.getName().endsWith(".rb"))
				{
					scriptLib = entry.getName();
				}
//				else if (entry.getName().endsWith(".xml"))
//				{
//					scriptDesc = entry.getName();
//				}
			}
		}
		
		classList = new Class[classNames.size()];
		for (int i = 0; i < classList.length; i++)
		{
			classList[i] = CuinaClassLoader.getInstance().loadClass(classNames.get(i));
		}
		if (scriptLib != null) 
			Logger.log(PluginManager.class, Logger.DEBUG, "lade Skript-Bibliothek: " + scriptLib);
//		if (scriptDesc != null)
//			Logger.log(PluginManager.class, Logger.DEBUG, "lade Skript-Beschreibung: " + scriptDesc);
	}
	

	private String getBinaryClassName(String name)
	{
		name = name.substring(0, name.lastIndexOf("."));
		name = name.replace(File.separator, ".");
		name = name.replace("\\", ".");
		name = name.replace("/", ".");
		
		return name;
	}

	@Override
	public String toString()
	{
		return jar.getName();
	}
}