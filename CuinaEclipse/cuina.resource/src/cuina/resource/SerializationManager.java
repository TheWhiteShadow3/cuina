package cuina.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

public class SerializationManager
{
//	private static String defaultExtension = "xml";
//	private static final Map<String, String> ALIAS_MAP = new HashMap<String, String>();
	private static HashMap<String, SerializationProvider> providers;
	
	private SerializationManager() {}
	
	private static void registerSerializationProviders()
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
				
				Class<?> clazz = plugin.loadClass(e.getAttribute("class"));
				
				System.out.println("[SerializationManager] Registriere SerializationProvider: " + clazz.getName() + " für Datei-Endungen: " + extList);
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
//		//XXX: lade Aliasse hier, da noch kein Extensionpoint definiert ist, der das tut.
//		SerializationManager.addAlias("xml", "cxd");
//		SerializationManager.addAlias("xml", "cxm");
//		SerializationManager.addAlias("ser", "cjd");
//		SerializationManager.addAlias("ser", "cjm");
//		SerializationManager.addAlias("xml.gz", "cxz");
//		SerializationManager.addAlias("ser,gz", "cjz");
	}
//	
//	public static void addAlias(String name, String alias)
//	{
//		ALIAS_MAP.put(alias, name);
//	}
//	
//	/**
//	 * Gibt die default Dateierweiterung zurück, die zum Laden benutzt werden soll.
//	 * @return Die Default-Dateierweiterung.
//	 */
//	public static String getDefaultExtension()
//	{
//		return defaultExtension;
//	}
//
//	public static void setDefaultExtension(String extension)
//	{
//		Assert.isTrue(defaultExtension != null);
//		SerializationManager.defaultExtension = extension;
//	}
	
	public static String[] getSupportedExtensions()
	{
		return providers.keySet().toArray(new String[providers.size()]);
	}

	public static SerializationProvider getSerializationProvider(String extension)
	{
//		String alias = ALIAS_MAP.get(extension);
//		if (alias != null) extension = alias;
//		if (extension.endsWith(".gz"))
//		{
//			int p = extension.lastIndexOf('.');
//			extension = extension.substring(0, p);
//			
//			SerializationProvider provider = providers.get(extension);
//			if (provider == null) return null;
//			
//			return new GZipXMLSerialisationProvider(provider);
//		}
		return providers.get(extension);
	}
	
	/**
	 * Gibt das IFile-Objekt mit der bevorzugten Dateiendung zu einer Datei zurück.
	 * <p>
	 * Sofern eine Datei mit dem angegebenen Namen existiert wird diese zurückgegeben.
	 * Dabei wird die Reihenfolge der bevorzugten Endungen berücksichtigt.
	 * </p>
	 * <p>
	 * Wenn keine Datei mit dem angegebenen Namen existiert,
	 * wird eine Datei mit der ersten Endung aus der Liste zurückgegeben.
	 * Ist die Liste leer, wird eine Datei ohne Endung zurückgegeben.
	 * </p>
	 * <p>
	 * <i>Es ist nicht sicher, dass die zurückgegebene Datei existiert.
	 * In jedem Fall ist sie niemals </i><code>null</code>.
	 * </p>
	 * @param folder Ordner, in dem die Datei ausgewählt werden soll.
	 * @param name Dateiname ohne Endung.
	 * @param preferredExtensions Liste, der bevorzugten Endungen.
	 * @return Datei mit der bevorzugten Endung.
	 * @throws ResourceException
	 */
	public static IFile resolve(IFolder folder, String name, String... preferredExtensions) throws ResourceException
	{
		try
		{
			IResource[] elements = folder.members();
			IFile file = null;
			int matchLevel = preferredExtensions.length;
			for (IResource r : elements)
			{
				if (!(r instanceof IFile)) continue;
				
				String filename = r.getName();
				int dot = filename.lastIndexOf('.');
				if (dot == -1)
				{
					// akzeptiere Datei ohne Ednung nur, wenn noch nichts gefunden wurde.
					if (file == null && filename.equals(name)) file = (IFile) r;
					continue;
				}
				else if (filename.substring(0, dot).equals(name))
				{
					String ext = r.getFileExtension();
					for (int i = 0; i < matchLevel; i++)
					{
						if (preferredExtensions[i].equals(ext))
						{
							file = (IFile) r;
							if (i == 0) return file;
							matchLevel = i;
							break;
						}
					}
					if (file == null) file = (IFile) r;
				}
			}
			if (file == null)
			{
				if (preferredExtensions.length > 0)
					file = folder.getFile(name + '.' + preferredExtensions[0]);
				else
					file = folder.getFile(name);
			}
			return file;
		}
		catch(CoreException e)
		{
			throw new ResourceException(name, ResourceException.LOAD, e);
		}
	}

	public static Object load(IFile file, ClassLoader cl) throws ResourceException
	{
		if (providers == null) registerSerializationProviders();
		SerializationProvider provider = getSerializationProvider(file.getFileExtension());
		if (provider == null) providerNotFound(file);
		
		try (InputStream in = file.getContents(true))
		{
			return provider.load(in, cl);
		}
		catch (IOException | CoreException | ClassNotFoundException e)
		{
			throw new ResourceException(file, ResourceException.LOAD, e);
		}
	}
	
//	private static boolean isGZIPFormat(InputStream in) throws IOException
//	{
//		in.mark(4);
//		byte[] b = new byte[2];
//		in.read(b);
//		in.reset();
//		int num = b[0] + 8 << b[1];
//		return (num == 0x8b1f); // So wie in GZIPOutputStream definiert.
//	}
	
	public static void save(final Object obj, final IFile file) throws ResourceException
	{
		if (providers == null) registerSerializationProviders();
		final SerializationProvider provider = getSerializationProvider(file.getFileExtension());
		if (provider == null) providerNotFound(file);
		
		try
		{
			ResourcesPlugin.getWorkspace().run(new SerialisationWriter(provider, file, obj), null);
		}
		catch (CoreException e)
		{
			throw new ResourceException(file, ResourceException.SAVE, e);
		}
	}
	
//	private static String getSecoundExtension(IFile file)
//	{
//		String name = file.getName();
//		int p2 = name.lastIndexOf('.');
//		int p1 = name.lastIndexOf('.', p2-1);
//		if (p1 == -1) return null;
//		return name.substring(p1+1, p2);
//	}
	
	private static void providerNotFound(IFile file) throws ResourceException
	{
		throw new ResourceException("No provider for '" + file.getName() + "' found!");
	}
	
	private static class SerialisationWriter implements IWorkspaceRunnable
	{
		private SerializationProvider provider;
		private IFile file;
		private Object object;
		
		public SerialisationWriter(SerializationProvider provider, IFile file, Object object)
		{
			this.provider = provider;
			this.file = file;
			this.object = object;
		}
		
		@Override
		public void run(IProgressMonitor monitor) throws CoreException
		{
			File f = file.getLocation().toFile();
			try (OutputStream out = new FileOutputStream(f))
			{
				provider.save(object, out);
				out.flush();
				out.close();
			}
			catch (IOException e)
			{
				CoreException ce = new CoreException(new Status(IStatus.ERROR, "cuina.resource", e.getMessage()));
				ce.initCause(e);
				throw ce;
			}
			file.refreshLocal(0, monitor);
		}
	}
}
