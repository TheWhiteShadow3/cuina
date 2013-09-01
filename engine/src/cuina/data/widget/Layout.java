package cuina.data.widget;

import cuina.widget.data.WidgetNode;

public class Layout extends WidgetNode
{
	public enum LayoutType
	{
		BORDER, DIALOG, BOX
	}

	public enum LayoutLocation
	{
		// for BorderLayout:
		EAST, WEST, NORTH, SOUTH, CENTER
	}

	private LayoutType type;

	public String getTitle()
	{
		return super.getKey() + "~" + toString();
	}

}
