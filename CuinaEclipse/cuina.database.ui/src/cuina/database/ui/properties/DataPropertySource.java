package cuina.database.ui.properties;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class DataPropertySource implements IPropertySource
{
	private Object obj;
	
	private HashMap<String, PropertyAttribut> attributs = new HashMap<String, PropertyAttribut>();
	private IPropertyDescriptor[] descriptors;
	
	public DataPropertySource(Object obj)
	{
		this.obj = obj;
		init();
	}
	
	private void init()
	{
		if (obj == null)
			descriptors = new IPropertyDescriptor[0];
		else try
		{
			Class c = obj.getClass();
	
			for (Field field : c.getDeclaredFields())
			{
				Property p = field.getAnnotation(Property.class);
				if (p != null) 
					attributs.put(field.getName(), new PropertyAttribut(field, p));
			}
			
			this.descriptors = new IPropertyDescriptor[attributs.size()];
			int i = 0;
			for (String name : attributs.keySet())
			{
				descriptors[i++] = new PropertyDescriptor(name, attributs.get(name).getName());
			}
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public Object getEditableValue()
	{
		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		return descriptors;
	}

	@Override
	public Object getPropertyValue(Object id)
	{
		return attributs.get(id).getValue(obj);
	}

	@Override
	public boolean isPropertySet(Object id)
	{
		return false;
	}

	@Override
	public void resetPropertyValue(Object id)
	{
	}

	@Override
	public void setPropertyValue(Object id, Object value)
	{
		attributs.get(id).setValue(obj, value);
	}
}

class PropertyAttribut
{
	private Field field;
	private String name;
	private Method getter;
	private Method setter;
	private Class type;
	
	public PropertyAttribut(Field field, Property propery) throws NoSuchMethodException, SecurityException
	{
		field.setAccessible(true);
		this.field = field;
		this.type = field.getType();
		this.name = propery.name();
		
		if (propery.getter().length() > 0)
		{
			this.getter = field.getDeclaringClass().getMethod(propery.getter(), new Class[0]);
			if (getter.getReturnType() != type)
				throw new IllegalArgumentException("Return-Type from Property-Getter does not match Field-Type.");
		}
		
		if (propery.setter().length() > 0)
		{
			this.setter = field.getDeclaringClass().getMethod(propery.setter(), new Class[] {type});
		}
	}
	
	public String getName()
	{
		return name;
	}
	
	public Class getType()
	{
		return type;
	}
	
	public Object getValue(Object obj)
	{
		try
		{
			if (getter != null)
				return getter.invoke(obj);
			else
				return field.get(obj);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	public void setValue(Object obj, Object value)
	{
		try
		{
			if (setter != null)
				setter.invoke(obj, value);
			else
				field.set(obj, value);
		}
		catch (Exception e)
		{
		}
	}
}
