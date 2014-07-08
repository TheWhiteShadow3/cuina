package cuina.database.ui.internal;

import cuina.database.Database;
import cuina.database.DatabasePlugin;
import cuina.database.IDatabaseDescriptor;
import cuina.database.ui.DatabaseTypeContentProvider;
import cuina.database.ui.DatabaseTypeLabelProvider;
import cuina.editor.core.CuinaCore;
import cuina.editor.core.CuinaProject;
import cuina.resource.ResourceException;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.navigator.IDescriptionProvider;

public class DatabaseWizard extends Wizard implements INewWizard
{
	private CuinaProject cuinaProject;
	private CuinaNewDatabasePage creationPage;
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		Object obj = selection.getFirstElement();
		if (obj instanceof IResource)
			this.cuinaProject = CuinaCore.getCuinaProject( ((IResource) obj).getProject() );
		if (cuinaProject == null) throw new NullPointerException("Project is null.");
		
		this.creationPage = new CuinaNewDatabasePage("Datenbank erstellen");
		addPage(creationPage);
	}

	@Override
	public boolean performFinish()
	{
		return creationPage.finish();
	}
	
	public class CuinaNewDatabasePage extends WizardPage implements Listener
	{
		private IDatabaseDescriptor<?>[] descriptors;
		private Database database;
		
		private CheckboxTableViewer databaseTable;
		private Button cmdSelectAll;
		private Button cmdSelectNone;
		private boolean update;
	
		protected CuinaNewDatabasePage(String pageName)
		{
			super(pageName);
			setTitle("Database");
			setDescription("Erstellt eine neue Cuina Datenbank.");
			
			this.database = cuinaProject.getService(Database.class);
			
			List<IDatabaseDescriptor> list = new ArrayList<IDatabaseDescriptor>();
			for (IDatabaseDescriptor d : DatabasePlugin.getDescriptors())
			{
				if (!database.existTable(d.getName())) list.add(d);
			}
			this.descriptors = list.toArray(new IDatabaseDescriptor[list.size()]);
		}

		public boolean finish()
		{
			Database db = cuinaProject.getService(Database.class);

			for (Object item : databaseTable.getCheckedElements())
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

			databaseTable = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
			databaseTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2));
			databaseTable.setContentProvider(new DatabaseTypeContentProvider());
			databaseTable.setLabelProvider(new DatabaseTypeLabelProvider());
			databaseTable.setInput(descriptors);
			
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
