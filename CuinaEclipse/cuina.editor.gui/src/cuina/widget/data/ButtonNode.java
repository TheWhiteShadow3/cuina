package cuina.widget.data;

public class ButtonNode extends WidgetNode
{
	private static final long serialVersionUID = -5416722110870453099L;
	
	public static final int PRESS	= 0;
	public static final int CHECK	= 1;
	public static final int TOGGLE	= 2;
	public static final int RADIO	= 3;
	
	public String text = "";
	public int buttonStyle = PRESS;
}
