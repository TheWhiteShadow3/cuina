package cuina.editor.ui.internal;

import cuina.editor.core.CuinaCore;
import cuina.editor.core.CuinaProject;
import cuina.editor.core.ProjectParameter;
import cuina.editor.core.util.Ini;
import cuina.resource.ResourceProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

public class ProjectWizard extends Wizard implements INewWizard
{
	public static final String ID = "cuina.editor.ui.new.project";
	
	private CuinaNewProjectCreationPage creationPage;
	private CuinaNewProjectConfigPage configPage;
	private List<ProjectParameter> parameters = new ArrayList<ProjectParameter>(16);
	private List<Text> parameterWidgets = new ArrayList<Text>(16);

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		creationPage = new CuinaNewProjectCreationPage("Cuina Projekt Wizard");
		configPage = new CuinaNewProjectConfigPage("Projekt Konfiguration");
	}

	@Override
	public void addPages()
	{
		addPage(creationPage);
		addPage(configPage);
	}

	@Override
	public boolean performFinish()
	{
		try
		{
			if (!creationPage.finish()) return false;
			if (!configPage.finish()) return false;
			
			CuinaProject cuinaProject = creationPage.getCuinaProject();
			cuinaProject.getIni().write();
			
//			ResourcesPlugin.getWorkspace().
			
			return true;
		}
		catch (CoreException | IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	private class CuinaNewProjectCreationPage extends WizardNewProjectCreationPage
	{
		private CuinaProject cuinaProject;
		
		public CuinaNewProjectCreationPage(String pageName)
		{
			super(pageName);
			setTitle("Cuina Projekt");
			setDescription("Erstellt ein neues Cuina Projekt.");
			setInitialProjectName("neues Projekt");
		}

		public boolean finish() throws CoreException, IOException
		{
			cuinaProject = CuinaCore.getCuinaProject(getProjectHandle());
			
			if (cuinaProject.getProject().exists()) return false;

			cuinaProject.create(null);

			ResourceProvider rp = cuinaProject.getService(ResourceProvider.class);
			
			ProgressMonitorDialog pmd = new ProgressMonitorDialog(getShell());
			pmd.open();
			rp.createFileStructure(pmd.getProgressMonitor());

			Ini ini = cuinaProject.getIni();
			ini.set("Game", "Title", cuinaProject.getName());
			
			return true;
		}
		
		public CuinaProject getCuinaProject()
		{
			return cuinaProject;
		}
	}
	
	private class CuinaNewProjectConfigPage extends WizardPage
	{
		public CuinaNewProjectConfigPage(String pageName)
		{
			super(pageName);
			setTitle("Cuina Projekt");
			setDescription("Erstellt ein neues Cuina Projekt.");
		}
		
		@Override
		public void createControl(Composite parent)
		{
			Composite container = new Composite(parent, SWT.NONE);
			container.setLayout(new GridLayout());
			
			String[] groups = CuinaCore.getProjectParameterGroups();
			Arrays.sort(groups);
			
			for (String group : groups)
			{
				Group groupWidget = new Group(container, SWT.NONE);
				groupWidget.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
				groupWidget.setLayout(new GridLayout(2, false));
				groupWidget.setText(group != null ? group : "default");
				
				for (ProjectParameter param : CuinaCore.getProjectParameters(group))
				{
					new Label(groupWidget, SWT.NONE).setText(param.getName() + ':');
					
					Text text = new Text(groupWidget, SWT.BORDER);
					text.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
					text.setToolTipText(param.getDescription());
					text.setText(param.getDefaultValue());
					parameters.add(param);
					parameterWidgets.add(text);
				}
			}
			
			setControl(container);
		}
		
		public boolean finish() throws CoreException, IOException
		{
			CuinaProject cuinaProject = creationPage.getCuinaProject();
			
			setIniParameter(cuinaProject);

			return true;
		}
		
		private void setIniParameter(CuinaProject cuinaProject)
		{
			for (int i = 0; i < parameters.size(); i++)
			{
				parameters.get(i).setValue(cuinaProject, parameterWidgets.get(i).getText());
			}
		}
	}
}
