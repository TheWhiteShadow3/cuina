package cuina.database.ui.internal;

import cuina.database.DatabasePlugin;
import cuina.database.NamedItem;
import cuina.database.ui.internal.tree.TreeDataNode;
import cuina.database.ui.internal.tree.TreeGroup;
import cuina.database.ui.tree.TreeNode;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class DataLabelProvider extends LabelProvider
{
	private HashMap<Object, Image> imageCache = new HashMap<Object, Image>();
	
	@Override
	public Image getImage(Object element)
	{
		if (element instanceof TreeDataNode)
		{
			return DatabasePlugin.getDescriptor(getTableName((TreeNode) element)).getImage();
		}
		else if (element instanceof TreeGroup)
		{
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
		}
		else if (element instanceof IFile)
		{
			if (DatabasePlugin.isDataFile((IFile) element))
				return DatabasePlugin.getDescriptor((IFile) element).getImage();
			else
				return null;
//			return getDefaultImage();//DatabasePlugin.getDescriptor((IFile) element).getImage();
		}
		
		ImageDescriptor desc = null;
		if (element instanceof IAdaptable)
			desc = (ImageDescriptor) ((IAdaptable) element).getAdapter(ImageDescriptor.class);
		
		if (desc == null)
			desc = (ImageDescriptor)Platform.getAdapterManager().getAdapter(element, ImageDescriptor.class);
		
		if (desc != null)
		{
			Image image = desc.createImage();
			imageCache.put(element, image);
			return desc.createImage();
		}
			
//			if (isDatabaseFolder((IFolder) element))
//			{
//				this.image = new Image(Display.getDefault(), FileLocator.resolve(plugin.getEntry("")).getPath());
//			}
//			else
//			{
//				PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
//			}
		return Activator.getDefaultImage();
	}
	
//	private boolean isDatabaseFolder(IFolder folder)
//	{
//		IProject project = folder.getProject();
//		IFolder f = project.getFolder(DatabasePlugin.getDatabase(project).getDataPath());
//		
////		IProject project = folder.getProject();
////		IPath path = new Path(project.getName() + '/' + DatabasePlugin.getDatabase(project).getDataPath());
//		return folder.equals(f);
//	}

	@Override
	public String getText(Object element)
	{
		if ((element instanceof TreeGroup) && ((TreeGroup) element).getParent() == null)
		{
			return getTableName((TreeNode) element);
		}
		else if (element instanceof NamedItem)
		{
			return ((NamedItem) element).getName();
		}
		else if (element instanceof IResource)
		{
			return ((IResource) element).getName();
		}
		return element.toString();
	}
	
	private String getTableName(TreeNode node)
	{
		return node.getTable().getName();
	}

	@Override
	public void dispose()
	{
		imageCache = null;
		super.dispose();
	}
}
