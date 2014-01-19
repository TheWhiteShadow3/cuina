package cuina.widget;

import cuina.Logger;
import cuina.util.ResourceManager;
import cuina.util.ResourceManager.Resource;

import java.io.IOException;

import de.matthiasmann.twl.AnimationState;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.Renderer;
import de.matthiasmann.twl.renderer.Texture;

public class Picture extends CuinaWidget
{
	private Image image;
	private String fileName;

	public Picture()
	{
		this(null, false);
	}
	
	public Picture(AnimationState animState, boolean inherit)
	{
		super(animState, inherit);
	}
	
	public void setImage(String fileName)
	{
		this.fileName = fileName;
		if (fileName == null) image = null;
	}
	
	public void calculateSize()
	{
		if (fileName != null)
		{
			if (image == null) loadImage();
			if (image == null) return;
			setSize(image.getWidth() + getBorderHorizontal(), image.getHeight() + getBorderVertical());
		}
	}
	
	@Override
	protected void layout()
	{
		calculateSize();
		setVisible(image != null);
	}

	private void loadImage()
	{
		try
		{
			Resource res = ResourceManager.getResource(ResourceManager.KEY_GRAPHICS, fileName);
			Renderer renderer = getGUI().getRenderer();
			Texture texture = renderer.loadTexture(res.getURL(), null, null);
			image = texture.getImage(0, 0, texture.getWidth(), texture.getHeight(),
					null, false, Texture.Rotation.NONE);
		}
		catch (IOException e)
		{
			Logger.log(Picture.class, Logger.ERROR, e);
		}
	}
	
	@Override
	protected void paintWidget(GUI gui)
	{
		if (image == null) return;
		
		image.draw(getAnimationState(), super.getInnerX(), super.getInnerY());
	}
}
