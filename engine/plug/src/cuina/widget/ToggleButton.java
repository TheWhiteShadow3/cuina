package cuina.widget;

import cuina.widget.model.ToggleButtonModel;

import de.matthiasmann.twl.AnimationState;

/**
 * @author TheWhiteShadow
 */
public class ToggleButton extends Button
{
	public ToggleButton(AnimationState animState, boolean inherit)
	{
		super(animState, inherit, new ToggleButtonModel());
	}

	public ToggleButton()
	{
		this(null, false);
	}

	public ToggleButton(String text)
	{
		this(null, false);
		setText(text);
	}
}
