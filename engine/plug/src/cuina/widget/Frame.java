package cuina.widget;

import de.matthiasmann.twl.ResizableFrame;

public class Frame extends ResizableFrame implements CuinaWidget
{
	private String name;
	private WidgetEventHandler handler;
	private Runnable eventCB;
	
	public Frame(String name)
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
	
	private void fireCallback()
	{
		if (handler == null) return;

		handler.handleEvent(name, this, null);
	}

	@Override
	public boolean canHandleEvents()
	{
		return true;
	}
	
	@Override
	public WidgetEventHandler getEventHandler()
	{
		return handler;
	}

	@Override
	public void setEventHandler(WidgetEventHandler handler)
	{
		this.handler = handler;
		if (this.handler != null && eventCB == null)
		{
			this.eventCB = new Runnable()
			{
				@Override
				public void run()
				{
					fireCallback();
				}
			};
			addCloseCallback(eventCB);
		}
	}
	
	@Override
	protected void layout()
	{
		int minWidth = getMinWidth();
		int minHeight = getMinHeight();
		if (getWidth() < minWidth || getHeight() < minHeight)
		{
			int width = Math.max(getWidth(), minWidth);
			int height = Math.max(getHeight(), minHeight);
			if (getParent() != null)
			{
				int x = Math.min(getX(), getParent().getInnerRight() - width);
				int y = Math.min(getY(), getParent().getInnerBottom() - height);
				setPosition(x, y);
			}
			setSize(width, height);
		}

        layoutTitle();
        layoutCloseButton();
        layoutResizeHandle();
	}
}
