package cuina.database.ui;

import cuina.database.DataTable;
import cuina.database.Database;
import cuina.database.DatabaseObject;
import cuina.database.DatabasePlugin;
import cuina.database.IDatabaseDescriptor;
import cuina.database.ResourceReference;
import cuina.editor.core.CuinaProject;
import cuina.resource.ResourceException;
import cuina.resource.ResourceManager.Resource;
import cuina.resource.ResourceProvider;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.eclipse.core.runtime.Assert;

public class ReferenceCounter
{
	private HashMap<String, Reference> resourceList = new HashMap<String, Reference>();

	public void clear()
	{
		resourceList.clear();
	}
	
	public void scanDatabase(Database db)
	{
		IDatabaseDescriptor[] descriptors = DatabasePlugin.getDescriptors();
		for (IDatabaseDescriptor descriptor : descriptors) try
		{
			DataTable<?> table = db.loadTable(descriptor.getName());
			if(table != null)
			{
				System.out.println("[ReferenceCounter] Scan Table " + table.getName());
				for (DatabaseObject obj : table.values()) scanObject(db, obj);					
			}
		}
		catch (ResourceException e) {}
	}
	
	public void scanObject(Database db, DatabaseObject obj)
	{
		Class<?> clazz = obj.getClass();
		
		for (Field field : clazz.getDeclaredFields())
		{
			ResourceReference annotation =  field.getAnnotation(ResourceReference.class);
			if (field.getType() == String.class && annotation != null) try
			{
				field.setAccessible(true);
				
				Object value = field.get(obj);
				if (value instanceof String)
				{
					addReference(db.getProject(), annotation.type(), (String) value);
				}
				else if (value instanceof String[])
				{
					String[] names = (String[]) value;
					for(int i = 0; i < names.length; i++)
					{
						addReference(db.getProject(), annotation.type(), names[i]);
					}
				}
				else Assert.isTrue(false); // falsch Annotiert
			}
			catch(IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void addReference(CuinaProject project, String type, String name)
	{
		Reference ref = resourceList.get(name);
		if (ref == null)
		{
			ref = new Reference(project, type, name);
			ref.exists = (ref.getResource() != null);
			resourceList.put(name, ref);
		}
		ref.count++;
	}
	
	public Reference getReference(String name)
	{
		return resourceList.get(name); 
	}
	
	public Reference[] getReferences()
	{
		return resourceList.values().toArray(new Reference[resourceList.size()]);
	}
	
	public static class Reference
	{
		private CuinaProject project;
		private String name;
		private String type;
		boolean exists;
		int count;
		
		public Reference(CuinaProject project, String type, String name)
		{
			this.project = project;
			this.name = name;
			this.type = type;
		}
		
		public String getName()
		{
			return name;
		}

		public String getType()
		{
			return type;
		}

		public int getCount()
		{
			return count;
		}

		public boolean exists()
		{
			return exists;
		}

		public Resource getResource()
		{
			try
			{
				return project.getService(ResourceProvider.class).getResource(type, name);
			}
			catch (ResourceException e)
			{
				return null;
			}
		}

		@Override
		public String toString()
		{
			String str = type + "@" + name + " (" + count + ")";
			if (!exists) str += " NOT found!";
			return str;
		}
	}
}
