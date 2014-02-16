package cuina.test;

import cuina.plugin.ForScene;
import cuina.plugin.LifeCycle;
import cuina.plugin.Plugin;

/**
 * Klasse für diverse Tests
 */
@ForScene(name="TestConfiguration", scenes="Map")
public class TestConfiguration implements Plugin, LifeCycle
{
	private static final long serialVersionUID = 1L;

	@Override
	public void init()
	{
		System.out.println("[TestConfiguration] init Test.");
		
		CollisionStressTest.setup();
	}

	@Override
	public void update() {}

	@Override
	public void dispose() {}

	@Override
	public void postUpdate() {}
}
