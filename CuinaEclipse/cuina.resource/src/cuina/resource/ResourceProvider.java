package cuina.resource;

import cuina.editor.core.CuinaProject;
import cuina.resource.ResourceManager.Directory;
import cuina.resource.ResourceManager.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Stellt einen Projekt-Service für Dateizugriffe da.
 * <p>
 * Die Klasse bildet die Verzeichnisstruktur im Projekt ab.
 * </p>
 * @author TheWhiteShadow
 */
public class ResourceProvider
{
	public static final String RESOURCE_PATH = "resource.path";
	public static final String RESOURCE_SECTION = "Resource";

	private CuinaProject project;
	private final HashMap<String, Resource> cache = new HashMap<String, Resource>();

	ResourceProvider(CuinaProject project)
	{
		this.project = project;
	}

	public CuinaProject getCuinaProject()
	{
		return project;
	}

	/**
	 * Erzeugt die komplette Verzeichnisstruktur des Projeks.
	 * @param monitor
	 * @throws CoreException
	 */
	public void createFileStructure(IProgressMonitor monitor) throws CoreException
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRunnable operation = new IWorkspaceRunnable()
		{
			@Override
			public void run(IProgressMonitor monitor) throws CoreException
			{
				List<Directory> dirs = new ArrayList<Directory>(ResourceManager.getDirectories().values());
				
				for (int i = dirs.size()-1; i >= 0; i--)
				{
					if (!dirs.get(i).forCreate()) dirs.remove(i);
				}
				
				if (monitor != null) monitor.beginTask("Create Directories", dirs.size());
				for (Directory dir : dirs)
				{
					if (monitor != null) monitor.worked(1);
					System.out.println("[ResourceProvider] erstelle Verzeichnis: " + dir.getPath());
					createFolder(dir, monitor);
					if (dir.keepRef()) project.getIni().set(RESOURCE_SECTION, dir.getId(), dir.getPath().toString());
				}
				if (monitor != null) monitor.done();
			}
		};
		workspace.run(operation, monitor);
	}

	private boolean createFolder(Directory dir, IProgressMonitor monitor) throws CoreException
	{
		Directory parentDir = dir.getParent();
		if (parentDir != null)
		{
			IFolder parent = project.getProject().getFolder(parentDir.getPath().toString());
			if (!parent.exists())
			{
				if (!parentDir.forCreate()) return false;
				if (!createFolder(parentDir, monitor)) return false;
			}
		}

		IFolder folder = project.getProject().getFolder(dir.getPath().toString());
		if (!folder.exists()) folder.create(true, true, monitor);
		
		return true;
	}

	/**
	 * Gibt den Wurzelpfad für Ressourcen des angegebenen Typs zurück.
	 * @param type Ressourcen-Typ.
	 * @return Wurzelpfad der Ressourcen.
	 */
	public String getResourcePath(String type)
	{
		if (type == null) throw new NullPointerException();

		String path = project.getIni().get(RESOURCE_SECTION, type);
		if (path == null) path = ResourceManager.getDirectories().get(type).getPath().toString();
		return path;
	}

	private void addToCache(String key, String name, Resource res)
	{
		if (res == null) throw new NullPointerException();
		cache.put(key + '/' + name, res);
	}

	private Resource getFromCache(String key, String name)
	{
		return cache.get(key + '/' + name);
	}

	/**
	 * Leert den Ressourcen-Cache.
	 */
	public void clearCache()
	{
		for (Resource r : cache.values())
			r.dispose();
		cache.clear();
	}

	/**
	 * Gibt eine Ressource zu einer Datei zurück. Gesucht wird in allen
	 * definierten Pfaden. Der Typ gibt dabei einen Schlüssel zu einem Pfad an,
	 * welcher aus der Ini gelesen wird.
	 * 
	 * @param type
	 *            Resourcen-Typ. Der Assozieierte Pfad muss in der Ini vorhanden
	 *            sein.
	 * @param name
	 *            Dateiname oder Relativer Pfad zur Resource.
	 * @return Die Resource oder <code>null</code>, wenn nicht gefunden.
	 * @throws ResourceException
	 *             Wenn die Resource nicht gefunden wurde.
	 */
	public Resource getResource(String type, String name) throws ResourceException
	{
		if (name == null || name == "") throw new ResourceException(name, ResourceException.LOAD, null);

		String pathName = getResourcePath(type);
		if (pathName == null) throw new NullPointerException("Path for type '" + type + "' is null!");

		Resource ress = getFromCache(pathName, name);

		if (ress == null)
		{
			String[] pathsName = getResourceLocations();
			for (int i = 0; i < pathsName.length; i++)
			{
				Path fullPath = Paths.get(pathsName[i], pathName, name);
				Path path = Paths.get(pathsName[i], pathName);
				if (Files.exists(fullPath))
				{
					ress = new Resource(path, name);
					ress.extern = i > 0;
					break;
				}
			}
			if (ress != null)
			{
				addToCache(pathName, name, ress);
			}
			else
			{
				throw new ResourceException(name, ResourceException.LOAD, null);
			}
		}
		return ress;
	}

	/**
	 * Gibt eine Liste von Datei-Pfaden eines bestimmten Ressourcen-Types an,
	 * welche für den RessourcenProvider zugänglich sind.
	 * 
	 * @param key
	 *            Typ-Einschränkung
	 * @return Resourcen-Liste
	 */
	public ArrayList<Resource> getResourceList(String key)
	{
		ArrayList<Resource> list = new ArrayList<Resource>();
		String pathName = getResourcePath(key);
		if (pathName == null || pathName == "")
			throw new NullPointerException("Path for type '" + key + "' is null!");

		String[] pathsName = getResourceLocations();
		for (int i = 0; i < pathsName.length; i++)
		{
			Path path = Paths.get(pathsName[i], pathName);
			try
			{
				fillResourceList(list, path, path, key, i > 0);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return (list);
	}

	private void fillResourceList(List list, Path base, Path parent, String key, boolean extern) throws IOException
	{
		for (Path path : Files.newDirectoryStream(parent))
		{
			if (Files.isDirectory(path))
			{
				fillResourceList(list, base, path, key, extern);
			}
			else if (Files.isRegularFile(path))
			{
				String name = base.relativize(path).toString();
				Resource ress = getFromCache(key, name);
				if (ress == null)
				{
					ress = new Resource(base, name);
					ress.extern = extern;
					addToCache(key, name, ress);
				}
				list.add(ress);
			}
		}
	}

	/**
	 * Gibt alle Pfade zurück, unter denen Ressourcen für ein Projekt
	 * gespeichert sein können. Der erste Pfad entspricht dabei immer dem
	 * Projekt-Verzeichnis selbst. Alle anderen Pfade beziehen sich auf externe
	 * Quellen, die zusätzlich in der Projekt-Ini angegeben sind.
	 * 
	 * @return Array mit allen Ressourcen-Quellen des Projekts.
	 */
	private String[] getResourceLocations()
	{
		String[] allPaths;
		String resourcePath = project.getIni().get(RESOURCE_SECTION, RESOURCE_PATH);
		if (resourcePath != null)
		{
			String[] externPaths = resourcePath.split(";");
			allPaths = new String[externPaths.length + 1];
			System.arraycopy(externPaths, 0, allPaths, 1, externPaths.length);
		}
		else
		{
			allPaths = new String[1];
		}
		allPaths[0] = project.getProject().getLocation().toString();
		return allPaths;
	}
}
