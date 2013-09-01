package cuina.database;

import cuina.Logger;
import cuina.graphics.ImageSet;
import cuina.plugin.PluginManager;
import cuina.util.CuinaClassLoader;
import cuina.util.LoadingException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class Database
{
	public static final String CUINA_DATABASEPATH_KEY 	= "cuina.database.path";
	
	private static HashMap<String, DataTable<?>> data = new HashMap<String, DataTable<?>>();
	private static XStream xstream;
	private static File dataDirectory;
	
	private Database() {}
	
	
	/**
	 * Gibt den absoluten Pfad zum Datenbankordner zurück.
	 * @return Den absoluten Pfad zum Datenbankordner.
	 */
	public static File getDataDirectory()
	{
		return dataDirectory;
	}
	
	/**
	 * Ladet die gloablen Datenbaken für das Spiel von serialisierten Objekten- oder XML-Dateien.
	 * <p>XML-Dateien müssen im {@link XStream}-Format vorliegen.</p>
	 */
	public static void loadDatabases(File rootFile)
	{
		Database.dataDirectory = rootFile;
		Logger.log(PluginManager.class, Logger.DEBUG, "Databasedirectory: " + rootFile.getAbsolutePath());
		
		File[] files = rootFile.listFiles();
		if (files == null) return;
		
		for (File file : files)
		{
			if (file.isFile() && !file.getName().startsWith("meta"))
			{
				DataTable<?> table = (DataTable<?>)loadData(file);
				if (table == null) continue;
				Logger.log(Database.class, Logger.DEBUG, "load table: " + table.getElementClass().getSimpleName());
				data.put(getDatabaseName(file), table);
			}
		}
		Logger.log(Database.class, Logger.INFO, data.size() + " tables loaded.");
	}
	
	/**
	 * Gibt die XStream-Instanz zurück, die zum laden von Datenbanken benutzt wird.
	 * @return
	 */
	public static XStream getXMLSerilizer()
	{
		if (xstream == null)
		{
			xstream = new XStream(null, new XppDriver(), CuinaClassLoader.getInstance());
			xstream.registerConverter(new DataTableConverter());
			xstream.setMode(XStream.ID_REFERENCES);
		}
		return xstream;
	}
	
	/**
	 * Gibt die Dateierweiterung eines übergebenen File-Objekts an.
	 * Wenn es sich um ein Verzeichnis handelt, oder die Datei keine Endung hat wird ein Leer-String zurückgegeben.
	 * @param file File-Objekt mit Pfad zur Datei.
	 * @return Dateierweiterung ohne Punkt.
	 * @throws NullPointerException wenn das übergebene File-Objekt null ist.
	 */
	public static String getExtension(File file)
	{
		int lastDot = file.getPath().lastIndexOf('.');
		if (lastDot < 1) return ""; // ignoriert führenden Punkt bei unix-Dateien
		return file.getPath().substring(lastDot + 1);
	}
	
	public static Object loadData(File file)
	{
		try (FileInputStream stream = new FileInputStream(file))
		{
			String ext = getExtension(file);
			if ("xml".equals(ext) || "cxd".equals(ext) || "cxm".equals(ext))
			{
				return getXMLSerilizer().fromXML(stream);
			}
			else// if ("ser".equals(ext))
			{
				ObjectInputStream in = new ObjectInputStream(stream);
				Object obj = in.readObject();
				return obj;
			}
		}
		catch (Exception | InstantiationError e)
		{
			Logger.log(ImageSet.class, Logger.WARNING, new LoadingException(file, e));
		}
		return null;
	}
	
	public static void saveData(File file, Object obj)
	{
		try (FileOutputStream stream = new FileOutputStream(file))
		{
			String ext = getExtension(file);
			if ("xml".equals(ext) || "cxd".equals(ext) || "cxm".equals(ext))
			{
				getXMLSerilizer().toXML(obj, stream);
			}
			else
			{
				ObjectOutputStream out = new ObjectOutputStream(stream);
				out.writeObject(obj);
			}
		}
		catch (Exception e)
		{
			Logger.log(ImageSet.class, Logger.ERROR, e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends DatabaseObject> T get(String dbName, String key)
	{
		DataTable<T> db = (DataTable<T>)data.get(dbName);
		if(db != null && key != null)
		{
			return db.get(key);
		}
		Logger.log(ImageSet.class, Logger.ERROR, "Spieldaten " + dbName + ": " + key + " nicht gefunden!");
		return null;
	}
	
	public static HashMap<String, DataTable<?>> getTables()
	{
		return data;
	}
	
	public static <T extends DatabaseObject> DataTable<T> getDataTable(String dbName)
	{
		return (DataTable<T>)data.get(dbName);
	}
	
//	/**
//	 * Gibt die Länge des Datenbank-Arrays zurück, einschließlich dem 0-Element.
//	 * @param name Datenbank-Name.
//	 * @return Arraylänge.
//	 */
//	public static int getLenght(String name)
//	{
//		if(data.get(name) != null)
//		{
//			return Array.getLength(data.get(name));
//		}
//		return -1;
//	}
	
	private static String getDatabaseName(File file)
	{
		String name = file.getName();
		int dot = name.lastIndexOf('.');
		if (dot > 0)
			return name.substring(0, dot);
		else
			return name;
	}
}
