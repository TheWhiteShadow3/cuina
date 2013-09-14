package cuina.editor.debug;

import cuina.editor.core.CuinaProject;
import cuina.editor.core.engine.CuinaPlugin;
import cuina.editor.core.engine.CuinaPlugin.State;
import cuina.editor.core.engine.EngineReference;
import cuina.editor.core.engine.PluginManager;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
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
	private String pluginPath;
	private Set<String> plugins;
	private Table table;
	private CheckboxTableViewer pluginTable;
	private Button cmdSelectAll;
	private Button cmdSelectNone;
	private Button cmdAddDepends;
	private EngineReference engineReference;
	private PluginManager pluginManager;
	
	public PluginBlock(CuinaTab cuinaTab)
	{
		this.cuinaTab = cuinaTab;
		this.pluginManager = new PluginManager();
	}

	public void createControl(Composite parent)
	{
		shell = parent.getShell();
		listener = new PluginListener();

		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(3, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		group.setText("Plugins");

		createPluginSelection(group);
	}

	private void createPluginSelection(Composite parent)
	{
		new Label(parent, SWT.NONE).setText("Pfad:");

		inPlugin = new Text(parent, SWT.BORDER);
		inPlugin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		inPlugin.addListener(SWT.Modify, listener);

		cmdPlugin = new Button(parent, SWT.NONE);
		cmdPlugin.setText("Suchen...");
		cmdPlugin.addListener(SWT.Selection, listener);

		pluginTable = CheckboxTableViewer.newCheckList(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		pluginTable.setContentProvider(new PluginContentProvider());
		pluginTable.setInput(pluginManager);
		pluginTable.addCheckStateListener(listener);
		table = pluginTable.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 3));

		cmdSelectAll = new Button(parent, SWT.NONE);
		cmdSelectAll.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		cmdSelectAll.setText("Alle auswählen");
		cmdSelectAll.addListener(SWT.Selection, listener);
		
		cmdSelectNone = new Button(parent, SWT.NONE);
		cmdSelectNone.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		cmdSelectNone.setText("Alle abwählen");
		cmdSelectNone.addListener(SWT.Selection, listener);
		
		cmdAddDepends = new Button(parent, SWT.NONE);
		cmdAddDepends.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		cmdAddDepends.setText("Abhängige Plugins hinzufügen");
		cmdAddDepends.addListener(SWT.Selection, listener);
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy config) throws CoreException
	{
//		config.setAttribute(CuinaLaunch.PLUGIN_PATH, "");
//		config.setAttribute(CuinaLaunch.PLUGIN_LIST, (Set<String>) null);
	}

	public void initializeFrom(ILaunchConfiguration config) throws CoreException
	{
//		enginePath = config.getAttribute(CuinaLaunch.ENGINE_PATH, "");
		pluginPath = config.getAttribute(CuinaLaunch.PLUGIN_PATH, (String) null);
		plugins = config.getAttribute(CuinaLaunch.PLUGIN_LIST, (Set<String>) null);
		
		CuinaProject project = cuinaTab.getProject();
		if (project != null)
			this.engineReference = project.getService(EngineReference.class);
		if (pluginPath == null)
		{
			pluginPath = engineReference != null ? engineReference.getPluginPath() : "";
		}
		inPlugin.setText(pluginPath.toString());
	}

	public void performApply(ILaunchConfigurationWorkingCopy config)
	{
		String pluginPath = inPlugin.getText();
		config.setAttribute(CuinaLaunch.PLUGIN_PATH, pluginPath);
		config.setAttribute(CuinaLaunch.PLUGIN_LIST, plugins);
		// Workaround wegen LaunchConfiguration Bug.
		config.setAttribute(CuinaLaunch.PLUGIN_MAGIC, Objects.hashCode(plugins));
		
		try
		{
			String resolvedPath = VariablesPlugin.getDefault().getStringVariableManager().
					performStringSubstitution(pluginPath);
			CuinaVariableResolver.setValue("plugin_path", resolvedPath);
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
			if (data == null) return new Object[0];
			
			Map<String, CuinaPlugin> plugins = ((PluginManager) data).getPluginFiles();
			return plugins.values().toArray();
		}
	}

	private class PluginListener implements Listener, ICheckStateListener
	{
		private boolean update;

		@Override
		public void checkStateChanged(CheckStateChangedEvent e)
		{
			if (update) return;
			update = true;
			
			String pluginName = ((CuinaPlugin) e.getElement()).getName();
			
			if (e.getChecked())
				plugins.add(pluginName);
			else
				plugins.remove(pluginName);
			cuinaTab.updateTab();
			
			update = false;
		}

		@Override
		public void handleEvent(Event e)
		{
			if (update) return;
			update = true;
			
			if (e.widget == cmdPlugin)
			{
				pluginPath = choosePluginPath().toString();
				if (pluginPath != null)
				{
					inPlugin.setText(pluginPath.toString());
				}
				cuinaTab.updateTab();
			}
			else if (e.widget == inPlugin)
			{
				try
				{
					changePluginDirectory(getPluginDirectory());
				}
				catch (CoreException e1)
				{
					e1.printStackTrace();
				}
			}
			else if (e.widget == cmdSelectAll)
			{
				Object[] elements = pluginManager.getPluginFiles().values().toArray();
				pluginTable.setCheckedElements(elements);
				
				plugins.clear();
				for (Object obj : elements)
					plugins.add( ((CuinaPlugin) obj).getName() );
				cuinaTab.updateTab();
			}
			else if (e.widget == cmdSelectNone)
			{
				pluginTable.setCheckedElements(new Object[0]);
				plugins.clear();
				cuinaTab.updateTab();
			}
			else if (e.widget == cmdAddDepends)
			{
				for (CuinaPlugin plugin : pluginManager.getPluginFiles().values())
				{
					if (!pluginTable.getChecked(plugin)) continue;
					
					List<CuinaPlugin> list = pluginManager.getAviableDependencies(plugin);
					for (CuinaPlugin otherPlugin : list)
					{
						pluginTable.setChecked(otherPlugin, true);
						plugins.add(otherPlugin.getName());
					}
				}
				cuinaTab.updateTab();
			}
			update = false;
		}
	}
	
	private void changePluginDirectory(File directory)
	{
		if (Objects.equals(directory, pluginManager.getDirectory()) ) return;

		if (directory == null)
			pluginManager.clear();
		else
			pluginManager.findPlugins(directory);
		
		pluginTable.refresh();
		fillPluginTable();
		cuinaTab.updateTab();
	}
	
	private void fillPluginTable()
	{
		if (plugins == null)
		{
			plugins = new HashSet<String>();
			for (CuinaPlugin plugin : pluginManager.getPluginFiles().values())
			{
				plugins.add(plugin.getName());
				if (plugin.getState() != State.LOADED)
					pluginTable.setGrayed(plugin, true);
				else
					pluginTable.setChecked(plugin, true);
			}
		}
		else
		{
			for (String name : plugins)
			{
				CuinaPlugin plugin = pluginManager.getPluginFiles().get(name);
				if (plugin != null)
				{
					if (plugin.getState() != State.LOADED)
						pluginTable.setGrayed(plugin, true);
					else
						pluginTable.setChecked(plugin, true);
				}
			}
		}
	}
	
	private File getPluginDirectory() throws CoreException
	{
		String path;
		try
		{
			path = VariablesPlugin.getDefault().getStringVariableManager().
					performStringSubstitution(inPlugin.getText());
			File directory = new File(path);
			if (!directory.isAbsolute())
			{
				CuinaProject project = cuinaTab.getProject();
				if (project == null) return null;
				
				String projectPath = project.getProject().getLocation().toOSString();
				directory = new File(projectPath, directory.getPath());
			}
			return directory;
		}
		catch (CoreException e)
		{
			return null;
		}
	}

	public String validate()
	{
		if (!inPlugin.getText().isEmpty())
		{
			File file = null;
			try
			{
				file = getPluginDirectory();
			}
			catch (CoreException e)
			{
				e.printStackTrace();
			}
			if (file == null || !file.exists())
				return "Plugin-Pfad nicht gefunden!";
		}
		if (plugins == null || plugins.isEmpty()) return null;
		
		Set<String> missingPlugins = new HashSet<String>();
		for (CuinaPlugin plugin : pluginManager.getPluginFiles().values())
		{
			if (!pluginTable.getChecked(plugin)) continue;
			
			Map<String, String> versions = plugin.getDependencies();
			for (String name : versions.keySet())
			{
				String version = versions.get(name);
				CuinaPlugin neededPlugin = pluginManager.getPluginFiles().get(name);
				if (neededPlugin == null || !pluginTable.getChecked(neededPlugin)
						|| !PluginManager.checkVersion(neededPlugin.getVersion(), version))
				{
					missingPlugins.add(name + " - " + version);
				}
			}
		}
		if (missingPlugins.size() > 0)
		{
			return "Ein oder mehrerere Plugin-Abhängikeiten sind nicht erfüllt:\n" + missingPlugins.toString();
		}
		
		return null;
	}
}
