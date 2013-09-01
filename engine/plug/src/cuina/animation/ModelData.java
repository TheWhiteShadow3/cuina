package cuina.animation;

import cuina.object.Instantiable;
import cuina.world.CuinaObject;

import java.io.Serializable;

public class ModelData implements Serializable, Instantiable
{
	private static final long	serialVersionUID	= 750069947673148008L;
	
	public String fileName = null;
	public int frames = 1;
	public int animations = 1;
	public int frame = 0;
	public int animation = 0;
	public boolean standAnimation = false;
	public int ox = 0;
	public int oy = 0;
	public String animator;
	
	@Override
	public Model createInstance(CuinaObject obj) throws Exception
	{
		Model model = new Model(fileName, frames, animations, standAnimation);
		model.setObject(obj);
		model.setOffset(ox, oy);
		if (animator != null)
		{
			model.setAnimator((Animator) Class.forName(animator).newInstance());
		}
		return model;
	}
}