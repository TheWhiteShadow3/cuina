package cuina.editor.eventx.internal;

import cuina.editor.eventx.internal.tree.CommandTree;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

public class FlowContentOutlinePage extends ContentOutlinePage
{
	private CommandTree tree;

	public void setInput(CommandTree tree)
	{
		this.tree = tree;
	}
	
	@Override
	public void createControl(Composite parent)
	{
		super.createControl(parent);
		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(new FlowOutlineProvider());
		viewer.setLabelProvider(new FlowLabelProvider(tree.getLibrary()));
		viewer.addSelectionChangedListener(this);
		viewer.setInput(tree);
	}
}