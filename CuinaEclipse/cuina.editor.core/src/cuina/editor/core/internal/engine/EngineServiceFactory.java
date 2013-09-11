package cuina.editor.core.internal.engine;

import cuina.editor.core.CuinaProject;
import cuina.editor.core.ProjectServiceFactory;
import cuina.editor.core.engine.EngineReference;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class EngineServiceFactory implements ProjectServiceFactory
{
	private final Map<CuinaProject, EngineReference> references = new HashMap<CuinaProject, EngineReference>();
	
	@Override
	public Object create(Class api, CuinaProject cuinaProject)
	{
		EngineReference ref = references.get(cuinaProject);
		if (ref == null)
		{
			try
			{
				ref = new EngineReference(cuinaProject);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				return null;
			}
			references.put(cuinaProject, ref);
		}
		
		if (api == EngineReference.class)
		{
			return ref;
		}
		if (api == ClassLoader.class)
		{
			return ref.getClassLoader();
		}
		return null;
	}
}
