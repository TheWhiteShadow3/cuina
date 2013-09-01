package cuina.editor.core;

public interface ProjectServiceFactory
{
	public Object create(Class api, CuinaProject cuinaProject);
}
