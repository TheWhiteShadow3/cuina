package cuina.transition;

import cuina.Game;
import cuina.animation.Model;
import cuina.graphics.Image;
import cuina.graphics.Images;
import cuina.graphics.Sprite;
import cuina.plugin.ForScene;
import cuina.plugin.LifeCycleAdapter;
import cuina.plugin.Plugin;
import cuina.util.LoadingException;
import cuina.world.CuinaObject;

import org.lwjgl.util.Color;

@SuppressWarnings("serial")
@ForScene(name = "MirrorTest", scenes="Map")
public class MirrorTest extends LifeCycleAdapter implements Plugin
{
	private Image groundMask;
	private Sprite sprite;
	
	@Override
	public void init()
	{
		try
		{
			groundMask = Images.createImage("pictures/Schule_ground-mask.png");
			sprite = new Sprite(null)
			{
				@Override
				public void refresh()
				{
					sprite.setImage(Images.createImage(groundMask.getWidth(), groundMask.getHeight()));
					sprite.getImage().setBlendMode(Image.COMPOSITE_NORMAL);
				}
			};
			sprite.refresh();
			sprite.setDepth(100);
		}
		catch (LoadingException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void postUpdate()
	{
		Image img = sprite.getImage();
		img.clear(Image.COLOR_TRANSPARENT);
		
		CuinaObject obj = Game.getWorld().getObject(1_000_001);
		Model model = (Model) obj.getExtension(Model.EXTENSION_KEY);
		Sprite s = model.getSprite();
		
//		img.setColor(Color.RED);
//		img.drawRect(0, 0, img.getWidth(), img.getHeight(), true);
		img.setBlendMode(Image.COMPOSITE_NORMAL);
		img.setColor(Color.WHITE);
		img.drawImage((int) s.getX(), (int) s.getY(), s.getImage());
		
		img.drawImage(0, 0, groundMask);
		img.setBlendMode(Image.COMPOSITE_ADD);
	}

	@Override
	public void dispose()
	{
		groundMask.dispose();
		sprite.dispose();
	}
}
