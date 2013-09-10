package cuina.editor.core.internal;


import cuina.editor.core.EngineReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;


public class EngineClassLoader extends URLClassLoader
{
	private EngineReference engineReference;
	
	public EngineClassLoader(EngineReference engineReference) throws FileNotFoundException
	{
		super(new URL[0]);
		this.engineReference = engineReference;
		
		String path = Util.resolveEnviromentVariables(engineReference.getEnginePath());
		if (path != null)
		{
			File file = new File(path);
			if (!file.exists()) throw new FileNotFoundException();
			try
			{
				addURL(file.toURI().toURL());
			}
			catch (MalformedURLException e)
			{ throw new Error(e); } // can't happen
			addPlugins();
		}
	}

	private void addPlugins()
	{
		String path = Util.resolveEnviromentVariables(engineReference.getPluginPath());
		if (path != null)
		{
			File rootDir = new File(path);
			if (!rootDir.exists()) return;
			for (File file : rootDir.listFiles())
			{
				try
				{
					addURL(file.toURI().toURL());
				}
				catch (MalformedURLException e)
				{ throw new Error(e); } // can't happen
			}
		}
	}
}
