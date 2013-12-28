package cuina.editor.ui;

import cuina.editor.core.CuinaProject;
import cuina.resource.ResourceException;
import cuina.resource.ResourceManager.Resource;
import cuina.resource.ResourceProvider;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class ResourceButton extends Composite implements Listener
{
	private CuinaProject project;
	private String type;
	private Button button;
	private String resourceName = "";

	public ResourceButton(Composite composite, CuinaProject project, String type)
	{
		super(composite, SWT.PUSH);
		setLayout(new FillLayout());
		this.project = project;
		this.type = type;
		this.button = new Button(this, SWT.PUSH);
		this.button.addListener(SWT.Selection, this);
	}
	
	/**
	 * Gibt den gesetzten Ressourcen-Namen zurück. Niemals null.
	 * @return den Namen der Ressource.
	 */
	public String getResourceName()
	{
		return resourceName;
	}
	
	/**
	 * Gibt das {@link Resource}-Objekt zurück. Wenn die Resource nicht existiert wird null zurückgegeben.
	 * @return Das <code>Resource</code>-Objekt.
	 */
	public Resource getResource()
	{
		try
		{
			return project.getService(ResourceProvider.class).getResource(type, resourceName);
		}
		catch(ResourceException e)
		{
			return null;
		}
	}
	
	public void setResourceName(String resourceName)
	{
		this.resourceName = (resourceName != null) ? resourceName : "";
		button.setText(this.resourceName);
	}

	@Override
	public void handleEvent(Event ev)
	{
		Resource res = getResource();
		
		ResourceDialog rd = new ResourceDialog(ev.widget.getDisplay().getActiveShell(), project, type, res);
		if (rd.open() == Window.OK)
		{
			if (rd.getResource() != null)
			{
				String newResourceName = rd.getResource().getName();
				if (newResourceName.equals(resourceName)) return;
				
				setResourceName(newResourceName);
			}
		
			ev.widget = this;
			ev.data = rd.getResource();
			notifyListeners(SWT.Modify, ev);
		}
	}
}