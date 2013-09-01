package cuina.editor.script.internal;

import cuina.editor.core.CuinaProject;
import cuina.editor.core.ProjectServiceFactory;
import cuina.editor.script.Scripts;
import cuina.editor.script.library.StaticScriptLibrary;

public class ScriptServiceFactory implements ProjectServiceFactory
{
	@Override
	public Object create(Class api, CuinaProject cuinaProject)
	{
		if (api == StaticScriptLibrary.class)
			return Scripts.getScriptLibrary(cuinaProject);
		
		return null;
	}
}
