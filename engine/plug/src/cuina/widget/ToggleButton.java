package cuina.widget;

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
	
	@Override
	public boolean isSelected()
	{
		return model.isSelected();
	}
	
	@Override
	public void setSelected(boolean selected)
	{
		model.setSelected(selected);
	}
}
