package cuina.editor.object.internal.properties;

import cuina.editor.ui.NumberPropertyDescriptor;
import cuina.object.ObjectData;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class ObjectPropertySource implements IPropertySource
{
	private static enum Attribut
	{
		OBJECT_ID,
		OBJECT_NAME,
		OBJECT_TEMPLATE,
		OBJECT_X,
		OBJECT_Y,
		OBJECT_Z;
	}
	
	private final ObjectData object;
	
	public ObjectPropertySource(ObjectData object)
	{
		this.object = object;
	}

	@Override
	public Object getEditableValue()
	{
		return object;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		IPropertyDescriptor[] list = new IPropertyDescriptor[6];
		
		list[0] = new PropertyDescriptor(Attribut.OBJECT_ID, "ID");
		list[1] = new TextPropertyDescriptor(Attribut.OBJECT_NAME, "Name");
		list[2] = new TextPropertyDescriptor(Attribut.OBJECT_TEMPLATE, "Template");
		list[3] = new NumberPropertyDescriptor(Attribut.OBJECT_X, "X");
		list[4] = new NumberPropertyDescriptor(Attribut.OBJECT_Y, "Y");
		list[5] = new NumberPropertyDescriptor(Attribut.OBJECT_Z, "Z");
		
		return list;
	}

	@Override
	public Object getPropertyValue(Object id)
	{
		switch((Attribut) id)
		{
			case OBJECT_ID: 		return object.id;
			case OBJECT_NAME: 		return object.name;
			case OBJECT_TEMPLATE:	return object.templateKey;
			case OBJECT_X:			return object.x;
			case OBJECT_Y:			return object.y;
			case OBJECT_Z:			return object.z;
		}
		return null;
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
		switch((Attribut) id)
		{
			case OBJECT_ID: 		break; // Nicht setzbar
			case OBJECT_NAME: 		object.name = value.toString(); break;
			case OBJECT_TEMPLATE:	object.templateKey = value.toString(); break;
			case OBJECT_X:			object.x = Integer.parseInt(value.toString()); break;
			case OBJECT_Y:			object.y = Integer.parseInt(value.toString()); break;
			case OBJECT_Z:			object.z = Integer.parseInt(value.toString()); break;
		}
	}
}
