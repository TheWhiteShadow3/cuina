package cuina.editor.core.internal.engine;

import cuina.editor.core.engine.EngineReference;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

public class EngineClassLoader extends URLClassLoader
{
	private EngineReference engineReference;

	public EngineClassLoader(EngineReference engineReference) throws IOException
	{
		super(new URL[0]);
		this.engineReference = engineReference;

		Path path = engineReference.resolveEnginePath();
		try
		{
			addURL(path.toUri().toURL());
		}
		catch (MalformedURLException e)
		{	// can't happen
			throw new Error(e);
		}
		addPlugins(engineReference.resolvePluginPath());
	}

	public EngineReference getEngineReference()
	{
		return engineReference;
	}

	private void addPlugins(Path path) throws IOException
	{
		for (Path p : Files.newDirectoryStream(path))
		{
			try
			{
				addURL(p.toUri().toURL());
			}
			catch (MalformedURLException e)
			{	// can't happen
				throw new Error(e);
			}
		}
	}
}
