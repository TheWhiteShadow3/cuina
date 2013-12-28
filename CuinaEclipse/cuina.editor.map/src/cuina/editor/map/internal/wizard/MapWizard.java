package cuina.editor.map.internal.wizard;

import cuina.resource.ResourceException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class MapWizard extends Wizard implements INewWizard
{
	public static final String ID = "cuina.editor.map.new.map";

	public static final String DEFAULT_EXTENSION = "cxm";
	private MapCreationPage creationPage;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		Object obj = selection.getFirstElement();
		IFolder selectedFolder = null;
		if(obj instanceof IFolder)
		{
			selectedFolder = (IFolder) obj;
		}
		creationPage = new MapCreationPage(selectedFolder);
		creationPage.setWizard(this);
	}
	
	@Override
	public void addPages()
	{
		addPage(creationPage);
	}

	@Override
	public boolean performFinish()
	{
		try
		{
			creationPage.createMap();
		}
		catch (ResourceException e)
		{
			e.printStackTrace();
			return false;
		}
		return creationPage.isPageComplete();
	}
}
