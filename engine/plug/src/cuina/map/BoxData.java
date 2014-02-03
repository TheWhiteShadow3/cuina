package cuina.map;

import cuina.object.Instantiable;
import cuina.world.CuinaObject;

import java.io.Serializable;

public class BoxData implements Instantiable, Serializable
{
	private static final long serialVersionUID = 7842295701110370417L;
	
	public int x;
	public int y;
	public int width;
	public int height;
	
	public boolean through;
	public int alphaMask = 1;
	
	@Override
	public CollisionBox createInstance(CuinaObject obj) throws Exception
	{
		return new CollisionBox(obj, this);
	}
}
