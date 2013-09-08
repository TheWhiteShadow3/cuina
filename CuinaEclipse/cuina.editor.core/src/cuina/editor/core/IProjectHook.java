package cuina.editor.core;

public interface IProjectHook
{
	public void createProject(CuinaProject project);
	public void deleteProject(CuinaProject project);
	
	/** <i>Wird (noch) nicht benutzt!</i> */
	public void openProject(CuinaProject project);
	
	/** <i>Wird (noch) nicht benutzt!</i> */
	public void closeProject(CuinaProject project);
}
