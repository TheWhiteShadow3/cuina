package cuina.widget;

import de.matthiasmann.twl.ResizableFrame;

public class Frame extends ResizableFrame implements CuinaWidget
{
	private String key;
	
	public Frame(String key)
	{
		super();
		this.key = key;
		setCanAcceptKeyboardFocus(false);
	}
	
	@Override
	public String getKey()
	{
		return key;
	}

	@Override
	public void setEventHandler(WidgetEventHandler handler)
	{}
	
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
