package cuina.editor.core;

import cuina.editor.core.util.Ini;
import cuina.editor.core.util.InvalidFileFormatException;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class CuinaProject
{
	public static final String CONFIG_FILE = "cuina.cfg";
	
	private IProject project;
	private Ini ini;
	
	public CuinaProject(IProject project)
	{
		if (project == null) throw new NullPointerException();
		
		this.project = project;
	}

	public IProject getProject()
	{
		return project;
	}
	
	public Ini getIni()
	{
		if (ini == null) try
		{
			String fileName = project.getFile(CONFIG_FILE).getLocation().toOSString();
			ini = new Ini(new File(fileName));
		}
		catch (IOException | InvalidFileFormatException e)
		{
			e.printStackTrace();
		}
		return ini;
	}
	
	public String getName()
	{
		return project.getName();
	}
	
	public void create(IProgressMonitor monitor) throws CoreException
	{
		project.create(monitor);
		project.open(monitor);
		
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { CuinaPlugin.NATURE_ID });
		project.setDescription(description, null);
	}
	
	/**
	 * Gibt an, ob das Projekt ein gültiges CuinaProjekt ist.
	 * Prüft zuerst ob das zugrunde liegende Projekt existiert und ob es ein Cuina Projekt ist.
	 * @return <code>true</code>, wenn das Projekt existiert und ein Cuina Projekt ist,
	 * andernfalls <code>false</code>.
	 */
	public boolean valid()
	{
		try
		{
			return project.exists() && project.hasNature(CuinaPlugin.NATURE_ID);
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Gibt den Projekt spezifischen Service zur angegebenenen API zurück.
	 * 
	 * @param api
	 *            Das Interface, dass den Service implementiert. Darf nicht <code>null</code> sein.
	 * @return Den Service, oder <code>null</code> wenn kein entsprechender Service gefunden wurde.
	 */
	public <T> T getService(Class<T> api)
	{
		ProjectServiceFactory factory = CuinaPlugin.getProjectServiceFactory(api);
		if (factory == null) return null;
		
		return (T) factory.create(api, this);
	}

	@Override
	public int hashCode()
	{
		return project.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null || !(obj instanceof CuinaProject)) return false;
		
		return project.equals( ((CuinaProject) obj).project );
	}

	@Override
	public String toString()
	{
		return project.toString();
	}
}
