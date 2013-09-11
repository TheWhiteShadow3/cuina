package cuina.editor.debug;

import cuina.editor.core.CuinaProject;
import cuina.editor.core.engine.EngineReference;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class EngineBlock
{
	private CuinaTab cuinaTab;
	private Shell shell;
	private File path;
	private EngineListener listener;
	private Button opSrcHome;
	private Button opSrcPath;
	private Text inEngine;
	private Button cmdEngine;
	private Text txtHome;
	private String cuinaHome;
	
	public EngineBlock(CuinaTab cuinaTab)
	{
		this.cuinaTab = cuinaTab;
		this.cuinaHome = System.getenv(EngineReference.CUINA_SYSTEM_VARIABLE);
	}

	public void createControl(Composite parent)
	{
		shell = parent.getShell();
		listener = new EngineListener();

		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(3, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		group.setText("Engine");

		createEngineSelection(group);
	}
    
	private void createEngineSelection(Composite parent)
	{
		opSrcHome = new Button(parent, SWT.RADIO);
		opSrcHome.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		opSrcHome.setText("%CUINA_HOME%");
		opSrcHome.addSelectionListener(listener);

		txtHome = new Text(parent, SWT.NONE);
		txtHome.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		txtHome.setEditable(false);

		opSrcPath = new Button(parent, SWT.RADIO);
		opSrcPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		opSrcPath.setText("Pfad:");
		opSrcPath.addSelectionListener(listener);

		inEngine = new Text(parent, SWT.BORDER);
		inEngine.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		inEngine.addModifyListener(listener);

		cmdEngine = new Button(parent, SWT.NONE);
		cmdEngine.setText("Suchen...");
		cmdEngine.addSelectionListener(listener);
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy config) throws CoreException
	{
		config.setAttribute(CuinaLaunch.ENGINE_SOURCE, cuinaHome != null);
		config.setAttribute(CuinaLaunch.ENGINE_PATH, cuinaHome != null ? cuinaHome : (String) null);

//		txtHome.setText(getConfigEnginePath(config));
//		if (cuinaHome == null) opSrcHome.setEnabled(false);
	}

	public void initializeFrom(ILaunchConfiguration config) throws CoreException
	{
		opSrcHome.setEnabled(cuinaHome != null);
		boolean useHome = config.getAttribute(CuinaLaunch.ENGINE_SOURCE, true);
		if (useHome && cuinaHome != null)
		{
			opSrcHome.setSelection(true);
			inEngine.setEnabled(false);
			path = new File(cuinaHome);
		}
		else
		{
			opSrcPath.setSelection(true);
			inEngine.setEnabled(true);
			path = new File(config.getAttribute(CuinaLaunch.ENGINE_PATH, ""));
			inEngine.setText(path.toString());
		}

		String path = getConfigEnginePath(config);
		txtHome.setText(path != null ? path : "");
		if (cuinaHome == null) opSrcHome.setEnabled(false);
	}

	private String getConfigEnginePath(ILaunchConfiguration config)
	{
		CuinaProject project = cuinaTab.getProject();
		if (project == null) return null;
		EngineReference ref = project.getService(EngineReference.class);
	
		return ref.getEnginePath();
	}

	public void performApply(ILaunchConfigurationWorkingCopy config)
	{
		config.setAttribute(CuinaLaunch.ENGINE_SOURCE, opSrcHome.getSelection());
		config.setAttribute(CuinaLaunch.ENGINE_PATH, path.toString());
	}

	private File chooseEnginePath()
	{
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setFilterExtensions(new String[] { "*.jar; *.exe" });
		dialog.setText("Engine Auswahl");

		if (path != null)
		{
			dialog.setFileName(path.toString());
		}
		String result = dialog.open();
		if (result != null)
		{
			File file = new File(result);
			if (file.exists()) return file;
		}
		return null;
	}
 
	public String validate()
	{
		if (path == null || !path.exists()) return "Cuina-Engine nicht gefunden!";
		return null;
	}

	private class EngineListener implements ModifyListener, SelectionListener
	{
		@Override
		public void modifyText(ModifyEvent e)
		{
			path = new File(inEngine.getText());
			cuinaTab.updateTab();
		}

		@Override
		public void widgetSelected(SelectionEvent e)
		{
			if (e.widget == cmdEngine)
			{
				path = chooseEnginePath();
				if (path != null)
					inEngine.setText(path.toString());
			}
			else
			{
				if (opSrcHome.getSelection())
				{
					inEngine.setEnabled(false);
					path = new File(cuinaHome);
				}
				else
				{
					inEngine.setEnabled(true);
					path = new File(inEngine.getText());
				}
				cuinaTab.updateTab();
			}
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e)
		{}
	}
}