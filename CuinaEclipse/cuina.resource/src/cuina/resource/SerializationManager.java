package cuina.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

public class SerializationManager
{
	private static String defaultExtension = "xml";
	private static HashMap<String, SerializationProvider> providers;
	
	private SerializationManager() {}
	
	private static void registSerializationProviders()
	{
		providers = new HashMap<String, SerializationProvider>();
		
		IConfigurationElement[] elements = Platform.getExtensionRegistry().
				getConfigurationElementsFor("cuina.resource.SerializationProviders");

		for (IConfigurationElement e : elements)
		{
			try
			{
				Bundle plugin = Platform.getBundle(e.getNamespaceIdentifier());
				
				String extList = e.getAttribute("extensions");
				boolean isDefault = "true".equals(e.getAttribute("default"));
				if (extList == null) throw new NullPointerException("attribut extensions is null");
				
				Class clazz = plugin.loadClass(e.getAttribute("class"));
				
				System.out.println("[ResourceManager] Registriere SerializationProvider: " + clazz.getName() + " für Datei-Endungen: " + extList);
				SerializationProvider provider = (SerializationProvider)clazz.newInstance();
				for(String ext : extList.split(","))
				{
					if (ext == null) continue;
					
					ext = ext.trim().toLowerCase();
					if (!isDefault && providers.containsKey(ext)) continue;
					
					providers.put(ext, provider);
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * Gibt die default Dateierweiterung zurück, die zum Laden benutzt werden soll.
	 * Das Ergebnis ist <code>null</code>, wenn kein Serialization-Provider gefunden wurde.
	 * @return Die Default-Dateierweiterung.
	 */
	public static String getDefaultExtension()
	{
		if (defaultExtension == null) registSerializationProviders();
		return defaultExtension;
	}

	public static void setDefaultExtension(String extension)
	{
		Assert.isTrue(defaultExtension != null);
		SerializationManager.defaultExtension = extension;
	}

	public static Object load(IFile file, ClassLoader cl) throws ResourceException
	{
		if (providers == null) registSerializationProviders();
		String ext = file.getFileExtension();
		SerializationProvider provider = providers.get(ext);
		if (provider == null) throw new ResourceException("No Provider available!");
		
		try (InputStream in = file.getContents(true))
		{
			return provider.load(in, cl);
		}
		catch (IOException | CoreException | ClassNotFoundException e)
		{
			throw new ResourceException(file, ResourceException.LOAD, e);
		}
	}
	
	public static void save(final Object obj, final IFile file) throws ResourceException
	{
		if (providers == null) registSerializationProviders();
		String ext = file.getFileExtension();
		final SerializationProvider provider = providers.get(ext);
		if (provider == null) throw new ResourceException("No Provider available!");
		
		try
		{
			ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable()
			{
				@Override
				public void run(IProgressMonitor monitor) throws CoreException
				{
					File f = file.getLocation().toFile();
					try (OutputStream out = new FileOutputStream(f))
					{
						provider.save(obj, out);
					}
					catch (IOException e)
					{
						IStatus s = new Status(IStatus.ERROR, "cuina.resource", e.getMessage());
						CoreException ce = new CoreException(s);
						ce.initCause(e);
						throw ce;
					}
					file.refreshLocal(0, monitor);
				}

			}, null);
		}
		catch (CoreException e)
		{
			throw new ResourceException(file, ResourceException.SAVE, e);
		}
	}
}
