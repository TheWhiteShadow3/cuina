package cuina.editor.eventx.internal;

import cuina.editor.core.CuinaProject;
import cuina.editor.core.ProjectServiceFactory;

public class CommandServiceFactory implements ProjectServiceFactory
{
	@Override
	public Object create(Class api, CuinaProject cuinaProject)
	{
		if (cuinaProject == null) throw new NullPointerException();
		if (api != CommandLibrary.class) return null;
		
		return EventPlugin.getLibrary(cuinaProject);
	}
}