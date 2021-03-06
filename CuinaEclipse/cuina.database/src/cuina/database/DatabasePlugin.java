package cuina.database;

import cuina.editor.core.CuinaCore;
import cuina.editor.core.CuinaProject;
import cuina.resource.ResourceException;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.BundleContext;


/**
 * Das DatabasePlugin bietet die Möglichkeit Objekte in Listen zu serialisieren
 * und bei Bedarf mit einem Key darauf zuzugreifen.
 * <p>
 * <b>Datenbanken</b> werden Projektspezifisch verwaltet.
 * Über {@link #getDatabase(CuinaProject)} kann man sich die Datenbank zum Projekt geben lassen.
 * </p>
 * <p>
 * <b>Objekt-Typen</b> werden global für alle Datenbanken verwaltet.
 * Um einen neuen Objekt-Typ zu erstellen, kann der Erweiterungspunkt <code>cuina.database.types</code> benutzt werden.
 * Alternativ kann auch {@link #registDatabase(String, Class)} verwendet werden um dynamisch Objekt-Typen hinzuzufügen.
 * </p>
 * @see cuina.database.Database
 */
public final class DatabasePlugin extends Plugin
{
	// The plug-in ID
	public static final String PLUGIN_ID = "cuina.database"; //$NON-NLS-1$
	
	public static final String DATABASE_DIRECTORY_ID = "cuina.database.path"; //$NON-NLS-1$
	
	public static final String META_DATA_FILE = "meta"; //$NON-NLS-1$
	
	public static final String TYPE_EXTENSION = "cuina.database.types"; //$NON-NLS-1$
	
	private static final String MSG_SERVICE_CLOSED = "database-service is closed!"; //$NON-NLS-1$
	
	// The shared instance
	private static DatabasePlugin plugin;
	
	private HashMap<String, IDatabaseDescriptor> databaseTypes = new HashMap<String, IDatabaseDescriptor>();
	private HashMap<CuinaProject, Database> databases = new HashMap<CuinaProject, Database>();
	
	/**
	 * Erstellt eine neue Instanz des Plugins.
	 * <b>Do NOT use!</b>
	 */
	public DatabasePlugin()
	{
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		
		registDatabases();
		addProjectHook();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
	}
	
	/**
	 * Registriert einen neuen Objekt-Typen.
	 * Als Name kann z.B. Class.getName() benutzt werden.
	 * @param name Einmaliger Identifikationsname des Objekt-Typen.
	 * @param clazz Klasse des Objekt-Typen.
	 * @return Den DatabaseDescriptor zum Objekt-Typen.
	 * @throws IllegalStateException wenn das Plugin gestoppt ist.
	 */
	public static <E extends DatabaseObject> DatabaseDescriptor<E> registDatabase(String name, Class<E> clazz)
	{
		if (plugin == null) throw new IllegalStateException(MSG_SERVICE_CLOSED);
		
		DatabaseDescriptor<E> descriptor = new DatabaseDescriptor(name, clazz);
		plugin.databaseTypes.put(descriptor.getName(), descriptor);
		return descriptor;
	}

	/**
	 * Gibt das Plugin zurück.
	 * 
	 * @return Das Plugin.
	 */
	public static DatabasePlugin getPlugin()
	{
		return plugin;
	}
	
	static String loadDataPath(CuinaProject project)
	{
		return project.getIni().get(DatabasePlugin.PLUGIN_ID, DatabasePlugin.DATABASE_DIRECTORY_ID, "data");
	}
	
	/**
	 * Gibt die Datenbank für ein Projekt zurück.
	 * Existiert diese nicht wird die Datenbank angelegt.
	 * @param project Projekt
	 * @return die Projekt-Datenbank.
	 * @throws IllegalStateException wenn das Plugin gestoppt ist.
	 */
	public static synchronized Database getDatabase(CuinaProject project)
	{
		if (plugin == null || project == null) throw new IllegalStateException(MSG_SERVICE_CLOSED);
		Database db = plugin.databases.get(project);
		if (db == null)
		{
			System.out.println("[DatabasePlugin] erstelle Datenbank für Projekt: " + project.getName());
			db = new Database(project);
			plugin.databases.put(project, db);
		}
		return db;
	}

//	/**
//	 * Gibt die Datenbank zu einer Tabelle zurück.
//	 * Wenn die entsprechende Datenbank nicht existiert wird <code>null</code> zurück gegeben.
//	 * Dies ist z.B. dann der Fall, wenn die Tabelle manuell erstellt wurde.
//	 * @param table Tabelle
//	 * @return Datenbank zur Tabelle, oder null wenn keine Datenbank zur Tabelle existiert.
//	 * @throws IllegalStateException wenn das Plugin gestoppt ist.
//	 */
//	public static Database findDatabase(DataTable table)
//	{
//		if (plugin == null) throw new IllegalStateException(SERVICE_CLOSED);
//		if (table.getFileName() == null) return null;
//		
//		String projectName = new Path(table.getFileName()).segment(0);
//		return plugin.databases.get(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName));
//	}
	
