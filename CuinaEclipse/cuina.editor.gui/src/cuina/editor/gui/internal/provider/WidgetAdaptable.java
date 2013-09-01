package cuina.editor.gui.internal.provider;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class WidgetAdaptable implements IAdaptable
{

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter)
	{
		if (adapter == IWorkbenchAdapter.class)
            return WidgetWorkspaceAdapter.getAdapter();
		return null;
	}
	
}
