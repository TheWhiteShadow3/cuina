package cuina.test;

import cuina.object.Instantiable;
import cuina.plugin.LifeCycle;
import cuina.plugin.Priority;
import cuina.world.CuinaObject;

@Priority(updatePriority=-20)
public class HealthBar implements LifeCycle, Instantiable
{
	private CuinaObject object;
	private cuina.hud.HealthBar bar;
	
	@Override
	public Object createInstance(CuinaObject obj) throws Exception
	{
		this.object = obj;
		return this;
	}

	@Override
	public void init()
	{
		bar = new cuina.hud.HealthBar(0, 0, 32, 4);
		update();
	}

	@Override
	public void update()
	{
		bar.setX(object.getX() - 16);
		bar.setY(object.getY() - 48);
		// XXX: Rest ist zu Debugzwecken
		bar.minus(0.05f);
		
		if (bar.getValue() <= 0)
			object.dispose();
	}

	@Override
	public void dispose()
	{
		object = null;
		bar.dispose();
		bar = null;
	}
	
	@Override
	public void postUpdate() {}
}
