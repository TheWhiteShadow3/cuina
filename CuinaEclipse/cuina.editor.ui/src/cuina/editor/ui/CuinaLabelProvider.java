package cuina.editor.ui;

import cuina.editor.core.CuinaCore;
import cuina.resource.ResourceManager;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class CuinaLabelProvider extends LabelProvider
{
	@Override
	public Image getImage(Object element)
	{
		if (element instanceof IProject)
			return CuinaCore.getImage(CuinaCore.IMAGE_PROJECT);
		else if (element instanceof IFolder)
		{
			if (ResourceManager.isRegistedDirectory((IFolder) element))
				return CuinaCore.getImage(CuinaCore.IMAGE_DATA_FOLDER);
		}
		else if (element instanceof IFile && ((IFile) element).getFileExtension().equals("cfg"))
		{
			return CuinaCore.getImage(CuinaCore.IMAGE_CONFIG_FILE);
		}
		
		return null;
	}

	@Override
	public String getText(Object element)
	{
		if (element instanceof IResource)
		{
			return ((IResource) element).getName();
		}
		
		return super.getText(element);
	}
}
