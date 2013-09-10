package cuina.editor.core.internal;

import cuina.editor.core.CuinaProject;
import cuina.editor.core.ProjectServiceFactory;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class ClassLoaderServiceFactory implements ProjectServiceFactory
{
	private final Map<CuinaProject, EngineClassLoader> classLoaders = new HashMap<CuinaProject, EngineClassLoader>();
	
	@Override
	public Object create(Class api, CuinaProject cuinaProject)
	{
		if (api == ClassLoader.class)
		{
			EngineClassLoader cl = classLoaders.get(cuinaProject);
			if (cl == null)
			{
				try
				{
					cl = new EngineClassLoader(cuinaProject.getEngineReference());
				}
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
					return null;
				}
				classLoaders.put(cuinaProject, cl);
			}
			return cl;
		}
		return null;
	}
}
