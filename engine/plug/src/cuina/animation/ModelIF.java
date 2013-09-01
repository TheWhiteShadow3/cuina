package cuina.animation;

import cuina.graphics.Sprite;
import cuina.world.CuinaModel;
import cuina.world.CuinaObject;

import java.awt.image.BufferedImage;

public interface ModelIF extends CuinaModel
{
	public int getFrameCount();
	public int getFrame();
	public void setFrame(int frame);
	
	public int getAnimationCount();
	public int getAnimationIndex();
	public void setAnimationIndex(int index);

	public Sprite getSprite();

	public void setOffset(float ox, float oy);
	public float getOX();
	public float getOY();

	public boolean isAnimate();
	public void setAnimate(boolean animate);
	
	public Animator getAnimator();
	public void setAnimator(Animator animator);

	public BufferedImage getRawImage();

	public CuinaObject getObject();

	public void resetAnimation();

	public void setFrameTime(int time);
}
