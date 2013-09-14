package cuina.database;

import cuina.editor.core.CuinaCore;
import cuina.editor.core.CuinaProject;
import cuina.resource.ResourceException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class DatabaseInput implements IEditorInput
{
//	private String tableName;
	private IFile file;
	private String key;
//	private IProject project;
	
	public DatabaseInput(IFile file, String key)
	{
		this.file = file;
		this.key = key;
	}
	
	public DatabaseInput(CuinaProject project, DataTable table, String key)
	{
		Path path = new Path(table.getFileName());
		this.file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		this.key = key;
	}
	
	public DatabaseInput(CuinaProject project, String tableName, String key) throws ResourceException
	{
		this(project, getDataTable(project, tableName), key);
	}
	
	@Override
	public Object getAdapter(Class adapter)
	{
		if (adapter == DatabaseInput.class) return this;
		if (adapter == IFile.class) return file;
		if (adapter == CuinaProject.class) return getCuinaProject();
		
		try
		{
			if (adapter == DataTable.class)
			{
				return getCuinaProject().getService(Database.class).loadTable(file);
			}
			if (adapter == DatabaseObject.class)
			{
				return getCuinaProject().getService(Database.class).loadTable(file).get(key);
			}
		}
		catch (ResourceException e) {}
		return null;
	}

	@Override
	public boolean exists()
	{
		return file.exists();
	}

	@Override
	public ImageDescriptor getImageDescriptor()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return file.toString() + "/" + key;
	}

	@Override
	public IPersistableElement getPersistable()
	{
		return null;
	}

	@Override
	public String getToolTipText()
	{
		return "Datenbankelement: " + getName();
	}
	
	private CuinaProject getCuinaProject()
	{
		return CuinaCore.getCuinaProject(file.getProject());
	}
	
	private static DataTable getDataTable(CuinaProject project, String tableName) throws ResourceException 
	{
		return project.getService(Database.class).loadTable(tableName);
	}
}
