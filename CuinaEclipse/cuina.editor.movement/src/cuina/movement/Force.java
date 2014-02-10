package cuina.movement;

import java.io.Serializable;

public class Force implements Serializable
{
	private static final long serialVersionUID = -7956144248346220666L;
	
	public float value;
	public float direction;
	
	public Force(float value, float direction)
	{
		this.value = value;
		this.direction = direction;
	}
}
