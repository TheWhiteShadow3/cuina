package cuina.database;

import cuina.editor.core.CuinaProject;
import cuina.editor.core.ProjectServiceFactory;

public class DatabaseServiceFactory implements ProjectServiceFactory
{
	@Override
	public Object create(Class api, CuinaProject cuinaProject)
	{
		if (api != Database.class) return null;
		
		return DatabasePlugin.getDatabase(cuinaProject);
	}
}
