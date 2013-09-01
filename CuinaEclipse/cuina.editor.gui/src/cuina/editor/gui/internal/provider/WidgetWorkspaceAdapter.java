package cuina.editor.gui.internal.provider;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

@Deprecated
public class WidgetWorkspaceAdapter implements IWorkbenchAdapter
{
	private WidgetContentProvider contentProvider;
	private WidgetLabelProvider labelProvider;
	
	private static WidgetWorkspaceAdapter adapter;
	
	private WidgetWorkspaceAdapter()
	{
		this.contentProvider = new WidgetContentProvider();
//		this.labelProvider = new WidgetLabelProvider(false);
	}
	
	@Override
	public Object[] getChildren(Object o)
	{
		return contentProvider.getChildren(o);
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object)
	{
		return ImageDescriptor.createFromImage(labelProvider.getImage(object));
	}

	@Override
	public String getLabel(Object o)
	{
		return labelProvider.getText(o);
	}

	@Override
	public Object getParent(Object o)
	{
		return contentProvider.getParent(o);
	}

	public static WidgetWorkspaceAdapter getAdapter()
	{
		if(adapter == null)
			adapter = new WidgetWorkspaceAdapter();
		return adapter;
	}

}
