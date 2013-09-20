package cuina.editor.gui.internal.tree;

import cuina.widget.data.WidgetNode;

public interface WidgetTreeEditorListener
{
	public void widgetAdded(WidgetTreeEditor treeEditor, WidgetNode parent, WidgetNode node);
	public void widgetRemoved(WidgetTreeEditor treeEditor, WidgetNode parent, WidgetNode node);
	public void widgetChanged(WidgetTreeEditor treeEditor, WidgetNode node);
}
