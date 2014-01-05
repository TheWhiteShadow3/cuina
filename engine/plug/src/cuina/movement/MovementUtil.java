package cuina.movement;

import cuina.world.CuinaObject;

public class MovementUtil
{
	private MovementUtil() {}
	
	public static int getDistance(float x1, float y1, float x2, float y2)
	{
		return (int) Math.hypot(x2 - x1,  y2 - y1);
	}
	
	public static int getDistance(CuinaObject o1, CuinaObject o2)
	{
		return getDistance(o1.getX(), o1.getY(), o2.getX(), o2.getY());
	}
	
	public static float getDirection(CuinaObject o1, CuinaObject o2)
	{
		return getDirection(o1.getX(), o1.getY(), o2.getX(), o2.getY());
	}
	
	public static float getDirection(float x1, float y1, float x2, float y2)
	{
		float dx = x2 - x1;
		float dy = y2 - y1;
		
		if (dx == 0 || dy == 0)
		{	// In dem Fall brauchen wir nicht rechnen
			if (dx == 0 && dy == 0) return Float.NaN;
			if (dx == 0)
			{
				return dy > 0 ? 270 : 90;
			}
			else
			{
				return dx > 0 ? 0 : 180;
			}
		}
		
		if (dy > 0)
		{
			return (float) (360 - Math.acos(dx / Math.hypot(dx, dy)) * 180 / Math.PI);
		}
		else
		{
			return (float) (Math.acos(dx / Math.hypot(dx, dy)) * 180 / Math.PI);
		}
	}
}
