package cuina.widget;

import de.matthiasmann.twl.AnimationState;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Widget;


/**
 * @author TheWhiteShadow
 */
public class RadioButton extends ToggleButton
{
	public RadioButton(AnimationState animState, boolean inherit)
	{
		super(animState, inherit);
	}

	public RadioButton()
	{
		super(null, false);
	}

	public RadioButton(String text)
	{
		super(text);
	}
	
	@Override
	protected void afterAddToGUI(GUI gui)
	{
		int index = getParent().getChildIndex(this);
		if (index > 0)
		{
			Widget child = getParent().getChild(index-1);
			if (child instanceof Button)
			{
				ButtonGroup g = ((Button) child).getModel().getGroup();
				if (g != null)
				{
					getModel().setGroup(g);
					return;
				}
			}
		}
		getModel().setGroup(new ButtonGroup());
	}
}
