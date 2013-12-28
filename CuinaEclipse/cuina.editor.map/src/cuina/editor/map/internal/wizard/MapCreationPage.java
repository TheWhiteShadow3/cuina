package cuina.editor.map.internal.wizard;

import cuina.database.DataTable;
import cuina.database.Database;
import cuina.database.ui.DatabaseComboViewer;
import cuina.editor.core.CuinaCore;
import cuina.editor.core.CuinaProject;
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
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.ide.IDE;

public class MapCreationPage extends WizardPage
{
	private String key = "newMap";
	private String name = key;
	private int width = 20;
	private int height = 15;

	private IFolder folder;
	private CuinaProject cuinaProject;
	private Database db;
	
	private DatabaseComboViewer<Tileset> combo;
	private Text inKey;
	private Text inName;
	private Text inFolder;
	private Spinner widthSpinner;
	private Spinner heightSpinner;
	private Button cmdFolder;

	public MapCreationPage(IFolder selectedFolder)
	{
		super("Neue Karte");
		this.folder = selectedFolder;
		setTitle("Neue Karte");
		setDescription("Erstellt eine neue Karte.");
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent)
	{
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(3, false));

		Handler handler = new Handler();
		
		Label txtFolder = new Label(container, SWT.NULL);
		txtFolder.setText("Verzeichnis");
		
		inFolder = new Text(container, SWT.BORDER);
		inFolder.setText((folder != null) ? folder.getFullPath().toString() : "");
		inFolder.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		inFolder.setEditable(false);

		cmdFolder = new Button(container, SWT.NULL);
		cmdFolder.setText("Browse...");
		cmdFolder.addListener(SWT.Selection, handler);

		Label txtName = new Label(container, SWT.NULL);
		txtName.setText("Name");

		inName = new Text(container, SWT.BORDER);
		inName.setText(name);
		inName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		inName.addListener(SWT.Modify, handler);
		
		Label txtKey = new Label(container, SWT.NULL);
		txtKey.setText("Schlüssel");

		inKey = new Text(container, SWT.BORDER);
		inKey.setText(key);
		inKey.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		inKey.addListener(SWT.Modify, handler);

		Label tilesetName = new Label(container, SWT.NULL);
		tilesetName.setText("Tileset");
		combo = new DatabaseComboViewer(container, SWT.NONE);
		combo.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		combo.getCombo().addListener(SWT.Selection, handler);
		
		Label widthLabel = new Label(container, SWT.NULL);
		widthLabel.setText("Breite");

		widthSpinner = new Spinner(container, SWT.BORDER);
		widthSpinner.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 2, 1));
		widthSpinner.setMinimum(15);
		widthSpinner.setMaximum(999);
		widthSpinner.setSelection(15);
		widthSpinner.setIncrement(1);
		widthSpinner.setPageIncrement(100);
		widthSpinner.addListener(SWT.Modify, handler);

		Label heightLabel = new Label(container, SWT.NULL);
		heightLabel.setText("Höhe");

		heightSpinner = new Spinner(container, SWT.BORDER);
		heightSpinner.setMinimum(20);
		heightSpinner.setMaximum(999);
		heightSpinner.setSelection(20);
		heightSpinner.setIncrement(1);
		heightSpinner.setPageIncrement(100);
		heightSpinner.addListener(SWT.Modify, handler);

		setControl(container);
		update();
	}

	private void update()
	{
		boolean complete = false;
		try
		{
			if (this.folder != null)
			{
				CuinaProject newProject = CuinaCore.getCuinaProject(folder.getProject());
				if (this.cuinaProject != newProject)
				{
					this.cuinaProject = newProject;
					this.db = cuinaProject.getService(Database.class);
					combo.setTable(db.<Tileset>loadTable("Tileset"));
					combo.getControl().setEnabled(true);
				}
				
				if (name.length() > 0 && combo.getSelectedElement() != null)
					complete = true;
			}
			else
			{
				combo.setTable(null);
				combo.getControl().setEnabled(false);
			}
		}
		catch(ResourceException e)
		{
			e.printStackTrace();
		}
		setPageComplete(complete);
		getWizard().getContainer().updateButtons();
	}

	public void createMap() throws ResourceException
	{
		if (db == null) return;

		DataTable<MapInfo> mapInfoTable = db.<MapInfo>loadTable("MapInfo");
		this.key = mapInfoTable.createAviableKey(key);
		
		Map newMap = new Map(key, width, height);
		newMap.tilesetKey = combo.getSelectedElement().getKey();

		IFile file = folder.getFile(key + "." + MapWizard.DEFAULT_EXTENSION);
//		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		SerializationManager.save(newMap, file);
		mapInfoTable.put(new MapInfo(key, name));

		try
		{
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IDE.openEditor(page, file, true);
		}
		catch(PartInitException e)
		{
			e.printStackTrace();
		}
	}
	
	private class Handler implements Listener
	{
		@Override
		public void handleEvent(Event event)
		{
			if (event.widget == inName)
			{
				if(key.equals(name))
				{
					inKey.setText(Database.getValidKey(inName.getText()));
				}
				name = inName.getText();
			}
			else if (event.widget == inKey)				key = inKey.getText();
			else if (event.widget == widthSpinner)		width = widthSpinner.getSelection();
			else if (event.widget == heightSpinner)		height = heightSpinner.getSelection();
			else if (event.widget == cmdFolder)			selectFolder();
			else if (event.widget == combo.getCombo())	update();
		}
	}

	private void selectFolder()
	{
		ContainerSelectionDialog chooser = new ContainerSelectionDialog(
				getShell(), folder, false, "Wähle ein Verzeichnis");
		chooser.showClosedProjects(false);
		if (chooser.open() != Window.OK) return;
		
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
					update();
				}
			}
		}
	}
}