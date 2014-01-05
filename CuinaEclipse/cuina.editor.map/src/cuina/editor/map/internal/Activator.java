package cuina.editor.map.internal;

import cuina.editor.map.TerrainLayer;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{
	// The plug-in ID
	public static final String PLUGIN_ID = "cuina.editor.map"; //$NON-NLS-1$

	public static final String MAPS_DIRECTORY_ID = "cuina.maps.path";
	
	static final String LAYERS_EXTENSION_POINT = "cuina.mapeditor.layers";

	// The shared instance
	private static Activator plugin;
	private Map<String, LayerDefinition> layerDefinitions = new HashMap<String, LayerDefinition>();

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
		
		registrateLayers();
	}

	private void registrateLayers()
	{
		IConfigurationElement[] elements = Platform.getExtensionRegistry().
				getConfigurationElementsFor(LAYERS_EXTENSION_POINT);

		for (IConfigurationElement conf : elements) try
		{
			LayerDefinition def = new LayerDefinition(conf);
			layerDefinitions.put(def.getName(), def);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static Map<String, LayerDefinition> getLayerDefinitions()
	{
		return Collections.unmodifiableMap(plugin.layerDefinitions);
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
	
	public class LayerDefinition
	{
		private String name;
		private Class<? extends TerrainLayer> layerClass;
		private Class<? extends IEditorActionBarContributor> contributorClass;
		
		public LayerDefinition(IConfigurationElement conf) throws Exception
		{
			this.name = conf.getAttribute("name");
			Bundle plugin = Platform.getBundle(conf.getContributor().getName());

			this.layerClass = (Class<? extends TerrainLayer>) plugin.loadClass(conf.getAttribute("class"));
			System.out.println("[Activator] Registriere Layer: " + layerClass.getName());
			
			if (conf.getAttribute("contributorClass") != null)
			{
				this.contributorClass = (Class<? extends IEditorActionBarContributor>)
						plugin.loadClass(conf.getAttribute("contributorClass"));
			}
		}

		public String getName()
		{
			return name;
		}

		public Class<? extends TerrainLayer> getLayerClass()
		{
			return layerClass;
		}

		public Class<? extends IEditorActionBarContributor> getActionBarContributorClass()
		{
			return contributorClass;
		}
	}
}
