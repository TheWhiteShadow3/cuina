package cuina.editor.core.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;


public class EngineClassLoader extends URLClassLoader
{
	public EngineClassLoader() throws FileNotFoundException
	{
		super(new URL[0]);
		
		String path = CuinaEngine.getEnginePath();
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
		String path = CuinaEngine.getPluginPath();
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
