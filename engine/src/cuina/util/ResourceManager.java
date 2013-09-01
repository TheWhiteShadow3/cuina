package cuina.util;

import cuina.Game;
import cuina.Logger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

public final class ResourceManager
{
	public static final String KEY_GRAPHICS 	= "cuina.graphics.path";
	public static final String KEY_AUDIO 		= "cuina.audio.path";
	public static final String RESOURCE_SECTION = "Resource";

//	public static final String PATH_GRAPHICS 	= "graphics" + File.separator;
//	public static final String PATH_AUDIO 		= "audio" 	 + File.separator;
//	public static final String PATH_MAPS 		= "maps" 	 + File.separator;
//	public static final String PATH_DATA 		= "data"	 + File.separator;
	
//	public static final String PATH_TILESETS 	= PATH_GRAPHICS + "tilesets";
//	public static final String PATH_CHARSETS 	= PATH_GRAPHICS + "charsets";
//	public static final String PATH_BACKGROUNDS = PATH_GRAPHICS + "backgrounds";
//	public static final String PATH_PICTURES	= PATH_GRAPHICS + "pictures";
//	public static final String PATH_SYSTEM 		= PATH_GRAPHICS + "system";
//	public static final String PATH_ICONS 		= PATH_GRAPHICS + "icons";
//	public static final String PATH_AUTOTILES	= PATH_GRAPHICS + "autotiles";
	
//	public static final String PATH_BGM 		= PATH_AUDIO 	+ "bgm";
//	public static final String PATH_SND			= PATH_AUDIO	+ "snd";
	
	public static final int IMG_TILESET 	= 0;
	public static final int IMG_CHARSET 	= 1;
	public static final int IMG_BACKGROUND 	= 2;
	public static final int IMG_PICTURE 	= 3;
	public static final int IMG_SYSTEM 		= 4;
	public static final int IMG_ICON 		= 5;
	public static final int IMG_AUTOTILE	= 6;
	
	public static final int AUD_BGM 		= 11;
	public static final int AUD_SND 		= 12;
	
	private final static HashMap<String, Resource> cache = new HashMap<String, Resource>();
	private static BufferedImage DEFAULT_ICON;
	
	/**
	 * Leert den Resource-Cache.
	 */
	public static void clearCache()
	{
		cache.clear();
	}
	
    private static void addToCache(String key, String name, Resource res)
    {
    	if (res == null) throw new NullPointerException();
    	cache.put(key + '/' + name, res);
    }
    
    /**
     * Gibt eine Resource aus dem Cache zurück.
     * Im Unterschied zu getResource() wird hier nicht geprüft ob die Argumente 
	 * @param key Schlüssel für die Ressourcenart.
	 * @param name Ressourcen-Pfad.
     * @return Die Ressource aus dem Cache wenn vorhanden, andernfalls <code>null</code>.
     */
    public static Resource getFromCache(String key, String name)
    {
    	return cache.get(key + '/' + name);
    }
    
//    public static Resource getResource(String key, String name, boolean )
//    {
//    	
//    }

	/**
	 * Gibt eine Ressource zurück ohne die dazugehörige Datei zu laden.
	 * Das ist hilfreich, wenn der komplette Pfad analysiert werden soll.
	 * @param key Schlüssel für die Ressourcenart.
	 * @param name Ressourcen-Pfad.
	 * @return Resource-Objekt.
	 * @throws NullPointerException Wenn entweder key oder name <code>null</code> ist.
	 */
	public static Resource getResource(String key, String name) throws LoadingException
	{
		if (name == null || name == "") return null;
		String pathName = getResourcePath(key);
		if (pathName == null) throw new NullPointerException("name is null!");
		
		Resource ress = getFromCache(pathName, name);

		if (ress == null)
		{
			java.nio.file.Path fullPath = Paths.get(Game.getRootPath(), pathName, name);
			if (Files.exists(fullPath))
			{
				ress = new Resource(key, fullPath);
			}
			if (ress != null)
			{
//				System.out.println("[ResourceManager] neue Ressource hinzugefügt: " + ress.path);
				addToCache(pathName, name, ress);
			}
			else
			{
				throw new LoadingException(name);
//				Logger.log(ResourceManager.class, Logger.ERROR, new LoadingException(name));
			}
		}
		else
		{
//			System.out.println("[ResourceManager] Ressource im Cache vorhanden.");
		}
		return ress;
	}
	
