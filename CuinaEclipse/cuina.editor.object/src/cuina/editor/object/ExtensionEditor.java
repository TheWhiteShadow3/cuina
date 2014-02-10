package cuina.editor.object;


import org.eclipse.swt.widgets.Composite;

import cuina.editor.core.CuinaProject;

public abstract class ExtensionEditor
{
	private IExtensionContext context;
	
	public Object getExtension(String key)
	{
		return getObjectAdapter().getObject().extensions.get(key);
	}

	public void setExtension(String key, Object element)
	{
		getObjectAdapter().getObject().extensions.put(key, element);
	}
	
	protected ObjectAdapter getObjectAdapter()
	{
		return context.getObjectAdapter();
	}
	
	protected CuinaProject getCuinaProject()
	{
		return getObjectAdapter().getProject();
	}
	
	/**
	 * Diese Methode wird vom Framework aufgerufen und sollte daher nicht manuell aufgerufen werden.
	 * @param context Der Editor-Kontext.
	 */
	public void init(IExtensionContext context)
	{
		this.context = context;
	}
	
	public void fireDataChanged()
	{
		context.fireDataChanged();
	}
	
	public void setErrorMessage(String message)
	{
		context.setErrorMessage( message);
	}
	
	/**
	 * Diese Methode wird vom Framework aufgerufen und sollte daher nicht manuell aufgerufen werden.
	 * @param parent Der Container.
	 */
	public abstract void createComponents(Composite parent);
	public boolean performOk() { return true; };
	public void dispose() {}
}
