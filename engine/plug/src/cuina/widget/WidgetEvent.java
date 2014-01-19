package cuina.widget;

public class WidgetEvent
{
	public static final int STATE_CHANGED = 1;
	public static final int ACTION_PERFORMED = 2;
	
	public final Object source;
	public final int type;
	public int value;
	public int text;
	
	public WidgetEvent(Object source, int type)
	{
		this.source = source;
		this.type = type;
	}
}
