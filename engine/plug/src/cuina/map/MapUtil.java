package cuina.map;

import cuina.eventx.EventMethod;
import cuina.map.movement.Motor;
import cuina.object.BaseObject;
import cuina.plugin.Plugin;

@SuppressWarnings("serial")
public class MapUtil implements Plugin
{
	@EventMethod
	public static void moveObject(BaseObject obj, float dir, float dist, boolean turn)
	{
		Motor motor = (Motor) obj.getExtension(Motor.EXTENSION_KEY);
		motor.moveDir(dir, dist, turn);
		obj.update();
	}
	
	@EventMethod
	public static void turnToTarget(BaseObject obj, BaseObject target)
	{
		Motor motor = (Motor) obj.getExtension(Motor.EXTENSION_KEY);
		motor.turn(target, 360);
		obj.update();
	}
	
	@EventMethod
	public static void turnObject(BaseObject obj, float dir)
	{
		Motor motor = (Motor) obj.getExtension(Motor.EXTENSION_KEY);
		motor.setDirection(dir);
		obj.update();
	}
}
