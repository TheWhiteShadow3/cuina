package cuina.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	private String scriptDesc;
	
	private String dependencyStr;
	private Map<String, String> dependencies = new HashMap<String, String>();
	private List<String> classNames;
	
	public CuinaPlugin(File file) throws IOException
	{
		this.file = file;
		this.jar = new JarFile(file);
		scanManifest();
		resolveDependencyAttribut();
	}
	
	public String getName()
	{
		return file.getName();
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
	
	public String[] getClassNames()
	{
		return classNames.toArray(new String[classNames.size()]);
	}
	
	public Map<String, String> getDependencies()
	{
		return Collections.unmodifiableMap(dependencies);
	}

	public String getScriptLib()
	{
		return scriptLib;
	}

	public String getScriptDesc()
	{
		return scriptDesc;
	}

	private void scanManifest() throws IOException
	{
		Manifest manifest = jar.getManifest();
		if (manifest != null)
		{
			Attributes att 	= manifest.getMainAttributes();
			version			= att.getValue("Plugin-Version");
			pluginClass 	= att.getValue("Plugin-Classes");
			scriptLib 		= att.getValue("Script-Library");
			scriptDesc 		= att.getValue("Script-Description");
			dependencyStr	= att.getValue("Plugin-Dependency");
		}
	}

	public void load() throws IOException, ClassNotFoundException
	{
		if (pluginClass != null)
		{
			if (pluginClass.isEmpty())
			{
				this.classNames = Collections.EMPTY_LIST;
				return;
			}
			
			this.classNames = Arrays.asList(pluginClass.split(";"));
		}
		else
		{
			this.classNames = new ArrayList<String>(8);
			Enumeration<JarEntry> entries = jar.entries();
			while(entries.hasMoreElements())
			{
				JarEntry entry = entries.nextElement();
				if (entry.getName().endsWith(".class"))
				{
					classNames.add( getBinaryClassName(entry.getName()) );
				}
			}
		}
	}

	private void resolveDependencyAttribut() throws IOException
	{
		if (dependencyStr != null && !dependencyStr.isEmpty())
		{
			String[] plugins = dependencyStr.split("[;-]");
			if (plugins.length % 2 != 0)
			{
				throw new IOException("Invalid attribut-format 'Plugin-Dependency' in Plugin: " + jar.getName());
			}
			
			for (int i = 0; i < plugins.length; i+=2)
			{
				String name = plugins[i];
				String ver = plugins[i+1];
				
				if (!name.endsWith(".jar")) name = name + ".jar";
				dependencies.put(name, ver);
			}
		}
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
		return getName() + " - " + getVersion();
	}
}