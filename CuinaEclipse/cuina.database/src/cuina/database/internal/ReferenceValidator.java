package cuina.database.internal;

import java.lang.reflect.Field;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import cuina.database.DataTable;
import cuina.database.Database;
import cuina.database.DatabaseDescriptor;
import cuina.database.DatabasePlugin;
import cuina.database.KeyReference;
import cuina.resource.ResourceException;

public class ReferenceValidator
{
	/**
	 * Validiert ein Objekt auf gültige Schlüssel-Referenzen.
	 * Schlüsselreferenzen werden durch die Annotation {@link KeyReference} am Feld angegeben.
	 * @param obj Objekt.
	 * @param db Datenbank-Kontext.
	 * @return <code>true</code>, wenn alle Schlüsselreferenzen gültig sind, andernfalls <code>false</code>.
	 * @throws ResourceException Wenn das Laden einer Datenbank-Tabelle fehlgeschlagen ist.
	 */
	public static boolean isValid(Object obj, Database db) throws ResourceException
	{
		Class c = obj.getClass();
		for (Field field : c.getDeclaredFields())
		{
			KeyReference ref = field.getAnnotation(KeyReference.class);
			if (ref == null) continue;
			
			try
			{
				String key = (String) field.get(obj);
				DataTable table = db.loadTable(ref.name());
				if (table.get(key) == null) return false;
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	//XXX: need tests
	/**
	 * Aktualisiert alle Referenzen eines Eintrags innerhalb der Datenbank.
	 * @param db
	 * @param tableName
	 * @param oldKey
	 * @param newKey
	 * @param monitor
	 */
	public static void updateKey(Database db, String tableName, String oldKey, String newKey, IProgressMonitor monitor)
	{
		System.out.println("Aktualisiere Referenzen von " + oldKey + " auf " + newKey);
		DatabaseDescriptor[] descriptors = DatabasePlugin.getDescriptors();
		if (monitor != null) monitor.beginTask("update References", descriptors.length);
		
		for (DatabaseDescriptor d : descriptors)
		{
//			if ( tableName.equals(d.getName()) ) continue;
			if (monitor != null)
			{
				if (monitor.isCanceled()) return;
				monitor.worked(1);
			}

			Class c = d.getDataClass();
			DataTable dependent = null;
			for (Field field : c.getDeclaredFields())
			{
				KeyReference ref = field.getAnnotation(KeyReference.class);
				if (ref == null || !tableName.equals(ref.name()) ) continue;

				try
				{
					if (dependent == null) dependent = db.loadTable(d.getName());
					for (String key : (Set<String>)dependent.keySet())
					{
						field.setAccessible(true);
						Object obj = dependent.get(key);
						String reference = (String) field.get(obj);
						if (oldKey.equals(reference))
						{
							field.set(obj, newKey);
							System.out.println("Referenz aktualisiert! " + dependent.getName() + "." + obj);
						}
					}
				}
				catch (IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
				catch (ResourceException e)
				{
					e.printStackTrace();
				}
			}
		}
		if (monitor != null) monitor.done();
	}
}
