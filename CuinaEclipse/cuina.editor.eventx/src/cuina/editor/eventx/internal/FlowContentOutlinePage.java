package cuina.editor.eventx.internal;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import cuina.eventx.CommandList;

public class FlowContentOutlinePage extends ContentOutlinePage
{
	private CommandList list;

	public void setInput(CommandList list)
	{
		this.list = list;
	}
	
	@Override
	public void createControl(Composite parent)
	{
		super.createControl(parent);
		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(new FlowContentProvider());
		viewer.setLabelProvider(new FlowLabelProvider());
		viewer.addSelectionChangedListener(this);
		viewer.setInput(list);
	}
}