package cuina.database.ui.properties;

import java.lang.reflect.Method;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class BeanPropertyDescriptor extends PropertyDescriptor
{
	private Object bean;
	private Method setter;
	private Method getter;
	
	public BeanPropertyDescriptor(Object beanObj, String attribut, String displayName)
	{
		super(attribut, displayName);
		this.bean = beanObj;
		Class c = bean.getClass();
		try
		{
			this.getter = getGetter(c, attribut);
			this.setter = getSetter(c, attribut, getter.getReturnType());
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			e.printStackTrace();
		}
	}

	public Method getSetter()
	{
		return setter;
	}

	public Method getGetter()
	{
		return getter;
	}

	@Override
	public CellEditor createPropertyEditor(Composite parent)
	{
		Class type = getter.getReturnType();
		if (!type.isPrimitive() && type != String.class) return null;
		
		TextCellEditor editor = new TextCellEditor(parent);
		if (type == String.class)
			return editor;
		if (type == int.class || type == long.class)
			editor.setValidator(new ICellEditorValidator()
			{
				@Override
				public String isValid(Object value)
				{
					if (value == null) return "value is null!";
					if (!(value instanceof String)) return null;
					String txt = (String) value;
					try
					{
						Double.parseDouble(txt);
					}
					catch (NumberFormatException e)
					{
						return value + " is not valid!";
					}
					return null;
				}
			});
		return editor;
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
