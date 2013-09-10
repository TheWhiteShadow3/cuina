package cuina.editor.core;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class CuinaPlugin extends AbstractUIPlugin
{
	// The plug-in ID
	public static final String PLUGIN_ID 			= "cuina.editor.core"; //$NON-NLS-1$
	public static final String NATURE_ID 			= "cuina.ProjectNature"; //$NON-NLS-1$

	public static final String IMAGE_PROJECT 		= "project.png";
	public static final String IMAGE_DATA_FOLDER 	= "data_folder.png";
	public static final String IMAGE_CONFIG_FILE 	= "cuina16.png";

	public static final String PROJECT_EXTENSION_ID 	= "cuina.core.project.extension";
	
	private static final String MSG_PLUGIN_CLOSED = "cuina-plugin is closed!"; //$NON-NLS-1$

	// The shared instance
	private static CuinaPlugin plugin;
	private IPartService partService;
	private CuinaPartListener listener;
	private List<EditorContextChangeListener> listeners;
	private Map<String, ProjectServiceFactory> projectServiceFactories;
	private final Map<IProject, CuinaProject> projects = new HashMap<IProject, CuinaProject>();
	private final Map<String, List<ProjectParameter>> parameters = new HashMap<String, List<ProjectParameter>>();
	private final List<IProjectHook> projectHooks = new ArrayList<IProjectHook>();
	
	/**
	 * The constructor
	 */
	public CuinaPlugin()
	{
		listener = new CuinaPartListener();
		listeners = new ArrayList<EditorContextChangeListener>();
		projectServiceFactories = new HashMap<String, ProjectServiceFactory>();
	}

	public static CuinaProject[] getCuinaProjects()
	{
		IProject[] projects = getWorkspaceRoot().getProjects();
		CuinaProject[] cuinaProjects = new CuinaProject[projects.length];
		for (int i = 0; i < projects.length; i++)
		{
			cuinaProjects[i] = getCuinaProject(projects[i]);
		}
		return cuinaProjects;
	}

	private static IWorkspaceRoot getWorkspaceRoot()
	{
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	public static CuinaProject getCuinaProject(IProject project)
	{
		CuinaProject cuinaProject = getPlugin().projects.get(project);
		if (cuinaProject == null)
		{
			cuinaProject = new CuinaProject(project);
			plugin.projects.put(project, cuinaProject);
		}
		return cuinaProject;
	}

	public static CuinaProject getCuinaProject(String name)
	{
		return getCuinaProject(getWorkspaceRoot().getProject(name));
	}
	
	public static ProjectParameter getProjectParameter(String group, String name)
	{
		List<ProjectParameter> params = getPlugin().parameters.get(group);
		if (params == null) return null;
		
		return params.get(params.indexOf(name));
	}
	
	public static String[] getProjectParameterGroups()
	{
		return getPlugin().parameters.keySet().toArray(new String[plugin.parameters.size()]);
	}
	
	public static ProjectParameter[] getProjectParameters(String group)
	{
		List<ProjectParameter> params = getPlugin().parameters.get(group);
		if (params == null) return null;
		
		return params.toArray(new ProjectParameter[params.size()]);
	}

	static ProjectServiceFactory getProjectServiceFactory(Class api)
	{
		return getPlugin().projectServiceFactories.get(api.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
		Display.getDefault().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				partService = Workbench.getInstance().getActiveWorkbenchWindow().getPartService();
				partService.addPartListener(listener);
			}
		});
		loadProjectExtensions();
		loadProjectParameters();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		partService.removePartListener(listener);
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static CuinaPlugin getPlugin()
	{
		if (plugin == null) throw new IllegalStateException(MSG_PLUGIN_CLOSED);
		return plugin;
	}

	public void addEditorContextChangeListener(EditorContextChangeListener l)
	{
		listeners.add(l);
	}

	public void removeEditorContextChangeListener(EditorContextChangeListener l)
	{
		listeners.remove(l);
	}

	private void fireEditorContextChange(IWorkbenchPartReference partRef)
	{
		IWorkbenchPart part = partRef.getPart(false);
		if (part instanceof IEditorPart)
		{
			IEditorInput editorInput = ((IEditorPart) part).getEditorInput();
			IFile file = (IFile) editorInput.getAdapter(IFile.class);
			for (EditorContextChangeListener l : listeners)
			{
				l.editorContextChange((IEditorPart) part, file.getProject());
			}
		}
	}

	private void loadProjectExtensions()
	{
		IConfigurationElement[] elements = Platform.getExtensionRegistry().
				getConfigurationElementsFor(PROJECT_EXTENSION_ID);

		for (IConfigurationElement conf : elements)
		{
			if ( "serviceFactory".equals(conf.getName()) ) loadProjectService(conf);
			
			else if ( "hook".equals(conf.getName()) ) loadProjectHooks(conf);
		}
	}
	
	private void loadProjectService(IConfigurationElement conf)
	{
		try
		{
			ProjectServiceFactory factory = (ProjectServiceFactory) conf.createExecutableExtension("class");

			IConfigurationElement[] childs = conf.getChildren("Service");
			for (IConfigurationElement childConf : childs)
			{
				String apiName = childConf.getAttribute("class");
				if (!projectServiceFactories.containsKey(apiName))
				{
					projectServiceFactories.put(apiName, factory);
				}
			}
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}
	
	private void loadProjectHooks(IConfigurationElement conf)
	{
		try
		{
			IProjectHook hook = (IProjectHook) conf.createExecutableExtension("class");
			projectHooks.add(hook);
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}
	
	private void loadProjectParameters()
	{
		IConfigurationElement[] elements = Platform.getExtensionRegistry().
				getConfigurationElementsFor(ProjectParameter.PROJECT_PARAMETER_ID);
		
		for (IConfigurationElement conf : elements)
		{
			ProjectParameter param = new ProjectParameter(conf);
			
			List<ProjectParameter> list = parameters.get(param.getGroup());
			if (list != null)
			{
				if (list.contains(param)) throw new IllegalArgumentException("Parameter already exists.");
				
				list.add(param);
			}
			else
			{
				list = new ArrayList<ProjectParameter>(8);
				list.add(param);
				parameters.put(param.getGroup(), list);
			}
		}
		
		// Sortiere die Parameter
		for (List<ProjectParameter> list : parameters.values())
		{
			Collections.sort(list);
		}
	}
	
	private class CuinaPartListener implements IPartListener2
	{
		private boolean debugOut = false;

		@Override
		public void partActivated(IWorkbenchPartReference partRef)
		{
			if (debugOut) System.out.println("partActivated: " + partRef.getPartName());
			fireEditorContextChange(partRef);
		}

		@Override
		public void partBroughtToTop(IWorkbenchPartReference partRef)
		{
			if (debugOut) System.out.println("partBroughtToTop: " + partRef.getPartName());
		}

		@Override
		public void partClosed(IWorkbenchPartReference partRef)
		{
			if (debugOut) System.out.println("partClosed: " + partRef.getPartName());
		}

		@Override
		public void partDeactivated(IWorkbenchPartReference partRef)
		{
			if (debugOut) System.out.println("partDeactivated: " + partRef.getPartName());
		}

		@Override
		public void partOpened(IWorkbenchPartReference partRef)
		{
			if (debugOut) System.out.println("partOpened: " + partRef.getPartName());
		}

		@Override
		public void partHidden(IWorkbenchPartReference partRef)
		{
			if (debugOut) System.out.println("partHidden: " + partRef.getPartName());
		}

		@Override
		public void partVisible(IWorkbenchPartReference partRef)
		{
			if (debugOut) System.out.println("partVisible: " + partRef.getPartName());
		}

		@Override
		public void partInputChanged(IWorkbenchPartReference partRef)
		{
			if (debugOut) System.out.println("partInputChanged: " + partRef.getPartName());
		}
	}

	public static Image getImage(String imageName)
	{
		return getImageDescriptor(imageName).createImage();
	}

	public static ImageDescriptor getImageDescriptor(String imageName)
	{
		Bundle bundle = Platform.getBundle(PLUGIN_ID);
		if (!BundleUtility.isReady(bundle)) { return null; }

		URL fullPathString = BundleUtility.find(bundle, "icons/" + imageName);

		return ImageDescriptor.createFromURL(fullPathString);
	}
}
