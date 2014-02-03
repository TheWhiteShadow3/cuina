package cuina.transition;

import cuina.Game;
import cuina.eventx.EventMethod;
import cuina.eventx.Interpreter.Result;
import cuina.graphics.GLCache;
import cuina.graphics.Graphics;
import cuina.graphics.Image;
import cuina.graphics.Images;
import cuina.graphics.Sprite;
import cuina.graphics.View;
import cuina.plugin.ForGlobal;
import cuina.plugin.LifeCycle;
import cuina.plugin.Plugin;
import cuina.world.CuinaWorld;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;


@ForGlobal(name="Transition")
public class Transition implements Plugin, LifeCycle
{
	private static final long serialVersionUID = -1510952149775314923L;
	
	transient private TransitionSprite sprite;
	
	public static final int NONE  			= 0;
	public static final int WAIT			= 1;
	public static final int FREEZE_WORLD 	= 2;
	
	private static final int FADE_IN  		= 4;
	private static final int FADE_OUT  		= 8;
	private static final int FADE_SCREEN  	= 16;
	private static final int COLORIZE  		= 32;
	
	private int flags;

	public Transition() {}
	
	@Override
	public void init()
	{
		sprite = new TransitionSprite();
		sprite.setVisible(false);
		sprite.setDepth(1000);
	}

	@Override
	public void update()
	{
		sprite.update();
		if (!sprite.isFading() && flags != 0)
		{
			finishFade();
		}
	}

	private void finishFade()
	{
		if ((flags & FREEZE_WORLD) != 0) startWorld();
		flags = NONE;
	}
	
	@Override
	public void dispose()
	{
		sprite.dispose();
	}
	
	@EventMethod
	public Result fadeOut(int duration, int flags)
	{
		this.flags = FADE_OUT | flags;
		
		if ((flags & FADE_SCREEN) != 0)
		{
			sprite.setColor(Color.WHITE, 0);
			createScreenShot(0);
			this.flags |= WAIT;
			duration = 1;
		}
		else
		{
			sprite.setColor(Color.BLACK, duration);
		}
		System.out.println("Start fade out. Frames: + " + duration);
		
		return getInterpreterResult(duration, this.flags);
	}
	
	@EventMethod
	public Result fadeIn(int duration, int flags)
	{
		this.flags = FADE_IN | flags;
//		sprite.setColor(Color.BLACK, 0);
		sprite.setColor(Image.COLOR_TRANSPARENT, duration);
		System.out.println("Start fade in. Frames: + " + duration);
		
		return getInterpreterResult(duration, this.flags);
	}
	
	private Result getInterpreterResult(int duration, int flags)
	{
		if ((flags & WAIT) != 0)
		{
			return new Result(duration, 0, false);
		}
		else if ((flags & FREEZE_WORLD) != 0)
		{
			stopWorld();
			return Result.WAIT_ONE_FRAME;
		}
		else return Result.DEFAULT;
	}
	
	@EventMethod
	public void colorize(int red, int green, int blue, int alpha, int duration)
	{
		colorize(new Color(red, green, blue, alpha), duration);
	}
	
	public void colorize(ReadableColor color, int duration)
	{
		flags = COLORIZE;
		sprite.setColor(color, duration);
	}
	
	private void stopWorld()
	{
		CuinaWorld world = Game.getWorld();
		if (world != null) world.setFreeze(true);
	}

	private void startWorld()
	{
		CuinaWorld world = Game.getWorld();
		if (world != null) world.setFreeze(false);
	}
	
	private void createScreenShot(int viewID)
	{
		sprite.getImage().drawView(viewID, 0, 0);
	}
	
	static class TransitionSprite extends Sprite
	{
		private static final long serialVersionUID = -2672794989820109178L;
		
		private ReadableColor dstColor;
		private float red, green, blue, alpha;
		private int duration;

		public TransitionSprite()
		{
			super(createImage());
		}
		
		public boolean isFading()
		{
			return duration > 0;
		}
		
		public void update()
		{
			if (duration > 0)
			{
				duration--;
				red   = (red   * duration + dstColor.getRed())   / (duration + 1);
				green = (green * duration + dstColor.getGreen()) / (duration + 1);
				blue  = (blue  * duration + dstColor.getBlue())  / (duration + 1);
				alpha = (alpha * duration + dstColor.getAlpha()) / (duration + 1);
				
				getImage().getColor().set((int) red, (int) green, (int) blue, (int) alpha);
				setVisible(alpha > 0);
				System.out.println("[TransitionSprite] Fade-Color: " + getImage().getColor());
			}
		}

		@Override
		public void refresh()
		{
			setImage(createImage());
		}
		
		private static Image createImage()
		{
			Image image = Images.createImage(Graphics.getWidth(), Graphics.getHeight());
			image.clear(ReadableColor.WHITE);
			image.setColor(Image.COLOR_TRANSPARENT);
			return image;
		}
		
		public void setColor(ReadableColor color, int duration)
		{
			this.dstColor = color;
			this.duration = duration;
			if (duration <= 0)
				getImage().setColor(color);
			else
			{
				Color c = getImage().getColor();
				red = c.getRed();
				green = c.getGreen();
				blue = c.getBlue();
				alpha = c.getAlpha();
			}
		}

		@Override
		protected void render(Image image)
		{
			View view = View.getCurrent();
			GLCache.setMatrix(GL11.GL_MODELVIEW);
			GL11.glTranslatef(view.x, view.y, 0);
			super.render(image);
		}
	}
	
//	public static void main(String[] args)
//	{
//		int srcValue = 128;
//		int curValue = srcValue;
//		int duration = 10;
//		int dstValue = 64;
//		
//		System.out.println(curValue);
//
//		while(duration > 0)
//		{
//			if (duration > 0)
//			{
//				duration--;
//				
//				curValue = (int) (srcValue + (dstValue - curValue) * (startDuration - duration));
////				curValue = (curValue * duration + dstValue) / (duration + 1);
//				System.out.println(curValue);
//			}
//		}
//	}

	@Override
	public void postUpdate() {}
}
