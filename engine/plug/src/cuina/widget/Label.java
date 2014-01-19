package cuina.widget;

import de.matthiasmann.twl.AnimationState;
import de.matthiasmann.twl.utils.TextUtil;

public class Label extends TextWidget
{
	public Label(String text)
	{
		this(null, false);
		setText(text);
	}
	
	public Label(AnimationState animState, boolean inherit)
	{
		super(animState, inherit);
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
}
