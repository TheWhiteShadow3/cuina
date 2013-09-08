package cuina.editor.script.internal;

import cuina.editor.core.CuinaProject;
import cuina.editor.core.IProjectHook;

public class ProjectScriptHook implements IProjectHook
{
	@Override
	public void createProject(CuinaProject project)
	{
//		try
//		{
//			DataTable<Script> table = project.getService(Database.class).loadTable("Scripts");
//			Script script = new Script();
//			script.setKey("main");
//			script.setName("Main");
//			script.setInterfaceClass("cuina.script.MainScript");
//			
//			table.put(obj)
//		}
//		catch (ResourceException e)
//		{
//			e.printStackTrace();
//		}
		System.out.println("Mein erster Projekt-Hook :) - Class: " + getClass().getName());
	}

	@Override
	public void deleteProject(CuinaProject project)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void openProject(CuinaProject project)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void closeProject(CuinaProject project)
	{
		// TODO Auto-generated method stub

	}
}
