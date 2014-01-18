package cuina.widget;

import cuina.event.Event;
import cuina.input.Input;

import de.matthiasmann.twl.AnimationState;
import de.matthiasmann.twl.renderer.AnimationState.StateKey;
import de.matthiasmann.twl.utils.TextUtil;

/**
 * @author TheWhiteShadow
 */
public class Button extends TextWidget
{
	public static final Event BUTTON_PRESSED = Event.getEvent("cuina.widget.ButtonPressed");
	public static final Event STATE_CHANGED = Event.getEvent("cuina.widget.ButtonPressed");

	public static final StateKey STATE_ARMED = StateKey.get("armed");
	public static final StateKey STATE_PRESSED = StateKey.get("pressed");
	public static final StateKey STATE_SELECTED = StateKey.get("selected");
	
	private String control = "c2";
	protected ButtonModel model;
	
	public Button()
	{
		this(null, false, null);
	}

	public Button(AnimationState animState, boolean inherit, ButtonModel model)
	{
		super(animState, inherit);
		setCanAcceptKeyboardFocus(true);
		if (model != null)
		{
			this.model = model;
		}
		else
		{
			this.model = new DefaultButtonModel();
		}
		model.addListener(new Handler());
	}
	
	public Button(String text)
	{
		this(null, false, null);
		setText(text);
	}
	
	public String getControl()
	{
		return control;
	}

	public void setControl(String control)
	{
		this.control = control;
	}

	public ButtonModel getModel()
	{
		return model;
	}

	@Override
	protected void widgetDisabled()
	{
		disarm();
	}

	public String getText()
	{
		return (String) getCharSequence();
	}

	public void setText(String text)
	{
		super.setCharSequence(TextUtil.notNull(text));
		invalidateLayout();
	}

    public boolean isSelected()
    {
        return model.isSelected();
    }

    public void setSelected(boolean b)
    {
        model.setSelected(b);
    }
	
	@Override
	public int getMinWidth()
	{
		return Math.max(super.getMinWidth(), getPreferredWidth());
	}

	@Override
	public int getMinHeight()
	{
		return Math.max(super.getMinHeight(), getPreferredHeight());
	}

	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		if (!visible)
		{
			disarm();
		}
	}
	
	private void updateState(int state)
	{
		setEnabled(model.isEnabled());
		AnimationState as = getAnimationState();
		as.setAnimationState(STATE_HOVER, model.isHover());
		as.setAnimationState(STATE_SELECTED, model.isSelected());
		as.setAnimationState(STATE_ARMED, model.isArmed());
		as.setAnimationState(STATE_PRESSED, model.isPressed());
		testTriggers(STATE_CHANGED, null, state);
	}
	
	public void click()
	{
		model.setPressed(true);
		model.setArmed(true);
	}

	protected void disarm()
	{
		// disarm first to not fire a callback
		model.setHover(false);
		model.setArmed(false);
		model.setPressed(false);
	}
	
	@Override
	protected void updateWidget()
	{
		if (!isVisible() || !isEnabled()) return;
		
		boolean hover = isMouseInside();
		model.setHover(hover);
		if (hover && Input.isPressed("mouse.left") || Input.isPressed(control))
		{
			model.setPressed(true);
			model.setArmed(true);
		}
		else
		{
			model.setPressed(model.isPressed() && Input.isPressed("mouse.left"));
			model.setArmed(hover && model.isPressed());
		}
	}
	
	class Handler implements Listener
	{
		@Override
		public void handleEvent(WidgetEvent event)
		{
			if (event.type == WidgetEvent.STATE_CHANGED)
			{
				updateState(event.value);
			}
			else if (event.type == WidgetEvent.ACTION_PERFORMED)
			{
				testTriggers(BUTTON_PRESSED, null);
			}
		}
	}
}
