package cuina.movement;


import java.io.Serializable;

public interface Driver extends Serializable
{
	public void init(Motor motor);
	public void update();
	public void blocked();
}
