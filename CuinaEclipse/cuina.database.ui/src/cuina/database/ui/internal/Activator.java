package cuina.database.ui.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin
{
	// The plug-in ID
	public static final String PLUGIN_ID = "cuina.database.ui"; //$NON-NLS-1$
	
	private static final String DEFAULT_IMAGE_NAME = "default.png";
	
	private static Image defaultImage;
	
	@Override
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		defaultImage = getImageDescriptor(DEFAULT_IMAGE_NAME).createImage();
	}

	@Override
	public void stop(BundleContext context) throws Exception
	{
		defaultImage.dispose();
		super.stop(context);
	}
	
	public static Image getDefaultImage()
	{
		return defaultImage;
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
}
