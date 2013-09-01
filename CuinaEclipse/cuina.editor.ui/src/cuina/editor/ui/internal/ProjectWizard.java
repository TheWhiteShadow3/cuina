package cuina.editor.ui.internal;

import cuina.editor.core.CuinaPlugin;
import cuina.editor.core.CuinaProject;
import cuina.resource.ResourceProvider;
import cuina.util.Ini;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

public class ProjectWizard extends Wizard implements INewWizard
{
	public static final String ID = "cuina.editor.ui.new.project";

	CuinaNewProjectCreationPage page;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		page = new CuinaNewProjectCreationPage("Cuina Project Wizard");
		page.setTitle("Cuina Project");
		page.setDescription("Create new Cuina Project from scratch.");
	}

	@Override
	public void addPages()
	{
		addPage(page);
	}

	@Override
	public boolean performFinish()
	{
		try
		{
			return page.finish();
		}
		catch (CoreException | IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	class CuinaNewProjectCreationPage extends WizardNewProjectCreationPage
	{
		public CuinaNewProjectCreationPage(String pageName)
		{
			super(pageName);
		}

		public boolean finish() throws CoreException, IOException
		{
			CuinaProject cuinaProject = CuinaPlugin.getCuinaProject(getProjectHandle()); 

			if (cuinaProject.getProject().exists()) return false;

			cuinaProject.create(null);

			ResourceProvider rp = cuinaProject.getService(ResourceProvider.class);
			rp.createFileStructure();
			Ini ini = rp.getIni();
			ini.set("Game", "Title", cuinaProject.getName());
			ini.set("Game", "Start-Scene", null);
			ini.set("Game", "Main-Script", null);
			ini.write();

			return true;
		}

	}

}
