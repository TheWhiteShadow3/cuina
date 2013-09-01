package cuina.editor.gui.internal.provider;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import cuina.editor.gui.internal.WidgetLibrary;

public class LibraryLabelProvider extends LabelProvider
{
	private WidgetLibrary library;
	
	public LibraryLabelProvider(WidgetLibrary library)
	{
		this.library = library;
	}
	
	@Override
	public Image getImage(Object element)
	{
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
	}
	
	@Override
	public String getText(Object element)
	{
		return library.getDescription((Class<?>) element);
	}
}
