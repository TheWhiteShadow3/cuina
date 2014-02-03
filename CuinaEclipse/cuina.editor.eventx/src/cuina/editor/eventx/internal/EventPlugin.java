package cuina.editor.eventx.internal;

import cuina.editor.core.CuinaProject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class EventPlugin extends AbstractUIPlugin
{
	// The plug-in ID
	public static final String PLUGIN_ID = "cuina.editor.eventx"; //$NON-NLS-1$
	
	private static EventPlugin plugin;
	private Map<CuinaProject, CommandLibrary> libraries = new HashMap<CuinaProject, CommandLibrary>();
	
	/**
	 * Der Konstruktor
	 */
	public EventPlugin()
	{
		plugin = this;
	}
	
	/**
	 * Gibt die einzige Instanz zurück.
	 * @return Die einzige Instanz.
	 */
	public static EventPlugin getPlugin()
	{
		return plugin;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception
	{
		super.stop(context);
	}
	
	static CommandLibrary getLibrary(CuinaProject project)
	{
		CommandLibrary library = plugin.libraries.get(project);
		if (library == null)
		{
			library = new CommandLibrary(project);
			plugin.libraries.put(project, library);
		}
		return library;
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
	
	public static Image loadImage(String name)
	{
		String pathName = getBundleFile("icons/" + name).toString();
		return new Image(Display.getDefault(), pathName);
	}
}
