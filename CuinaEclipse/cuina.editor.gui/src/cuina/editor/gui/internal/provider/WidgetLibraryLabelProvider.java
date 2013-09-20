package cuina.editor.gui.internal.provider;

import cuina.editor.gui.internal.WidgetLibrary.WidgetType;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class WidgetLibraryLabelProvider extends LabelProvider
{
	private final Map<String, Image> imageCache = new HashMap<String, Image>();
	
	@Override
	public Image getImage(Object element)
	{
		if (element instanceof WidgetType)
		{
			WidgetType type = (WidgetType) element;
			Image image = imageCache.get(type.getName());
			if (image == null)
			{
				ImageDescriptor id = type.getImageDescriptor();
				if (id != null)
				{
					image = id.createImage();
					imageCache.put(type.getName(), image);
					return image;
				}
			}
		}
		return null;
	}
	
	@Override
	public String getText(Object element)
	{
		if (element instanceof WidgetType)
		{
			return ((WidgetType) element).getName();
		}
		return "";
	}

	@Override
	public void dispose()
	{
		for (Image img : imageCache.values()) img.dispose();
	}
}
