package cuina.widget;

import cuina.widget.data.WidgetTree;

import java.util.HashMap;

import de.matthiasmann.twl.Widget;

public class DataWidgetDescriptor implements WidgetDescriptor
{
	private static final long serialVersionUID = 8582619939177485415L;
	
	transient HashMap<String, Widget> widgets = new HashMap<String, Widget>();
	transient Widget root;
	
	private WidgetTree tree;
	
	DataWidgetDescriptor(WidgetTree tree)
	{
		this.tree = tree;
	}

	@Override
	public Widget getWidget(String key)
	{
		return widgets.get(key);
	}
	
	@Override
	public Widget createRoot()
	{
		this.root = WidgetFactory.createWidget(tree.root);
		this.widgets = WidgetFactory.buildMap;
		return root;
	}

	@Override
	public String getTheme()
	{
		return null;
	}

	@Override
	public void postBuild()
	{
		if (tree.active != null)
			getWidget(tree.active).requestKeyboardFocus();
	}
}
