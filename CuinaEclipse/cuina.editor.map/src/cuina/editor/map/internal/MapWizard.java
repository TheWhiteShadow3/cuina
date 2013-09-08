package cuina.editor.map.internal;

import cuina.database.Database;
import cuina.database.ui.DatabaseComboViewer;
import cuina.editor.core.CuinaPlugin;
import cuina.map.Map;
import cuina.map.MapInfo;
import cuina.map.Tileset;
import cuina.resource.ResourceException;
import cuina.resource.SerializationManager;

import java.net.URI;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.ide.IDE;

public class MapWizard extends Wizard implements INewWizard
{
	public static final String ID = "cuina.editor.map.new.map";

	public static final String DEFAULT_EXTENSION = "cxm";
	private MapCreationPage creationPage;
	private Database db;
	private IWorkbench workbench;
	
	private Text inName;
	private Text inFolder;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		this.workbench = workbench;
		Object obj = selection.getFirstElement();
		IFolder selectedFolder = null;
		if(obj instanceof IFolder)
		{
			selectedFolder = (IFolder) obj;
		}
		creationPage = new MapCreationPage(selectedFolder);
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

	private class MapCreationPage extends WizardPage implements SelectionListener
	{
		private String key = "newMap";
		private String name = key;
		private int width = 20;
		private int height = 15;

		private IFolder folder;
		private DatabaseComboViewer<Tileset> combo;

		protected MapCreationPage(IFolder selectedFolder)
		{
			super("Neue Karte");
			this.folder = selectedFolder;
			setTitle("Neue Karte");
			setDescription("Erstellt eine neue Karte.");
		}

		@Override
		public void createControl(Composite parent)
		{
			Composite container = new Composite(parent, SWT.NULL);
			container.setLayout(new GridLayout(3, false));

			Label txtFolder = new Label(container, SWT.NULL);
			txtFolder.setText("Verzeichnis");

			if(folder != null)
				db = CuinaPlugin.getCuinaProject(folder.getProject()).getService(Database.class);
			
			inFolder = new Text(container, SWT.BORDER);
			inFolder.setText((folder != null) ? folder.getFullPath().toString() : "");
			inFolder.setEditable(false);
			inFolder.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

			Button cmdFolder = new Button(container, SWT.NULL);
			cmdFolder.setText("Browse...");
			cmdFolder.addSelectionListener(this);

			Label txtKey = new Label(container, SWT.NULL);
			txtKey.setText("Key");

			final Text inKey = new Text(container, SWT.BORDER);
			inKey.setText(key);
			inKey.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
			inKey.addModifyListener(new ModifyListener()
			{
				@Override
				public void modifyText(ModifyEvent e)
				{
					key = inKey.getText();
				}
			});

			Label txtName = new Label(container, SWT.NULL);
			txtName.setText("Name");

			inName = new Text(container, SWT.BORDER);
			inName.setText(name);
			inName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
			inName.addModifyListener(new ModifyListener()
			{
				@Override
				public void modifyText(ModifyEvent e)
				{
					if(key.equals(name))
					{
						inKey.setText(inName.getText());
					}
					name = inName.getText();
				}
			});

			Label tilesetName = new Label(container, SWT.NULL);
			tilesetName.setText("Tileset");
			try
			{
				combo = new DatabaseComboViewer(container, SWT.NONE);
				combo.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
				combo.setTable(db.<Tileset>loadTable("Tileset"));
			}
			catch (ResourceException e)
			{
				e.printStackTrace();
			}
			
			Label widthLabel = new Label(container, SWT.NULL);
			widthLabel.setText("Breite");

			final Spinner widthSpinner = new Spinner(container, SWT.BORDER);
			widthSpinner.setMinimum(15);
			widthSpinner.setMaximum(999);
			widthSpinner.setSelection(15);
			widthSpinner.setIncrement(1);
			widthSpinner.setPageIncrement(100);
			widthSpinner.addModifyListener(new ModifyListener()
			{
				@Override
				public void modifyText(ModifyEvent e)
				{
					width = widthSpinner.getDigits();
				}
			});

			Label heightLabel = new Label(container, SWT.NULL);
			heightLabel.setText("Höhe");

			final Spinner heightSpinner = new Spinner(container, SWT.BORDER);
			heightSpinner.setMinimum(20);
			heightSpinner.setMaximum(999);
			heightSpinner.setSelection(20);
			heightSpinner.setIncrement(1);
			heightSpinner.setPageIncrement(100);
			heightSpinner.addModifyListener(new ModifyListener()
			{
				@Override
				public void modifyText(ModifyEvent e)
				{
					height = heightSpinner.getDigits();
				}
			});

			setControl(container);
		}

		public void createMap() throws ResourceException
		{
			if(db == null) return;

			Map newMap = new Map(key, width, height);
			newMap.tilesetKey = combo.getSelectedElement().getTilesetName();

			IPath path = new Path(folder + key + "." + DEFAULT_EXTENSION);
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			SerializationManager.save(newMap, file);
			db.loadTable("MapInfo").put(new MapInfo(key, name));

			try
			{
				IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
				IDE.openEditor(page, file, true);
			}
			catch(PartInitException e)
			{
				e.printStackTrace();
			}
			setPageComplete(true);
		}
		
		@Override
		public void widgetSelected(SelectionEvent e)
		{
			ContainerSelectionDialog chooser = new ContainerSelectionDialog(
					getShell(), folder, false, "Wähle ein Verzeichnis");
			chooser.showClosedProjects(false);

			if(chooser.open() == ContainerSelectionDialog.OK)
			{
				Object[] results = chooser.getResult();
				if(results.length == 1)
				{
					if(results[0] instanceof IPath)
					{
						IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
						URI uri = root.findMember((IPath)results[0]).getLocationURI();

						IContainer[] folders = root.findContainersForLocationURI(uri);
						if(folders != null && folders.length > 0)
						{
							folder = (IFolder) folders[0];
							inFolder.setText((folder != null) ? folder.getFullPath().toString() : "");
							db = CuinaPlugin.getCuinaProject(folder.getProject()).getService(Database.class);
						}
					}
				}
			}
			
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e)
		{
			widgetSelected(e);
		}
	}
}
