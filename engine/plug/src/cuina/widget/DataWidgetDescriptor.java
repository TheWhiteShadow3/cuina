package cuina.widget;

import cuina.Logger;
import cuina.widget.data.WidgetNode;

public class DataWidgetDescriptor implements WidgetDescriptor
{
	private static final long serialVersionUID = 8582619939177485416L;
	
//	transient Map<String, Widget> widgets = Collections.EMPTY_MAP;
	private CuinaWidget root;
	
	private WidgetNode rootNode;
	private String active;
	
	DataWidgetDescriptor(WidgetNode rootNode, String active)
	{
		this.rootNode = rootNode;
		this.active = active;
	}

//	@Override
//	public Widget getWidget(String key)
//	{
//		return widgets.get(key);
//	}
	
	@Override
	public CuinaWidget createRoot()
	{
		this.root = WidgetFactory.createWidget(rootNode);
//		this.widgets = WidgetFactory.buildMap;
		
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
		if (active != null)
		{
			CuinaWidget widget = root.find(active);
			if (widget == null)
				Logger.log(DataWidgetDescriptor.class, Logger.WARNING, "Widget '" + active + "' not found.");
			else
				widget.requestKeyboardFocus();
		}
	}
}
