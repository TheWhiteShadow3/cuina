package cuina.editor.object;

import cuina.editor.core.CuinaProject;
import cuina.object.ObjectData;
import cuina.object.ObjectTemplate;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * Die ObjectPropertyPage wird für ObjectData und ObjectTemplate -Instanzen benutzt.
 * @author TheWhiteShadow
 */
public abstract class ObjectPropertyPage extends PropertyPage
{
	private ObjectData object;
	private ObjectTemplate template;
	
	@Override
	public void setElement(IAdaptable element)
	{
		super.setElement(element);
		
		if (element instanceof ObjectAdapter)
		{
			ObjectAdapter adapter = (ObjectAdapter) element;
			object = adapter.getPhysicalObject();
			template = adapter.getTemplate();
		}
		else
		{
			throw new IllegalArgumentException("element must be an ObjectAdapter");
		}
	}
	
	public ObjectAdapter getObjectAdapter()
	{
		return (ObjectAdapter) getElement();
	}
	
	public CuinaProject getProject()
	{
		return getObjectAdapter().getProject();
	}
	
//	/**
//	 * Gibt das zugrunde liegende Objekt zurück.
//	 * Das Ergebnis ist niemals null.
//	 * @return das Objekt.
//	 */
//	protected ObjectData getSourceObject()
//	{
//		return template != null ? template.sourceObject : object;
//	}
//	
//	/**
//	 * Gibt das tatsächliche Objekt zurück, wenn vorhanden.
//	 * <p>
//	 * Wenn es sich um ein Template handelt ist der Rückgabewert <code>null</code>.
//	 * Ob es ein Template ist, kann mit {@link #isTemplate()} geprüft werde.
//	 * </p>
//	 * @return das Objekt
//	 */
	protected ObjectData getObject()
	{
		return template != null ? template.sourceObject : object;
	}

	protected boolean isTemplate()
	{
		return object == null;
	}
	
	/**
	 * Gibt das Template zurück.
	 * @return
	 */
	protected ObjectTemplate getTemplate()
	{
		return template;
	}
}
