package cuina.widget;

import cuina.editor.gui.internal.WidgetPage;
import cuina.widget.data.WidgetNode;
import cuina.widget.data.WidgetTree;

import de.matthiasmann.twl.Widget;

public class WidgetTreeEditor
{
	private WidgetPage page;
	private WidgetNode rootNode;
	private WidgetFactory factory;
	
	public WidgetTreeEditor(WidgetPage page)
	{
		this.page = page;
		this.factory = new WidgetFactory(page);
	}
	
	public void setTree(WidgetTree tree)
	{
		factory.createWidget(rootNode);
	}
	
//	public Widget getRootWidget()
//	{
//		return rootNode.;
//	}
	
	public void addWidget(WidgetNode parent, WidgetNode newNode)
	{
		Widget newWidget = factory.createWidget(newNode);
		parent.children.add(newNode);
//		parent.widget.add(newWidget);
	}
}
