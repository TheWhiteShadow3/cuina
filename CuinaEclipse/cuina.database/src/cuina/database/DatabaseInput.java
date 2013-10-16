package cuina.database;

import cuina.editor.core.CuinaCore;
import cuina.editor.core.CuinaProject;
import cuina.resource.ResourceException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

public class DatabaseInput implements IEditorInput, IPersistableElement
{
	private IFile file;
	private String key;
	private DataTable table;
	
	public DatabaseInput(IFile file, String key)
	{
		if (file == null) throw new NullPointerException("file is null.");
		if (key == null) throw new NullPointerException("key is null.");
		this.file = file;
		this.key = key;
	}
	
	public DatabaseInput(DataTable table, String key)
	{
		this(getFile(table), key);
	}
	
	public DatabaseInput(CuinaProject project, String tableName, String key) throws ResourceException
	{
		this(getDataTable(project, tableName), key);
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
				return getTable();
			}
			if (adapter == DatabaseObject.class)
			{
				return getData();
			}
		}
		catch (ResourceException e) {}
		return null;
	}

	@Override
	public boolean exists()
	{
		try
		{
			return getTable() != null;
		}
		catch (ResourceException e)
		{
			return false;
		}
	}

	@Override
	public ImageDescriptor getImageDescriptor()
	{
		return ImageDescriptor.createFromImage(DatabasePlugin.getDescriptor(file).getImage());
	}

	@Override
	public String getName()
	{
		return file.toString() + '/' + key;
	}
	
	public CuinaProject getCuinaProject()
	{
		return CuinaCore.getCuinaProject(file.getProject());
	}
	
	public DataTable getTable() throws ResourceException
	{
		if (table == null)
			table = getCuinaProject().getService(Database.class).loadTable(file);
		return table;
	}
	
	public DatabaseObject getData() throws ResourceException
	{
		return getTable().get(key);
	}
	
	public String getKey()
	{
		return key;
	}

	@Override
	public IPersistableElement getPersistable()
	{
		return this;
	}

	@Override
	public String getToolTipText()
	{
		return "Datenbankelement: " + getName();
	}

	private static IFile getFile(DataTable table)
	{
		return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(table.getFileName()));
	}
	
	private static DataTable getDataTable(CuinaProject project, String tableName) throws ResourceException 
	{
		return project.getService(Database.class).loadTable(tableName);
	}

	@Override
	public int hashCode()
	{
		final int prime = 37;
		return prime * (prime + file.hashCode()) + key.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		DatabaseInput other = (DatabaseInput) obj;
		return (file.equals(other.file) && key.equals(other.key));
	}

	@Override
	public void saveState(IMemento memento)
	{
		memento.putString("file", file.getFullPath().toString());
		memento.putString("key", key);
	}

	@Override
	public String getFactoryId()
	{
		return "cuina.database.InputFactory";
	}
}
