package cuina.widget;

import cuina.event.Event;
import cuina.event.Trigger;
import cuina.input.Input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.matthiasmann.twl.AnimationState;
import de.matthiasmann.twl.Widget;

/**
 * @author TheWhiteShadow
 */
public class CuinaWidget extends Widget
{
	private String name;
	List<Trigger> triggers;
	
	public CuinaWidget()
	{
		this(null, false);
	}

	public CuinaWidget(AnimationState animState, boolean inherit)
	{
		super(animState, inherit);
		setTheme("widget");
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public void addTrigger(Trigger trigger)
	{
		if (triggers == null) triggers = new ArrayList<Trigger>();
		triggers.add(trigger);
	}

	public boolean removeTrigger(Trigger trigger)
	{
		if (triggers == null) return false;
		
		return triggers.remove(trigger);
	}

	public List<Trigger> getTriggers()
	{
		if (triggers == null) return Collections.EMPTY_LIST;
		return Collections.unmodifiableList(triggers);
	}
	
	public int getTriggerCount()
	{
		return (triggers != null) ? triggers.size() : 0;
	}
	
	public void testTriggers(Event event, Object eventArg, Object... callArgs)
	{
		if (triggers == null) return;
		
		for(Trigger trigger : triggers)
		{
			if (trigger.isActive() && trigger.test(event, eventArg))
			{
				Object[] newArgs = new Object[callArgs.length + 1];
				newArgs[0] = this;
				System.arraycopy(callArgs, 0, newArgs, 1, callArgs.length);
				trigger.run(newArgs);
			}
		}
	}
	
	public CuinaWidget find(String name)
	{
		if (name == null) return null;
		
		for (int i = 0; i < getNumChildren(); i++)
		{
			Widget w = getChild(i);
			if (!(w instanceof CuinaWidget)) continue;
			
			CuinaWidget widget = (CuinaWidget) w;
			if (name.equals(widget.getName())) return widget;
			
			widget = widget.find(name);
			if (widget != null) return widget;
		}
		return null;
	}
	
	protected boolean isMouseInside()
	{
		return isInside(Input.mouseX(), Input.mouseY());
	}
	
	protected final void update()
	{
		if (!isVisible()) return;
		updateWidget();
		for(int i = 0; i < getNumChildren(); i++)
		{
			Widget w = getChild(i);
			if (w instanceof CuinaWidget)
				((CuinaWidget) w).update();
		}
	}
	
	@Override
	protected boolean handleEvent(de.matthiasmann.twl.Event evt)
	{
		return evt.isMouseEvent();
	}

	protected void updateWidget() {}
}
