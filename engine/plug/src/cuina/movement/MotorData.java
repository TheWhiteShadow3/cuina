package cuina.movement;

import cuina.object.Instantiable;
import cuina.world.CuinaObject;

import java.io.Serializable;

public class MotorData implements Serializable, Instantiable
{
	private static final long serialVersionUID = 2554461103902859231L;
	
	public float speed = 0;
	public float direction = 270;
	public float friction = 0;
	public String driver;
	
	@Override
	public Object createInstance(CuinaObject obj) throws Exception
	{
		Motor motor = new Motor(obj, speed, friction, direction);
		if (driver != null)
		{
			motor.setDriver((Driver) Class.forName(driver).newInstance());
		}
		return motor;
	}
}
