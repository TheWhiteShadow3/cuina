package cuina.test;

import cuina.Input;
import cuina.animation.Model;
import cuina.object.Instantiable;
import cuina.plugin.LifeCycle;
import cuina.world.CuinaMotor;
import cuina.world.CuinaObject;

public class SimpleMotor implements CuinaMotor, LifeCycle, Instantiable
{
	private static final long serialVersionUID = -5597777421125218713L;

	public static int[] DEFAULT_ANIMATION_MASK = new int[] {2, 3, 3, 3, 1, 0, 0, 0};
	
	private CuinaObject object;
	private int speed = 2;
	
	public CuinaObject getObject()
	{
		return object;
	}

	public void setObject(CuinaObject object)
	{
		this.object = object;
	}

	@Override
	public void update()
	{
		int dir = Input.dir4();
		if (dir == -1) return;
		float rad = (float) (dir / 180.0 * Math.PI);
		
		float dx = (float) (speed * +Math.cos(rad));
		float dy = (float) (speed * -Math.sin(rad));

		object.setX(object.getX() + dx);
        object.setY(object.getY() + dy);
        
        Model model = (Model) object.getExtension("model");
        if (model != null)
        {
            int id = Math.round(dir / 45);
            model.setAnimationIndex(DEFAULT_ANIMATION_MASK[id]);
        }
	}

	@Override
	public void init()
	{
		
	}

	@Override
	public void dispose()
	{
		
	}

	@Override
	public Object createInstance(CuinaObject obj) throws Exception
	{
		setObject(obj);
		return this;
	}
	
	@Override
	public void postUpdate() {}
}