	public static boolean resourceExists(String key, String name)
	{
		if (name == null || name == "") return false;
		String pathName = getResourcePath(key);
		if (pathName == null) return false;
		
		java.nio.file.Path fullPath = Paths.get(Game.getRootPath(), pathName, name);
		return Files.exists(fullPath);
	}
	
	/**
	 * Gibt den vollständigen Pfad zu einer Ressource zurück.
	 * @param key Schlüssel für die Ressourcenart.
	 * @return Vollständigen Pfad.
	 */
    public static String getResourcePath(String key)
    {
    	if (key == null) throw new NullPointerException("key is null!");
    	
    	String path = System.getProperty(key);
    	if (path == null)
    		path = Game.getIni().get(RESOURCE_SECTION, key);
    	return path;
    }
	
	/**
	 * Ladet ein Bild aus der Jar oder einer lokalen Datei im Arbeitsverzeichnis.
	 * @param fileName vollständigen Pfad zu einer Ressource.
	 * @return geladenes Image
	 * @throws LoadingException wenn das Bild nicht geladen werden konnte.
	 */
	public static BufferedImage loadImageFromJar(String fileName) throws LoadingException
	{
		BufferedImage image = null;
		try
		{
			URL url = Game.class.getResource('/' + fileName);
			if (url != null)
				image = ImageIO.read(url);
			else
				image = ImageIO.read(new File(fileName));
		}
		catch (IOException | IllegalArgumentException e)
		{
			throw new LoadingException(fileName, e);
		}
		return image;
	}
	
	/**
	 * Ladet ein Bild.
	 * @param fileName Name der Datei.
	 * @return geladenes Image
	 * @throws LoadingException wenn das Bild nicht geladen werden konnte.
	 */
	public static BufferedImage loadImage(String fileName) throws LoadingException
	{
		Resource res = getResource(KEY_GRAPHICS, fileName);
		if (res == null)
		{
			if (DEFAULT_ICON == null)
			{
				DEFAULT_ICON = loadImageFromJar("CE_Icon32.png");
			}
			return DEFAULT_ICON;
		}
		else
		{
			BufferedImage image;
			if (res.data != null)
			{
				image = (BufferedImage) res.data;
			}
			else
			{
				image = loadImageFromJar(res.path.toString());
				res.data = image;
			}
			return image;
		}
	}
	

//	public static Resource getResource(String name, int type)
//	{
//		if (name == null || name == "") return null;
//		
//		Resource ress = cache.get(name + type);
//		if (ress == null)
//		{
//			String fullPath = getResourcePath(name, getSubPathByType(type));
//			// erstelle Affe(Resource) mit Banane(Pfad) und Regenwald(Image) ;-)
//			ress = new Resource(fullPath, type);
//		}
//		return cache.get(name + type);
//	}
	
//	public static Image loadUnbufferedImage(String fileName, int type)
//	{
//		return Toolkit.getDefaultToolkit().getImage(getResourcePath(fileName, type));
//	}
	
//	private static String getSubPathByType(int type)
//	{
//		switch(type)
//		{
//			case IMG_TILESET:	 return PATH_TILESETS;
//			case IMG_CHARSET:	 return PATH_CHARSETS;
//			case IMG_BACKGROUND: return PATH_BACKGROUNDS;
//			case IMG_PICTURE:	 return PATH_PICTURES;
//			case IMG_SYSTEM:	 return PATH_SYSTEM;
//			case IMG_ICON:	 	 return PATH_ICONS;
//			case IMG_AUTOTILE:  return PATH_AUTOTILES;
//			case AUD_BGM:		 return PATH_BGM;
//			case AUD_SND:		 return PATH_SND;
//			default:
//				throw new IllegalArgumentException("Icon-Type: " + type);
//		}
//	}
	
