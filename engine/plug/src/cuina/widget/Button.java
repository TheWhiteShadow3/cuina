package cuina.widget;

import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.SimpleButtonModel;
import de.matthiasmann.twl.model.ToggleButtonModel;


public class Button extends de.matthiasmann.twl.Button implements CuinaWidget
{
	private final String name;
	private WidgetEventHandler handler;
	private Runnable eventCB;

	public Button(String name, boolean toggleButton)
	{
		super(null, (toggleButton) ? new ToggleButtonModel() : new SimpleButtonModel());
		this.name = name;
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
//	
//	@Override
//	public int getPreferredInnerWidth()
//	{
//		int width = super.getPreferredInnerWidth();
//		
//		if (getNumChildren() > 0)
//		{
//			
//		}
//		
//		return width;
//	}
//
//	@Override
//	public int getPreferredInnerHeight()
//	{
//		return super.getPreferredInnerHeight();
//	}

	@Override
	protected void layout()
	{
		if (getWidth() == 0 || getHeight() == 0)
		{
			int width = getPreferredInnerWidth();
			int height = getPreferredInnerHeight();
			
			Widget child;
			for (int i = 0, n = getNumChildren(); i < n; i++)
			{
				child = getChild(i);
				child.setPosition(getInnerX(), getInnerY());
				child.validateLayout();
				width = Math.max(width, child.getWidth());
				height = Math.max(height, child.getHeight());
			}
			
			setInnerSize(width, height);
		}
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
			addCallback(eventCB);
		}
	}

	public boolean isActive()
	{
		return getModel().isSelected();
	}
	
	public void setActive(boolean value)
	{
		getModel().setSelected(value);
	}
}
