package cuina.test;

import cuina.object.Instantiable;
import cuina.plugin.LifeCycle;
import cuina.plugin.Priority;
import cuina.world.CuinaObject;

@Priority(updatePriority=0)
public class DummyExtension implements LifeCycle, Instantiable
{
	private CuinaObject object;
	
	@Override
	public Object createInstance(CuinaObject obj) throws Exception
	{
		this.object = obj;
		return this;
	}

	@Override
	public void init()
	{
		update();
	}

	@Override
	public void update()
	{
		@SuppressWarnings("unused")
		int var = 0;
		// Eine Dummy-Funktion die Zugriffe auf die Objekt-Erweiterungen simuliert.
		for (String key : object.getExtensionKeys())
		{
			Object ext = object.getExtension(key);
			var += ext.hashCode();
		}
	}

	@Override
	public void dispose()
	{
		object = null;
	}
	
	@Override
	public void postUpdate() {}
}
