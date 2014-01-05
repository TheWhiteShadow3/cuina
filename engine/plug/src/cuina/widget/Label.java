package cuina.widget;


public class Label extends de.matthiasmann.twl.Label implements CuinaWidget
{
	private String name;
	
	public Label(String name)
	{
		super();
		this.name = name;
		setCanAcceptKeyboardFocus(false);
	}
	
	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public boolean canHandleEvents()
	{
		return false;
	}
	
	@Override
	public WidgetEventHandler getEventHandler()
	{
		return null;
	}
	
	@Override
	public void setEventHandler(WidgetEventHandler handler)
	{
		throw new UnsupportedOperationException();
	}
}
