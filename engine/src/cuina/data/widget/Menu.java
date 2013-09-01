package cuina.data.widget;

import cuina.widget.data.WidgetNode;

import java.util.ArrayList;

public class Menu extends WidgetNode
{
	public enum Type
	{
		BAR, POPUP, COMBO, RADIAL
	}

	private WidgetNode parent;
	
	private boolean enabled;
	private Type type;
}
