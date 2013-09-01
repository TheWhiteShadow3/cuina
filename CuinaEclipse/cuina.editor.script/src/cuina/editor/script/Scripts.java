package cuina.editor.script;

import cuina.editor.core.CuinaProject;
import cuina.editor.script.internal.ScriptCache;
import cuina.editor.script.library.IScriptLibrary;
import cuina.editor.script.library.StaticScriptLibrary;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Scripts extends AbstractUIPlugin
{
	// The plug-in ID
	public static final String PLUGIN_ID = "cuina.editor.script"; //$NON-NLS-1$
	
	private static final String MSG_SERVICE_CLOSED = "script-service is closed!"; //$NON-NLS-1$

	// The shared instance
	private static Scripts plugin;
	private HashMap<CuinaProject, ScriptCache> scriptCaches = new HashMap<CuinaProject, ScriptCache>();
	private HashMap<CuinaProject, StaticScriptLibrary> libaries = new HashMap<CuinaProject, StaticScriptLibrary>();
	
	private Image PAGE_REQUIRED_IMAGE;
	private Image PAGE_OPTIONAL_IMAGE;
	
	public static final int IMG_PAGE_REQUIRED = 1;
	public static final int IMG_PAGE_OPTIONAL = 2;
	
	/**
	 * The constructor
	 */
	public Scripts()
	{
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		
		PAGE_REQUIRED_IMAGE = loadBundleImage("req_page.png");
		PAGE_OPTIONAL_IMAGE = loadBundleImage("opt_page.png");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception
	{
		PAGE_REQUIRED_IMAGE.dispose();
		PAGE_OPTIONAL_IMAGE.dispose();
		
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Scripts getPlugin()
	{
		return plugin;
	}
	
	// Delegate-Methode. Kann für Test überschrieben werden.
	@Override
	public IPreferenceStore getPreferenceStore()
	{
		return super.getPreferenceStore();
	}
	
	public static IScriptLibrary getScriptLibrary(CuinaProject project)
	{
		if (plugin == null) throw new IllegalStateException(MSG_SERVICE_CLOSED);
		StaticScriptLibrary library = plugin.libaries.get(project);
		if (library == null)
		{
			library = new StaticScriptLibrary(project);
			plugin.libaries.put(project, library);
		}
		return library;
	}
	
	public static ScriptCache getScriptCache(CuinaProject project)
	{
		if (plugin == null) throw new IllegalStateException(MSG_SERVICE_CLOSED);
		ScriptCache cache = plugin.scriptCaches.get(project);
		if (cache == null)
		{
			cache = new ScriptCache(project);
			plugin.scriptCaches.put(project, cache);
		}
		return cache;
	}
	
	public static Image getImage(int type)
	{
		switch (type)
		{
			case IMG_PAGE_REQUIRED: return plugin.PAGE_REQUIRED_IMAGE;
			case IMG_PAGE_OPTIONAL: return plugin.PAGE_OPTIONAL_IMAGE;
			default: return null;
		}
	}
	
	public static File getBundleFile(String name)
	{
		if (name == null) throw new NullPointerException();
		
		URL url = plugin.getBundle().getEntry(name);
		try
		{
			URL realUrl = FileLocator.resolve(url);
			return new File(realUrl.toURI());
		}
		catch (IOException | URISyntaxException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	private Image loadBundleImage(String name)
	{
		String pathName = getBundleFile("icons/" + name).toString();
		return new Image(Display.getDefault(), pathName);
	}
}
