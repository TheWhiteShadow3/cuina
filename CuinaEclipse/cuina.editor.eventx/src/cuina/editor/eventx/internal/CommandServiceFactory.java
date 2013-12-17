package cuina.editor.eventx.internal;

import java.util.HashMap;
import java.util.Map;

import cuina.editor.core.CuinaProject;
import cuina.editor.core.ProjectServiceFactory;

public class CommandServiceFactory implements ProjectServiceFactory
{
	private static Map<CuinaProject, CommandLibrary> libraries = new HashMap<CuinaProject, CommandLibrary>();
	
	@Override
	public Object create(Class api, CuinaProject cuinaProject)
	{
		if (cuinaProject == null) throw new NullPointerException();
		if (api != CommandLibrary.class) return null;
		
		CommandLibrary library = libraries.get(cuinaProject);
		if (library == null)
		{
			library = new CommandLibrary(cuinaProject);
			libraries.put(cuinaProject, library);
		}
		return library;
	}
}