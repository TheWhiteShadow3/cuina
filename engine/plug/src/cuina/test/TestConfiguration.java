package cuina.test;

import cuina.plugin.ForScene;
import cuina.plugin.LifeCycle;
import cuina.plugin.Plugin;

/**
 * Klasse für diverse Tests
 */
@ForScene(name="TestConfiguration", scenes="test")
public class TestConfiguration implements Plugin, LifeCycle
{
	private static final long serialVersionUID = 1L;

	@Override
	public void init()
	{
		System.out.println("[TestConfiguration] init Test.");
		
//		GlobalContext.set("World", new BaseWorld());
//		EventExecuter.init();
//		EventExecuter.runEvent(Database.<Event>get("Event", "test").getCode());
//		EventExecuter.runEvent("puts 'Zeile 1a'\nwait 10\nputs 'Zeile 2a'\nwait 20\nputs 'Zeile 3a'");
//		EventExecuter.runEvent("puts 'Zeile 1b'\nwait 10\nputs 'Zeile 2b'\nwait 20\nputs 'Zeile 3b'");
//		EventTest.setup();
//		AnimationFunctionTest.setup();
//		AnimationStressTest.setup();
	}

	@Override
	public void update()
	{
		
//		GlobalContext.<BaseWorld>get("World").update();
//		if (!Game.getWorld().isFreezed())
//		{
//			EventExecuter.update();
//		}
//		EventTest.update();
//		AnimationFunctionTest.update();
//		AnimationStressTest.update();
	}

	@Override
	public void dispose() {}

	@Override
	public void postUpdate()
	{
//		GlobalContext.<BaseWorld>get("World").postUpdate();
	}
}
