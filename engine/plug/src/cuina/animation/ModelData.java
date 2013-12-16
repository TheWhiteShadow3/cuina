package cuina.animation;

import cuina.database.DatabaseObject;
import cuina.object.Instantiable;
import cuina.world.CuinaObject;

import java.io.Serializable;

public class ModelData implements Serializable, Instantiable, DatabaseObject
{
	private static final long	serialVersionUID	= 750069947673148008L;
	
	public String key;
	public String name;
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
	public String getName()
	{
		return name;
	}

	@Override
	public void setKey(String key)
	{
		this.key = key;
	}

	@Override
	public String getKey()
	{
		return key;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}
	
	@Override
	public ModelImpl createInstance(CuinaObject obj) throws Exception
	{
		ModelImpl model = new ModelImpl(fileName, frames, animations, standAnimation);
		model.setObject(obj);
		model.setOffset(ox, oy);
		if (animator != null)
		{
			model.setAnimator((Animator) Class.forName(animator).newInstance());
		}
		return model;
	}
}