	/**
	 * Gibt eine Liste von Datei-Pfaden eines bestimmten Image-Types an,
	 * welche für den RessourcenManager zugänglich sind.
	 * @param key Typ-Einschränkung
	 * @return Resourcen-Liste
	 */
	public static ArrayList<Resource> getResourceList(String key)
	{
		ArrayList<Resource> list = new ArrayList<Resource>();
		
		String[] pathsName = System.getProperty(key).split(";");
		for(int i = 0; i < pathsName.length; i++)
		{
			Path path = Paths.get(pathsName[i]);
			DirectoryStream<Path> stream;
			try
			{
				stream = Files.newDirectoryStream(path);
				for(Path pathFile : stream)
				{
					String fileName = new File(pathFile.toString()).getName();
					Resource ress = getFromCache(key, fileName);
					if (ress == null)
					{
						Path fullPath = Paths.get(path.toString(), fileName);
						ress = new Resource(key, fullPath);
						ress.extern = i > 0;
						addToCache(key, fileName, ress);
					}
					if (ress != null)
					{
						list.add(ress);
					}
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return(list);
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
	 * Stellt eine Referenz zu einer Projekt-Ressource da.
	 * <p>
	 * Die Klasse kann ein Objekt aufnehmen, welches die geladene Ressource darstellt.
	 * Eine mögliche Verwendung wäre:
	 * <pre>
	 * res = ResourceManager.getResource(pfad, name);
	 * if (res == null) throw new LoadingException(name);
	 * if (res.getData() == null)
	 * 	res.setData(MeinLoader.load(new FileInputStream(res.getFile)));
	 * return res.getData();
	 * </pre>
	 * </p>
	 * @author TheWhiteShadow
	 */
	public final static class Resource
	{
		final private Path path;
		final private String type;
		private Object data;
		boolean extern;
		
		public Resource(String type, Path path)
		{
			this(type, path, null);
		}
		
		public Resource(String type, Path path, Object data)
		{
			this.type = type;
			this.path = path;
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
		
		/**
		 * Gibt den vollen Pfad der Resource zurück.
		 * Diese Methode ist nur ein Wrapper für:
		 * <pre>getPath().toFile()</pre>
		 * @return Voller Pfad der Resource.
		 * @see #getPath()
		 */
		public File getFile()
		{
			return path.toFile();
		}
		
		/**
		 * Gibt eine URL zur Ressource zurück.
		 * Diese Methode ist nur ein Wrapper für:
		 * <pre>getPath().toUri().toURL()</pre>
		 * Im Falle der dabei möglichen Ausnahme wird <code>null</code> zurück gegeben.
		 * @return URL zur Ressource.
		 */
		public URL getURL()
		{
			try
			{
				return path.toUri().toURL();
			}
			catch (MalformedURLException e)
			{
				Logger.log(ResourceManager.class, Logger.ERROR, e);
			}
			return null;
		}
		
		public String getFileName()
		{
			return path.getFileName().toString();
		}

		/**
		 * Setzt ein Objekt, welches durch diese Ressource dargestellt wird.
		 * Das gesetzte Objekt wird ebenfalls im Cache abgelegt.
		 * @param data
		 */
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
			result = prime * result + ((type == null) ? 0 : type.hashCode());
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
			if (type == null)
			{
				if (other.type != null) return false;
			}
			else if (!type.equals(other.type)) return false;
			return true;
		}

		@Override
		public String toString()
		{
			return path.toString();
		}
	}
}
