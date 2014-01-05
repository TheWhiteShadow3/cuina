package cuina.widget;

import cuina.database.NamedItem;

public interface CuinaWidget extends NamedItem
{
	@Override
	public String getName();
	public boolean canHandleEvents();
	public void setEventHandler(WidgetEventHandler handler);
	public WidgetEventHandler getEventHandler();
	public int getX();
	public int getY();
	public int getWidth();
	public int getHeight();
	public boolean isEnabled();
	public boolean isVisible();
}
