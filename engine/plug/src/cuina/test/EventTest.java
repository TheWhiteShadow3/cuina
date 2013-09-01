package cuina.test;

import static cuina.test.TestObjectFactory.createDefaultPlayerObject;

import cuina.FrameTimer;
import cuina.Game;
import cuina.object.BaseObject;
import cuina.object.ObjectData;

public class EventTest
{
	private static BaseObject player;

	public static void setup()
	{
		if (Game.getSession() == null) Game.newGame();
		Game.newScene("Map");
		FrameTimer.syncScene();
		
		ObjectData data = createDefaultPlayerObject("Yuna");
//		createTriggers(data);
		
		player = new BaseObject(data);
		Game.getWorld().addObject(player);
	}
	
	public static void update()
	{
		
	}
	
//	private static void createTriggers(ObjectData data)
//	{
//		MapTrigger trigger = new MapTrigger();
//		trigger.type = MapTrigger.OBJECT_CREATE;
//		trigger.scriptKey = "me_create";
//		trigger.main = "run";
//		
//		data.triggers.add(trigger);
//	}
}
