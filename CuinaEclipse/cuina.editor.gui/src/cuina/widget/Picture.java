package cuina.widget;

import de.matthiasmann.twl.AnimationState;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.Image;

public class Picture extends Widget
{
	Image image;
	String fileName;
	
	public Picture()
	{
		super();
	}
	
	public Picture(AnimationState animState)
	{
		super(animState);
	}
	
	public void setImage(Image image)
	{
		this.image = image;
	}
	
	public void calculateSize()
	{
		if (image != null)
		{
			setSize(image.getWidth() + getBorderHorizontal(), image.getHeight() + getBorderVertical());
		}
	}
	
	@Override
	protected void layout()
	{
		calculateSize();
		setVisible(image != null);
	}
	
	@Override
	protected void paintWidget(GUI gui)
	{
		if (image == null) return;
		
		image.draw(getAnimationState(), super.getInnerX(), super.getInnerY());
	}
}
