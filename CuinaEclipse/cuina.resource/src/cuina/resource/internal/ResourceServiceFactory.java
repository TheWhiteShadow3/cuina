package cuina.resource.internal;

import cuina.editor.core.CuinaProject;
import cuina.editor.core.ProjectServiceFactory;
import cuina.resource.ResourceManager;
import cuina.resource.ResourceProvider;

public class ResourceServiceFactory implements ProjectServiceFactory
{
	@Override
	public Object create(Class api, CuinaProject cuinaProject)
	{
		if (api != ResourceProvider.class) return null;
		
		return ResourceManager.getResourceProvider(cuinaProject);
	}
}
