package cuina.editor.gui.internal.tree;

import cuina.widget.data.WidgetNode;
import cuina.widget.data.WidgetTree;

import java.util.ArrayList;
import java.util.List;

public class WidgetTreeEditor
{
	private WidgetTree tree;
	
	private final List<WidgetTreeEditorListener> listeners = new ArrayList<WidgetTreeEditorListener>();
	
	public WidgetTreeEditor()
	{
	}
	
	public void setWidgetTree(WidgetTree tree)
	{
		if (this.tree != null)
			fireWidgetRemoved(null, this.tree.root);
		this.tree = tree;
		fireWidgetAdded(null, tree.root);
	}
	
	public WidgetTree getWidgetTree()
	{
		return tree;
	}
	
	public void addWidget(WidgetNode parent, WidgetNode newNode)
	{
		parent.children.add(newNode);
		fireWidgetAdded(parent, newNode);
	}
	
	public void removeWidget(WidgetNode node)
	{
		node.parent.children.remove(node);
		fireWidgetRemoved(node.parent, node);
	}
	
	public void updateWidget(WidgetNode node)
	{
		fireWidgetChanged(node);
	}

	public boolean addWidgetTreeEditorListener(WidgetTreeEditorListener l)
	{
		return listeners.add(l);
	}

	public boolean removeWidgetTreeEditorListener(WidgetTreeEditorListener l)
	{
		return listeners.remove(l);
	}
	
	protected void fireWidgetAdded(WidgetNode parent, WidgetNode node)
	{
		for (int i = 0; i < listeners.size(); i++)
		{
			listeners.get(i).widgetAdded(this, parent, node);
		}
	}
	
	protected void fireWidgetRemoved(WidgetNode parent, WidgetNode node)
	{
		for (int i = 0; i < listeners.size(); i++)
		{
			listeners.get(i).widgetRemoved(this, parent, node);
		}
	}
	
	protected void fireWidgetChanged(WidgetNode node)
	{
		for (int i = 0; i < listeners.size(); i++)
		{
			listeners.get(i).widgetChanged(this, node);
		}
	}
}
