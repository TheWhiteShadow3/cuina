package cuina.widget;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeListener;

public class DefaultButtonModel implements ButtonModel
{
	private static final long serialVersionUID = 3787711009565990586L;
	
	protected int state;
	protected ButtonGroup group;
	
	protected List<Listener> listeners = new ArrayList<Listener>();

    public DefaultButtonModel()
    {
    	state = 0;
        setEnabled(true);
    }

	@Override
	public boolean isEnabled()
	{
		return (state & ENABLED) != 0;
	}

	@Override
	public boolean isHover()
	{
		return (state & HOVER) != 0;
	}

	@Override
	public boolean isSelected()
	{
		return false;
	}

	@Override
	public boolean isArmed()
	{
		return (state & ARMED) != 0;
	}

	@Override
	public boolean isPressed()
	{
		return (state & PRESSED) != 0;
	}
	
	@Override
	public void setEnabled(boolean enable)
	{
		if (enable == isEnabled()) return;
		
		setStateBit(ENABLED, enable);
		fireStateChanged(ENABLED);
	}

	@Override
	public void setHover(boolean hover)
	{
		if (hover == isHover()) return;
		
		setStateBit(HOVER, hover);
		fireStateChanged(HOVER);
	}

	@Override
	public void setSelected(boolean selected)
	{
		if (group != null && selected) group.select(this);
	}
	
	@Override
	public void setArmed(boolean armed)
	{
		if (armed == isArmed()) return;
		
		setStateBit(ARMED, armed);
		fireStateChanged(ARMED);
	}

	@Override
	public void setPressed(boolean pressed)
	{
		if (pressed == isPressed()) return;
		
		boolean action = !pressed && isArmed();
		setStateBit(PRESSED, pressed);
		fireStateChanged(PRESSED);
		
		if (action)
		{
			buttonPressed();
			fireButtonPressed();
		}
	}
	
	protected void buttonPressed() {}

	protected void setStateBit(int mask, boolean set)
	{
		if (set)
			state |= mask;
		else
			state &= ~mask;
	}

	@Override
	public void addListener(Listener l)
	{
		listeners.add(l);
	}

	@Override
	public void removeListener(ChangeListener l)
	{
		listeners.remove(l);
	}
	
	protected void fireStateChanged(int state)
	{
		if (listeners.isEmpty()) return;
		
		WidgetEvent event = new WidgetEvent(this, WidgetEvent.STATE_CHANGED);
		event.value = state;
		for(Listener l : listeners)
		{
			l.handleEvent(event);
		}
	}
	
	protected void fireButtonPressed()
	{
		if (listeners.isEmpty()) return;
		
		WidgetEvent event = new WidgetEvent(this, WidgetEvent.ACTION_PERFORMED);
		for(Listener l : listeners)
		{
			l.handleEvent(event);
		}
	}

	@Override
	public void setGroup(ButtonGroup group)
	{
		this.group = group;
	}

	@Override
	public ButtonGroup getGroup()
	{
		return group;
	}
}
