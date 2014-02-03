package cuina.editor.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class BeanPropertyDescriptor extends PropertyDescriptor
{
	private Method setter;
	private Method getter;
	
	public BeanPropertyDescriptor(Class<?> beanClass, String attribut, String displayName)
			throws NoSuchMethodException, SecurityException
	{
		super(attribut, displayName);
		this.getter = getGetter(beanClass, attribut);
		this.setter = getSetter(beanClass, attribut, getter.getReturnType());
	}

	public Method getSetter()
	{
		return setter;
	}

	public Method getGetter()
	{
		return getter;
	}
	
	public Object getValue(Object object)
	{
		try
		{
			return getter.invoke(object);
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public void setValue(Object object, Object value)
	{
		try
		{
			setter.invoke(object, value);
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public CellEditor createPropertyEditor(Composite parent)
	{
		final Class type = getter.getReturnType();
		if (!type.isPrimitive() && type != String.class) return null;
		
		if (type == boolean.class) return new CheckboxCellEditor(parent);
		
		TextCellEditor editor = new TextCellEditor(parent);
		if (type == String.class) return editor;
		
		if (isNumeric(type))
		{
			editor.setValidator(new NumberValidator(type));
		}
		return editor;
	}
	
	private boolean isNumeric(Class type)
	{
		return (type == byte.class || type == short.class || type == int.class ||
				type == long.class || type == float.class || type == double.class);
	}

	private Method getSetter(Class c, String field, Class type) throws NoSuchMethodException, SecurityException
	{
		return c.getMethod("set" + getUpperName(field), type);
	}
	
	private Method getGetter(Class c, String field) throws NoSuchMethodException, SecurityException
	{
		String name = getUpperName(field);
		try
		{
			return c.getMethod("get" + name, (Class[]) null);
		}
		catch (NoSuchMethodException e)
		{
			return c.getMethod("is" + name, (Class[]) null);
		}
	}
	
	private String getUpperName(String field)
	{
		return Character.toUpperCase(field.charAt(0)) + field.substring(1);
	}
}
