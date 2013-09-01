package cuina.resource;

import cuina.editor.core.CuinaProject;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class ResourceManager
{
	public static final String KEY_GRAPHICS 	= "cuina.graphics.path";
	public static final String KEY_AUDIO 		= "cuina.audio.path";
	
	private static final String DIRECTORY_CONFIG_ID = "cuina.resource.directories";
	
//	static final String[] GRAPHIC_PATHS = new String[]
//	{
//		"tilesets",
//		"charsets",
//		"backgrounds",
//		"pictures",
//		"system",
//		"icons",
//		"autotiles",
//	};
//	
//	static final String[] AUDIO_PATHS = new String[]
//	{
//		"bgm",
//		"snd",
//	};
	
//	private static Image DEFAULT_ICON;
	private static final HashMap<String, Directory> directories = new HashMap<String, Directory>();
	private final static HashMap<CuinaProject, ResourceProvider> providers =
			new HashMap<CuinaProject, ResourceProvider>();
	
	static
	{
		addProjectHook();
		loadDirectories();
	}
	
	public static ResourceProvider getResourceProvider(CuinaProject project)
	{
		ResourceProvider provider = providers.get(project);
		if (provider == null)
		{
			provider = new ResourceProvider(project);
			providers.put(project, provider);
		}
		return provider;
	}
	
    public static HashMap<String, Directory> getDirectories()
    {
        return directories;
    }
    
    public static boolean isRegistedDirectory(IFolder folder)
    {
    	for(Directory dir : directories.values())
    	{
    		if (folder.getProjectRelativePath().equals(dir.getPath()) ) return true;
    	}
    	return false;
    }
 
    private static void loadDirectories()
    {
        IConfigurationElement[] elements = Platform.getExtensionRegistry().
        			getConfigurationElementsFor(DIRECTORY_CONFIG_ID);
 
        loadDirectories(null, elements);
    }
    
    private static void loadDirectories(Directory parent, IConfigurationElement[] elements)
    {
        for (IConfigurationElement conf : elements)
        {
            String id = conf.getAttribute("id");
            if (id == null) throw new NullPointerException("id is null");
            if (directories.containsKey(id)) throw new IllegalArgumentException("id '" + id + "' already exists.");
            
            String name = conf.getAttribute("name");
            boolean create = "true".equals(conf.getAttribute("create"));
            boolean keepRef = "true".equals(conf.getAttribute("keep-Reference"));
            String p = conf.getAttribute("parent");
            Directory customParent = (p != null) ? directories.get(p) : parent;
            
            Directory dir;
            if (customParent == null)
                dir = new Directory(id, Paths.get(name), create, keepRef, null);
            else
                dir = new Directory(id, customParent.getPath().resolve(name), create, keepRef, customParent);
            directories.put(id, dir);
            loadDirectories(dir, conf.getChildren());
        }
    }
	
	/**
	 * Ladet ein SWT-Image, der übergebenen Resource.
	 * Die Resource muss auf eine Datei mit gültigem Image-Format zeigen.
	 * <p>
	 * Es wird zuerst versucht das Bild aus dem Cache der Ressource zu laden.
	 * Wenn der Cache leer ist, wird die Datei aus dem Dateisystem geladen und in den Cache gelegt.
	 * </p>
	 * @param resource Image-Ressource, welche geladen werden soll.
	 * @return Das Image.
	 * @throws NullPointerException wenn die übergebene Ressource <code>null</code> ist.
	 * @throws ResourceException Wenn beim Laden Fehler auftreten
	 * oder Die Resource auf kein gültiges Image-Format zeigt.
	 * @throws ClassCastException wenn der Cache nicht leer ist und kein Image beinhaltet.
	 */
	public static Image loadImage(Resource resource) throws ResourceException
	{
		if (resource == null) throw new NullPointerException();
		ImageData data = (ImageData) resource.getData();
		if (data == null)
		{
			data = new ImageData( resource.getPath().toString());
			resource.setData(data);
		}
		return data.image;
	}
	
	/**
	 * Ladet ein SWT-Image mit dem angegebenen Namen.
	 * Der Name muss dem absoluten Pfad unterhalb 
	 * <p>
	 * Diese Methode ist aquivalent zum Aufruf:
	 * <pre>
	 * loadImage(getResourceProvider(project).getResource(KEY_GRAPHICS, imageName));
	 * </pre>
	 * </p>
	 * @param project Projekt-Kontext der Ressource.
	 * @param imagePath Path und Dateiname des Bildes.
	 * @return Das Image.
	 * @throws NullPointerException wenn das übergebene ResProjekt oder der Dateiname <code>null</code> ist.
	 * @throws ResourceException Wenn beim Laden Fehler auftreten oder
	 * Die Resource auf kein gültiges Image-Format zeigt.
	 * @throws ClassCastException wenn der Cache nicht leer ist und kein Image beinhaltet.
	 */
	public static Image loadImage(CuinaProject project, String imagePath) throws ResourceException
	{
		if (project == null) throw new NullPointerException();
		Resource res = getResourceProvider(project).getResource(KEY_GRAPHICS, imagePath);
		return loadImage(res);
	}

	/**
	 * Stellt eine Methode zum löschen von Ressourcen bereit.
	 * Objekte, die in einen Ressourcne-Cache abgelegt werden sollten dieses Interface implementieren.
	 * @author TheWhiteShadow
	 */
	public static interface Disposable
	{
		public void dispose();
	}
	
	/**
	 * Ressourcen Cache-Container für Image-Objekte.
	 * @author TheWhiteShadow
	 */
	public static class ImageData implements Disposable
	{
		public final Image image;
		
		public ImageData(String fileName) throws ResourceException
		{
			try
			{
				this.image = new Image(Display.getDefault(), fileName);
			}
			catch (Exception e)
			{
				throw new ResourceException(fileName, ResourceException.LOAD, e);
			}
		}

		@Override
		public void dispose()
		{
			image.dispose();
		}
	}
	
	private static void addProjectHook()
	{
		ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener()
		{
			@Override
			public void resourceChanged(IResourceChangeEvent event)
			{
				if (event.getResource() instanceof IProject && event.getType() == IResourceChangeEvent.PRE_CLOSE)
				{
					IProject project = (IProject) event.getResource();
					System.out.println("[ResourceManager] dispose Resourcen für Projekt: " + project.getName());
					ResourceProvider provider = providers.get(project);
					if (provider != null)
					{
						provider.clearCache();
						providers.remove(project);
					}
				}
			}
		});
	}
	
	/**
	 * Stellt eine Ressource da, die nur bei Bedarf die Daten vom Dateisystem ladet.
	 * @author TheWhiteShadow
	 */
	public final static class Resource
	{
		private int baseCount;
		final private Path path;
		private Object data;
		boolean extern;
		
		
		public Resource(Path basePath, String name)
		{
			this(basePath, name, null);
		}
		
		public Resource(Path basePath, String name, Object data)
		{
			this.baseCount = basePath.getNameCount();
			this.path = basePath.resolve(name);
			this.data = data;
		}
		
		/**
		 * Gibt den vollen Pfad der Resource zurück.
		 * @return Voller Pfad der Resource.
		 */
		public Path getPath()
		{
			return path;
		}
		
		public URL getURL()
		{
			try
			{
				return path.toUri().toURL();
			}
			catch (MalformedURLException e)
			{
				Assert.isTrue(false);
			}
			return null;
		}
		
		public String getName()
		{
			return path.subpath(baseCount, path.getNameCount()).toString();
		}

		public void setData(Object data)
		{
			this.data = data;
		}
		
		/**
		 * Gibt die Daten zurück, wenn es geladen wurde, ansonsten <code>null</code>.
		 * @return Das Daten-Objekt wenn geladen, andernfalls <code>null</code>.
		 */
		public Object getData()
		{
			return data;
		}
		
		/** Wenn die beinhaltenden Daten vom Typ Disposable sind, werden sie disposed. */
		public void dispose()
		{
			if (data instanceof Disposable) ((Disposable) data).dispose();
		}
		
		/**
		 * Gibt an, ob das Bild im Projektordner liegt oder woanders.
		 * @return <code>true</code>, wenn das Bild außerhalb des Projektordners liegt,
		 * andernfalls <code>false</code>.
		 */
		public boolean isExtern()
		{
			return extern;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((path == null) ? 0 : path.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			Resource other = (Resource) obj;
			if (path == null)
			{
				if (other.path != null) return false;
			}
			else if (!path.equals(other.path)) return false;
			return true;
		}

		@Override
		public String toString()
		{
			return path.toString();
		}
	}
	
    public static class Directory
    {
        private String id;
        private Path path;
        private boolean create;
        private boolean keepRef;
        private Directory parent;
        
        public Directory(String id, Path path, boolean create, boolean keepRef, Directory parent)
        {
            this.id = id;
            this.path = path;
            this.create = create;
            this.keepRef = keepRef;
            this.parent = parent;
        }
 
        public String getId()
        {
            return id;
        }
 
        public Path getPath()
        {
            return path;
        }
 
        public boolean isCreate()
        {
            return create;
        }
 
        public boolean isKeepRef()
        {
            return keepRef;
        }
 
        public Directory getParent()
        {
            return parent;
        }
 
        @Override
        public String toString()
        {
            return id + "=" + path;
        }
    }
}
