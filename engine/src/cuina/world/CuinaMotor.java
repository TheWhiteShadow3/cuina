package cuina.world;

import java.io.Serializable;

public interface CuinaMotor extends Serializable
{
	public static final String EXTENSION_KEY = "motor";
	
	public void update();
}
