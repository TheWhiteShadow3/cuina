package cuina.database;

import cuina.editor.core.CuinaProject;
import cuina.resource.ResourceException;
import cuina.resource.SerializationManager;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Eine Datenbank bietet die Möglichkeit Objekte in Listen zu serialisieren
 * und bei Bedarf mit einem Key darauf zuzugreifen.
 * <p>
 * <b>Meta-Daten</b> können für eine Datenbank angelegt werden um neben den eigentlichen Daten
 * Zusätzliche Informationen zu gespeichert.
 * Das Plugin cuina.database.ui speichert Beispielsweise die Baum-Daten für die Tabellen Viewes.
 * </p>
 * @author TheWhiteShadow
 */
public class Database
{
	private CuinaProject project;
	private String dataPath;
	
	private HashMap<String, Object> metaData;
	private HashMap<String, DataTable> cache = new HashMap<String, DataTable>();
	
	protected Database(CuinaProject project)
	{
		this.project = project;
		this.dataPath = DatabasePlugin.loadDataPath(project);
	}
	
	public CuinaProject getProject()
	{
		return project;
	}
	
	public String getDataPath()
	{
		return dataPath;
	}
	
//	public IPath getDatabasePath()
//	{
//		ResourceProvider rp = ResourceManager.getResourceProvider(project);
//		return rp.getResourcePath(DatabasePlugin.DATABASE_DIRECTORY_ID);
//	}

	public Object getMetaData(String key)
	{
		if (metaData == null) loadMetaData();
		return metaData.get(key);
	}

	public void setMetaData(String key, Object value)
	{
		if (metaData == null) loadMetaData();
		if (value == null)
			metaData.remove(key);
		else
			metaData.put(key, value);
	}

	public <E extends DatabaseObject> DataTable<E> loadTable(String name) throws ResourceException
	{
		DataTable<E> table = cache.get(name);
		if (table == null)
		{
			IFile file = getPreferredDataFile(name);
			if (file == null) throw new ResourceException("Database '" + name + "' not found!");
			table = loadTable(file, name);
		}
		
		return table;
	}
	
	private IFile getPreferredDataFile(String name) throws ResourceException
	{
		IFolder folder = project.getProject().getFolder(dataPath);
		IFile found = null;
		
		try
		{
			IResource[] elements = folder.members();
			for (IResource r : elements)
			{
				if (r instanceof IFile && r.getName().startsWith(name))
				{
					String ext = r.getFileExtension();
					if (ext != null && ext.equals(SerializationManager.getDefaultExtension()) )
					{
						return (IFile) r;
					}
					if (found == null) found = (IFile) r;
				}
			}
			return found;
		}
		catch (CoreException e)
		{
			throw new ResourceException("Database '" + name + "' not found!", e);
		}
	}
	
	public <E extends DatabaseObject> DataTable<E> loadTable(IFile file) throws ResourceException
	{
		String name = DatabasePlugin.getTableNameFromFile(file);
		DataTable<E> table = cache.get(name);
		if (table == null)
		{
			table = loadTable(file, name);
		}
			
		return table;
	}
	
	private <E extends DatabaseObject> DataTable<E> loadTable(IFile file, String name) throws ResourceException
	{
		if (file == null) throw new NullPointerException("file is null");
		if (name == null) throw new NullPointerException("name is null");
		
		IDatabaseDescriptor descriptor = DatabasePlugin.getDescriptor(name);
		if (descriptor == null) throw new ResourceException("Descriptor for " + name + " is null!");
		
		Class clazz = descriptor.getDataClass();
		if (clazz == null) throw new ResourceException("Data-Type for " + name + " not found!");
	
		DataTable<E> table = (DataTable<E>)SerializationManager.load(file, clazz.getClassLoader());
		
		if (table == null)
		{
			table = new DataTable<E>(name, clazz);
		}
		
		table.setRuntimeMetaData(this, file.getFullPath().toString());
		cache.put(name, table);
		
		return table;
	}
	
	public void saveTable(DataTable table) throws ResourceException
	{
		if (table.getFileName() == null)
		{
			String name = table.getName() + "." + SerializationManager.getDefaultExtension();
			table.setRuntimeMetaData(this, dataPath + '/' + name);
		}
		IPath path = new Path(table.getFileName());
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		SerializationManager.save(table, file);
	}
	
	public void saveMetaData() throws ResourceException
	{
		IFile metaFile = project.getProject().getFile(dataPath + '/' + DatabasePlugin.META_DATA_FILE);
		if (metaData != null)
		{
			SerializationManager.save(metaData, metaFile);
		}
	}
	
	private void loadMetaData()
	{
		IFile metaFile = project.getProject().getFile(dataPath + '/' + DatabasePlugin.META_DATA_FILE);
		if (metaFile.exists())
		{
			try
			{
				metaData = (HashMap<String, Object>) SerializationManager.load(metaFile, getClass().getClassLoader());
			}
			catch (ResourceException e)
			{
				e.printStackTrace();
			}
		}
		if (metaData == null) metaData = new HashMap<String, Object>();
	}
	
	public <E extends DatabaseObject> DataTable<E> createNewTable(String name)
	{
		IDatabaseDescriptor descriptor = DatabasePlugin.getDescriptor(name);
		if (descriptor == null) throw new NullPointerException("Database " + name + " is not registed!");

		return new DataTable<E>(name, descriptor.getDataClass());
	}
	
	public static String getValidKey(String str)
	{
		if (str == null || str.isEmpty()) str = "new";
		StringBuilder builder = new StringBuilder(str.length());
		
		char c;
		for(int i = 0; i < str.length(); i++)
		{
			c = str.charAt(i);
			if ((c > 64 && c < 91) || (c > 96 & c < 123) || (c > 47 && c < 58 & i > 0) || c == '_')
			{
//				System.out.println("Letter: " + c);
			}
			else
			{
				switch(c)
				{	// Buchstaben, die ersetzt werden können.
					case ' ': c = '_'; break;
					case 'ä': c = 'a'; break;
					case 'ö': c = 'o'; break;
					case 'ü': c = 'u'; break;
					case 'Ä': c = 'A'; break;
					case 'Ö': c = 'O'; break;
					case 'Ü': c = 'U'; break;
					case 'ß': c = 's'; break;
					default: continue;
				}
			}
			builder.append(c);
		}

		return builder.toString();
	}
}
