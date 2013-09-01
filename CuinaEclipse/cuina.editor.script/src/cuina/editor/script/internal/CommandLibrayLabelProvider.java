package cuina.editor.script.internal;

import cuina.editor.script.internal.CommandLibraryContentProvider.CommandLibraryElement;
import cuina.editor.script.library.ClassDefinition;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class CommandLibrayLabelProvider extends LabelProvider
{
	@Override
	public String getText(Object element)
	{
		if (element instanceof ClassDefinition)
		{
			String text = ((ClassDefinition) element).getLabel();
			if (text == null || text.isEmpty())
				return "default";
			else
				return text;
		}
		else if (element instanceof CommandLibraryElement)
		{
			CommandLibraryElement cle = (CommandLibraryElement) element;
			if (cle.function != null)
				return cle.function.getLabel();
			else
				return cle.name;
		}
		else
			return "!LabelProvider ist Machtlos!";
	}

	@Override
	public Image getImage(Object element)
	{
		if (element instanceof ClassDefinition)
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
		else
			return null;
	}
}
