package cuina.editor.object;

import cuina.database.DataTable;
import cuina.database.DatabasePlugin;
import cuina.editor.core.CuinaProject;
import cuina.editor.object.internal.properties.ObjectPropertySource;
import cuina.object.ObjectData;
import cuina.object.ObjectTemplate;
import cuina.resource.ResourceException;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * Kapselt eine Instanz von ObjectData um diverse Schnittstellen zu bedienen.
 * Adaptiert werden auch alle Objekt-Erweiterungen und die Vorlage.
 * Kann die Adapter-Klasse nicht direkt bedient werden, wird der Adapter-Manager herangezogen.
 * @author TheWhiteShadow
 */
public class ObjectAdapter implements IAdaptable
{
	private CuinaProject project;
	private ObjectTemplate template;
	private ObjectData psysicalObject;
	private ObjectData object;
	
	public ObjectAdapter(CuinaProject project, ObjectTemplate template)
	{
		this.project = project;
		this.template = template;
		this.object = template.sourceObject;
		this.psysicalObject = null;
	}
	
	public ObjectAdapter(CuinaProject project, ObjectData object)
	{
		this.project = project;
		if (object.templateKey != null && !object.templateKey.isEmpty()) try
		{
			DataTable<ObjectTemplate> table = DatabasePlugin.getDatabase(project).<ObjectTemplate>loadTable("Template");
			this.template = table.get(object.templateKey);
		}
		catch (ResourceException e)
		{
			e.printStackTrace();
		}
		
		this.object = template != null ? template.sourceObject : object;
		this.psysicalObject = object;
	}

	/**
	 * Gibt das Projekt zurück, zudem das Objekt gehört.
	 * @return das Projekt.
	 */
	public CuinaProject getProject()
	{
		return project;
	}
	
	/**
	 * Gibt das Physikalische Objekt zurück falls vorhanden.
	 * Wenn der Adapter nur eine Vorlage besitzt ist der Rückgabewert <code>null</code>.
	 * @return das Physikalische Objekt zurück oder <code>null</code>.
	 * @see #getObject()
	 */
	public ObjectData getPhysicalObject()
	{
		return psysicalObject;
	}

	/**
	 * Gibt das Referenz-Objekt zurück, welches für das Bereitstellen der Daten benutzt wird.
	 * Das Objekt ist niemals <code>null</code>, kann aber vom tatsächlichen physischen Objekt abweichen.
	 * Wenn der Adapter ein Objekt besitzt, das keine Vorlage hat, gilt:
	 * <pre>
	 * getObject() == getPhysicalObject()
	 * </pre>
	 * Bei einer existierenden Vorlate gilt:
	 * <pre>
	 * getObject() == getTemplate().sourceObject
	 * </pre>
	 * @return das Referenz-Objekt.
	 * @see #getPhysicalObject()
	 */
	public ObjectData getObject()
	{
		return object;
	}
	
	/**
	 * Gibt die Erweiterung mit dem angegebenen Schlüssel zurück.
	 * @param key Schlüssel
	 * @return die Erweiterung wenn vorhanden, andernfalls <code>null</code>.
	 */
	public Object getExtension(String key)
	{
		return object.extensions.get(key);
	}
	
	/**
	 * Gibt das Template zum Objekt zurück falls vorhanden.
	 * @return das Template zum Objekt, oder <code>null</code>.
	 */
	public ObjectTemplate getTemplate()
	{
		return template;
	}

	@Override
	public Object getAdapter(Class adapter)
	{
		if (adapter == ObjectData.class)
			return object;
		else if (adapter == ObjectTemplate.class)
			return template;
		else if (adapter == IPropertySource.class)
			return new ObjectPropertySource(object);
		else
		{
			for (Object obj : object.extensions.values())
			{
				if (obj.getClass() == adapter) return obj;
			}
		}
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
}
