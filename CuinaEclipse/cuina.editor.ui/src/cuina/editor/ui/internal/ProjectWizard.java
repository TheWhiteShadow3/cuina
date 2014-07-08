package cuina.editor.ui.internal;

import cuina.database.Database;
import cuina.database.DatabasePlugin;
import cuina.database.IDatabaseDescriptor;
import cuina.database.ui.DatabaseTypeContentProvider;
import cuina.database.ui.DatabaseTypeLabelProvider;
import cuina.editor.core.CuinaCore;
import cuina.editor.core.CuinaProject;
import cuina.editor.core.ProjectParameter;
import cuina.editor.core.util.Ini;
import cuina.resource.ResourceException;
import cuina.resource.ResourceProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

public class ProjectWizard extends Wizard implements INewWizard
{
	public static final String ID = "cuina.editor.ui.new.project";
	
	private CuinaNewProjectCreationPage creationPage;
	private CuinaNewProjectConfigPage configPage;
	private CuinaNewProjectDatabasePage dbPage;
	private List<ProjectParameter> parameters = new ArrayList<ProjectParameter>(16);
	private List<Text> parameterWidgets = new ArrayList<Text>(16);

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		this.creationPage = new CuinaNewProjectCreationPage("Cuina Projekt Wizard");
		this.configPage = new CuinaNewProjectConfigPage("Projekt Konfiguration");
		this.dbPage = new CuinaNewProjectDatabasePage("Datenbank initialisierung");
	}

	@Override
	public void addPages()
	{
		addPage(creationPage);
		addPage(configPage);
		addPage(dbPage);
	}

	@Override
	public boolean performFinish()
	{
		try
		{
			if (!creationPage.finish()) return false;
			if (!configPage.finish()) return false;
			if (!dbPage.finish()) return false;
			
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
			setDescription("Projekt-Parameter einstellen.");
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
					new Label(groupWidget, SWT.NONE).setText(param.getLabel() + ':');
					
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
	
	private class CuinaNewProjectDatabasePage extends WizardPage implements Listener, ICheckStateListener
	{
		private IDatabaseDescriptor<?>[] descriptors;
		private CheckboxTableViewer databaseTable;
		private Button cmdSelectAll;
		private Button cmdSelectNone;
		private boolean update;
		
		public CuinaNewProjectDatabasePage(String pageName)
		{
			super(pageName);
			setTitle("Cuina Projekt");
			setDescription("Datenbank anlegen.");
			
			this.descriptors = DatabasePlugin.getDescriptors();
		}

		public boolean finish()
		{
			CuinaProject cuinaProject = creationPage.getCuinaProject();
			Database db = cuinaProject.getService(Database.class);
			
			for(Object item : databaseTable.getCheckedElements())
			{
				IDatabaseDescriptor<?> desc = (IDatabaseDescriptor<?>) item;
				try
				{
					db.saveTable(db.createNewTable(desc));
				}
				catch (ResourceException e)
				{
					e.printStackTrace();
					setErrorMessage(e.getMessage());
					return false;
				}
			}
			
			return true;
		}

		@Override
		public void createControl(Composite parent)
		{
			Composite container = new Composite(parent, SWT.NONE);
			container.setLayout(new GridLayout(2, true));
			
			databaseTable = CheckboxTableViewer.newCheckList(container,
					SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
			databaseTable.setContentProvider(new DatabaseTypeContentProvider());
			databaseTable.setLabelProvider(new DatabaseTypeLabelProvider());
			databaseTable.setInput(descriptors);
			databaseTable.addCheckStateListener(this);
			databaseTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2));

			cmdSelectAll = new Button(container, SWT.NONE);
			cmdSelectAll.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
			cmdSelectAll.setText("Alle auswählen");
			cmdSelectAll.addListener(SWT.Selection, this);
			
			cmdSelectNone = new Button(container, SWT.NONE);
			cmdSelectNone.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
			cmdSelectNone.setText("Alle abwählen");
			cmdSelectNone.addListener(SWT.Selection, this);
			
			setControl(container);
		}
		
		@Override
		public void checkStateChanged(CheckStateChangedEvent e)
		{
			if (update) return;
			update = true;
			
//			String pluginName = ((CuinaPlugin) e.getElement()).getName();
			
//			if (e.getChecked())
//				plugins.add(pluginName);
//			else
//				plugins.remove(pluginName);
//			cuinaTab.updateTab();
			
			update = false;
		}
		
		@Override
		public void handleEvent(Event e)
		{
			if (update) return;
			update = true;
			
			if (e.widget == cmdSelectAll)
			{
				databaseTable.setCheckedElements(descriptors);
			}
			else if (e.widget == cmdSelectNone)
			{
				databaseTable.setCheckedElements(new Object[0]);
			}
			update = false;
		}
	}
}
