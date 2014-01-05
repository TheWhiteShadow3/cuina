package cuina.widget;

import cuina.Logger;
import cuina.widget.data.WidgetNode;

import java.util.Collections;
import java.util.Map;

import de.matthiasmann.twl.Widget;

public class DataWidgetDescriptor implements WidgetDescriptor
{
	private static final long serialVersionUID = 8582619939177485415L;
	
	transient Map<String, Widget> widgets = Collections.EMPTY_MAP;
	private Widget root;
	
	private WidgetNode rootNode;
	private String active;
	private WidgetEventHandler handler;
	
	DataWidgetDescriptor(WidgetNode rootNode, String active)
	{
		this.rootNode = rootNode;
		this.active = active;
	}
	
	@Override
	public void setGlobalEventHandler(WidgetEventHandler handler)
	{
		this.handler = handler;
		refreshGlobalEventHandler();
	}

	@Override
	public Widget getWidget(String key)
	{
		return widgets.get(key);
	}
	
	@Override
	public Widget createRoot()
	{
		this.root = WidgetFactory.createWidget(rootNode);
		this.widgets = WidgetFactory.buildMap;
		refreshGlobalEventHandler();
		
		return root;
	}
	
	private void refreshGlobalEventHandler()
	{
		if (widgets.isEmpty() || handler == null) return;
		for(Widget w : widgets.values())
		{	// Möglich, da nur Widgets aus der Fabrik in der Liste liegen und diese alle CuinaWidget implementieren.
			CuinaWidget cw = (CuinaWidget) w;
			if (cw.canHandleEvents())
				cw.setEventHandler(handler);
		}
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
			Widget widget = getWidget(active);
			if (widget == null)
				Logger.log(DataWidgetDescriptor.class, Logger.WARNING, "Widget '" + active + "' not found.");
			else
				widget.requestKeyboardFocus();
		}
	}
}
