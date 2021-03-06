package cuina.editor.object.internal;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{
	// The plug-in ID
	public static final String PLUGIN_ID = "cuina.editor.object"; //$NON-NLS-1$

	private static final String EXTENSION_TYPES = "cuina.object.extensionTypes";

	// The shared instance
	private static Activator plugin;
	
	public static Map<String, java.util.List<ExtensionDescriptor>> descriptors;

	/**
	 * The constructor
	 */
	public Activator() {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault()
	{
		return plugin;
	}
	
	public static Map<String, java.util.List<ExtensionDescriptor>> getExtensionDescriptors()
	{
		if (descriptors == null)
		{
			descriptors = new HashMap<String, java.util.List<ExtensionDescriptor>>();
			
			IConfigurationElement[] elements = Platform.getExtensionRegistry().
					getConfigurationElementsFor(EXTENSION_TYPES);

			for (IConfigurationElement conf : elements) try
			{
				ExtensionDescriptor desc = new ExtensionDescriptor(conf);
				
				String id = desc.getID();
				java.util.List<ExtensionDescriptor> descList = descriptors.get(id);
				if (descList == null)
				{
					descList = new ArrayList<ExtensionDescriptor>();
					descriptors.put(id, descList);
				}
				descList.add(desc);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return descriptors;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path)
	{
		return imageDescriptorFromPlugin(PLUGIN_ID, "icons/" + path);
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
}
