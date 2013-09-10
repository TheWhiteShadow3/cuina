package cuina.editor.debug;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class PluginBlock
{
	private CuinaTab cuinaTab;
	private Shell shell;
	private Text inPlugin;
	private Button cmdPlugin;
	private PluginListener listener;
	private String enginePath;
	private String pluginPath;
	private Set<String> plugins;
	private Table table;
	private CheckboxTableViewer pluginTable;
	private Button cmdSelectAll;
	private Button cmdSelectNone;
	private ILaunchConfiguration originalConfig;

	public PluginBlock(CuinaTab cuinaTab)
	{
		this.cuinaTab = cuinaTab;
	}

	public void createControl(Composite parent)
	{
		shell = parent.getShell();
		listener = new PluginListener();

		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(3, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		group.setText("Plugins");

		createPluginSelection(group);
	}

	private void createPluginSelection(Composite parent)
	{
		new Label(parent, SWT.NONE).setText("Pfad:");

		inPlugin = new Text(parent, SWT.BORDER);
		inPlugin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		inPlugin.addModifyListener(listener);

		cmdPlugin = new Button(parent, SWT.NONE);
		cmdPlugin.setText("Browse...");
		cmdPlugin.addSelectionListener(listener);

		// XXX Debug Zeilen:
		Label label = new Label(parent, SWT.NONE);
		label.setText("DEBUG: Es werden z.Z. immer alle Plugins geladen!");
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 3));

		pluginTable = CheckboxTableViewer.newCheckList(parent, SWT.BORDER | SWT.FULL_SELECTION);
		pluginTable.setContentProvider(new PluginContentProvider());
		pluginTable.addCheckStateListener(listener);
		table = pluginTable.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 3));

		cmdSelectAll = new Button(parent, SWT.NONE);
		cmdSelectAll.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		cmdSelectAll.setText("Alle auswählen");
		cmdSelectNone = new Button(parent, SWT.NONE);
		cmdSelectNone.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		cmdSelectNone.setText("Alle abwählen");
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy config)
	{
		try
		{
			String path = config.getAttribute(CuinaLaunch.PLUGIN_PATH, "plugins");
			this.pluginPath = enginePath + File.separatorChar + path;
			config.setAttribute(CuinaLaunch.PLUGIN_PATH, pluginPath);
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
		
		plugins = new HashSet<String>();
		config.setAttribute(CuinaLaunch.PLUGIN_LIST, plugins);
	}

	public void initializeFrom(ILaunchConfiguration config) throws CoreException
	{
		enginePath = config.getAttribute(CuinaLaunch.ENGINE_PATH, "");
		pluginPath = config.getAttribute(CuinaLaunch.PLUGIN_PATH, "");
		plugins = config.getAttribute(CuinaLaunch.PLUGIN_LIST, new HashSet<String>());
		inPlugin.setText(pluginPath.toString());
		pluginTable.setCheckedElements(plugins.toArray());
		originalConfig = config;
	}

	public void performApply(ILaunchConfigurationWorkingCopy config)
	{
		config.setAttribute(CuinaLaunch.PLUGIN_PATH, inPlugin.getText());
		config.setAttribute(CuinaLaunch.PLUGIN_LIST, plugins);
		// Workaround wegen LaunchConfiguration Bug.
		config.setAttribute(CuinaLaunch.PLUGIN_MAGIC, plugins.hashCode());
	}

	private void findPlugins(File dir)
	{
		if (dir.exists())
		{
			String[] pluginNames = dir.list(new FilenameFilter()
			{
				@Override
				public boolean accept(File dir, String name)
				{
					return name.endsWith(".jar");
				}
			});
			pluginTable.setInput(pluginNames);
		}
		else
			pluginTable.setInput(null);
		if (originalConfig != null) try
		{
			if (dir.toString().equals(originalConfig.getAttribute(CuinaLaunch.PLUGIN_PATH, (String) null)))
			{
				plugins = originalConfig.getAttribute(CuinaLaunch.PLUGIN_LIST, new HashSet<String>());
				pluginTable.setCheckedElements(plugins.toArray());
				return;
			}
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	private File choosePluginPath()
	{
		DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN);
		dialog.setText("Plugin-Ordner Auswahl");

		String result = dialog.open();
		if (result != null)
		{
			File file = new File(result);
			if (file.exists()) return file;
		}
		return null;
	}

	private class PluginContentProvider implements IStructuredContentProvider
	{
		@Override
		public void dispose()
		{}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{}

		@Override
		public Object[] getElements(Object data)
		{
			Object[] elements = new Object[((String[]) data).length];
			System.arraycopy(data, 0, elements, 0, elements.length);
			return elements;
		}
	}

	private class PluginListener implements ModifyListener, SelectionListener, ICheckStateListener
	{
		@Override
		public void modifyText(ModifyEvent e)
		{
			IPath path = new Path(inPlugin.getText());
			if (!path.isAbsolute())
				pluginPath = enginePath + File.separatorChar + inPlugin.getText();
			else
				pluginPath = path.toOSString();
			findPlugins(new File(pluginPath));
			cuinaTab.updateTab();
		}

		@Override
		public void widgetSelected(SelectionEvent e)
		{
			if (e.getSource() == cmdPlugin)
			{
				pluginPath = choosePluginPath().toString();
				if (pluginPath != null)
				{
					inPlugin.setText(pluginPath.toString());
				}
			}
		}

		@Override
		public void checkStateChanged(CheckStateChangedEvent e)
		{
			if (e.getChecked())
				plugins.add((String) e.getElement());
			else
				plugins.remove(e.getElement());
			cuinaTab.updateTab();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e)
		{}
	}

	public String validate()
	{
		if (!inPlugin.getText().isEmpty() && !new File(inPlugin.getText()).exists())
			return "Plugin-Pfad nicht gefunden!";
		return null;
	}
}