	/**
	 * Gibt den Descriptor zum Datenbank-Typen an.
	 * @param name Typ-Name
	 * @return Den DatabaseDescriptor.
	 * @throws IllegalStateException wenn das Plugin gestoppt ist.
	 */
	public static IDatabaseDescriptor getDescriptor(String name)
	{
		if (plugin == null) throw new IllegalStateException(MSG_SERVICE_CLOSED);
		
		return plugin.databaseTypes.get(name);
	}
	
	/**
	 * Gibt den Datenbank Descriptor zur angegebenen Datenbank Datei an.
	 * @param file Datenbank Datei.
	 * @return Den DatabaseDescriptor.
	 * @throws IllegalStateException wenn das Plugin gestoppt ist.
	 */
	public static IDatabaseDescriptor getDescriptor(IFile file)
	{
		return getDescriptor(getTableNameFromFile(file));
	}
	
	public static IFile getTableFile(DataTable table)
	{
		Path path = new Path(table.getFileName());
		return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
	}
	
//	public static <E extends DatabaseObject> DatabaseDescriptor<E> getDescriptor(Class<E> clazz)
//	{
//		if (plugin == null) throw new IllegalStateException(MSG_SERVICE_CLOSED);
//		
//		for (DatabaseDescriptor d : plugin.databaseTypes.values())
//		{
//			if (d.getDataClass() == clazz) return d;
//		}
//		return null;
//	}
	
	static String getTableNameFromFile(IFile file)
	{
		String name = file.getName();
		String ext  = file.getFileExtension();
		if (ext.isEmpty())
			return name;
		else
			return name.substring(0, name.length() - ext.length() - 1);
	}
	
	/**
	 * Gibt eien Liste aller verfügbaren DatabaseDescriptor zurück.
	 * @return Liste aller verfügbaren DatabaseDescriptor.
	 * @throws IllegalStateException wenn das Plugin gestoppt ist.
	 */
	public static IDatabaseDescriptor[] getDescriptors()
	{
		if (plugin == null) throw new IllegalStateException(MSG_SERVICE_CLOSED);
		
		DatabaseDescriptor[] list = new DatabaseDescriptor[plugin.databaseTypes.size()];
		return plugin.databaseTypes.values().toArray(list);
	}
	
	private void registDatabases()
	{
		IConfigurationElement[] elements = Platform.getExtensionRegistry().
				getConfigurationElementsFor(TYPE_EXTENSION);

		for(IConfigurationElement conf : elements)
		{
			try
			{
				IDatabaseDescriptor descriptor = new DatabaseDescriptor(conf);
			
				databaseTypes.put(descriptor.getName(), descriptor);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	static DataTable<?> tryLoad(IFile file)
	{
		if (plugin == null) throw new IllegalStateException(MSG_SERVICE_CLOSED);
		
		CuinaProject project = CuinaCore.getCuinaProject(file.getProject());
		IFolder f = project.getProject().getFolder( loadDataPath(project));
		if (file.getParent().equals(f) && !file.getName().startsWith(DatabasePlugin.META_DATA_FILE)) try
		{
			return getDatabase(project).loadTable(file);
		}
		catch (ResourceException e) { /* fail */ }
		return null;
	}
	
	public static boolean isDataFile(IFile file)
	{
		return tryLoad(file) != null;
	}
	
	private void addProjectHook()
	{
		ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener()
		{
			@Override
			public void resourceChanged(IResourceChangeEvent event)
			{
				if (event.getType() == IResourceChangeEvent.PRE_CLOSE && event.getResource() instanceof IProject)
				{
					IProject project = (IProject) event.getResource();
					System.out.println("[DatabasePlugin] schließe Datenbank für Projekt: " + project.getName());
					IDatabaseDescriptor descriptor = databaseTypes.get(project.getName());
					if (descriptor != null)
					{
						Image image = descriptor.getImage();
						if (image != null) image.dispose();
						databaseTypes.remove(project.getName());
					}
				}
				else if (event.getType() == IResourceChangeEvent.POST_CHANGE)
				{
					findChangedTables(event.getDelta());
				}
			}
			
			private void findChangedTables(IResourceDelta delta)
			{
				for (IResourceDelta projectDelta : delta.getAffectedChildren())
				{
					IProject project = projectDelta.getResource().getProject();
					Database db = databases.get(CuinaCore.getCuinaProject(project));
					if (db == null) continue;
					
					IResourceDelta folderDelta = delta.findMember(db.getDataPath());
					if (folderDelta != null)
					{
						for (IResourceDelta fileDelta : folderDelta.getAffectedChildren())
						{
							String name = getTableNameFromFile((IFile) fileDelta.getResource());
							if (name != null)
							{
								DataTable table = db.getCache().get(name);
								if (table != null) db.fireTableChanged(table);
							}
						}
					}
				}
			}
		});
	}
}
