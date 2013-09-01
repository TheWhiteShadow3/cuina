package cuina.editor.gui.internal;

import cuina.widget.data.WidgetNode;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;

public class AdapterFactory implements IAdapterFactory
{
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType)
	{
		if (adapterType == ImageDescriptor.class && adaptableObject instanceof WidgetNode)
		{
			return Activator.getImageDescriptor("widget.png");
		}
		return null;
	}

	@Override
	public Class[] getAdapterList()
	{
		throw new UnsupportedOperationException(); // Wird eh nicht benutzt
	}
}
