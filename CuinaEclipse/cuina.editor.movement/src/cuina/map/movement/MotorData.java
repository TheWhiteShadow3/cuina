package cuina.map.movement;

import java.io.Serializable;

public class MotorData implements Serializable
{
	private static final long serialVersionUID = 2554461103902859231L;
	
	public float speed = 0;
	public float direction = 270;
	public float friction = 0;
	public String driver;
}
