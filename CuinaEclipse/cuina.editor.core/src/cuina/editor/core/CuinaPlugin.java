package cuina.editor.core;


import cuina.editor.core.internal.EngineClassLoader;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

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
	public static final String PLUGIN_ID = "cuina.editor.core";		//$NON-NLS-1$
	public static final String NATURE_ID = "cuina.ProjectNature";	//$NON-NLS-1$
	
	public static final String IMAGE_PROJECT	 	= "project.png";
	public static final String IMAGE_DATA_FOLDER	= "data_folder.png";
	public static final String IMAGE_CONFIG_FILE 	= "cuina16.png";
	
	public static final String PROJECT_SERVICE_ID 	= "cuina.core.project.extension";

	// The shared instance
	private static CuinaPlugin plugin;
	private EngineClassLoader engineClassLoader;
	private IPartService partService;
	private CuinaPartListener listener;
	private ArrayList<EditorContextChangeListener> listeners;
	private HashMap<String, ProjectServiceFactory> projectServiceFactories;
	private HashMap<IProject, CuinaProject> projects = new HashMap<IProject, CuinaProject>();
	
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
    	for(int i = 0; i < projects.length; i++)
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
    	CuinaProject cuinaProject = plugin.projects.get(project);
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

    static ProjectServiceFactory getProjectServiceFactory(Class api)
    {
    	return plugin.projectServiceFactories.get(api.getName());
    }
    
	/*
	 * (non-Javadoc)
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
		loadProjectServices();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
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
			for(EditorContextChangeListener l : listeners)
			{
				l.editorContextChange((IEditorPart) part, file.getProject());
			}
		}
	}
	
	private void loadProjectServices()
	{
        IConfigurationElement[] elements = Platform.getExtensionRegistry().
    			getConfigurationElementsFor(PROJECT_SERVICE_ID);
        
        for (IConfigurationElement conf : elements)
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
	}
	
	public static ClassLoader getCuinaClassLoader()
	{
		if (plugin == null) throw new IllegalStateException("plugin is not started");
		if (plugin.engineClassLoader == null) try
		{
			plugin.engineClassLoader = new EngineClassLoader();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return plugin.engineClassLoader;
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

		// look for the image (this will check both the plugin and fragment
		// folders
		URL fullPathString = BundleUtility.find(bundle, "icons/" + imageName);
//		if (fullPathString == null)
//		{
//			try
//			{
//				fullPathString = new URL(imageName);
//			}
//			catch (MalformedURLException e)
//			{
//				return null;
//			}
//		}

		return ImageDescriptor.createFromURL(fullPathString);
	}
}
