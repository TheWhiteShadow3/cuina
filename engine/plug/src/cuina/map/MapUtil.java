package cuina.map;

import cuina.animation.Model;
import cuina.eventx.EventMethod;
import cuina.graphics.Graphics;
import cuina.graphics.Panorama;
import cuina.movement.Motor;
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
	public static void setObjectPosition(BaseObject obj, float x, float y, float dir)
	{
		obj.setX(x);
		obj.setY(y);
		if (dir != -1) turnObject(obj, dir);
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
	
	@EventMethod
	public static void setAnimation(BaseObject obj, int frame, int animation)
	{
		Model model = (Model) obj.getExtension(Model.EXTENSION_KEY);
		if (frame != -1) model.setFrame(frame);
		if (animation != -1) model.setAnimationIndex(animation);
	}
	
	@EventMethod
	public static void setPanorama(int id, String fileName, 
			float speedX, float speedY, float zoomX, float zoomY, boolean foreground)
	{
		Panorama panorama;
		if (foreground)
		{
			panorama = new Panorama(fileName, Graphics.GraphicManager);
			panorama.setDepth(100);
		}
		else
			panorama = new Panorama(fileName);
		panorama.setSpeedX(speedX);
		panorama.setSpeedY(speedY);
		panorama.setZoomX(zoomX);
		panorama.setZoomY(zoomY);
		GameMap.getInstance().setPanorama(id, panorama);
	}
}
