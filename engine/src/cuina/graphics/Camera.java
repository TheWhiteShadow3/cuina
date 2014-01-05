package cuina.graphics;

import org.lwjgl.util.glu.GLU;

/**
 * Kamera für den 3D-Modus
 * 
 * @author TheWhiteShadow
 */
public class Camera
{
	public float fromX;
	public float fromY;
	public float fromZ;
	
	public float toX;
	public float toY;
	public float toZ = 1;
	
	public float upX;
	public float upY = -1;
	public float upZ;
	
	public Camera()
	{
		
	}
	
	void apply()
	{
		GLU.gluLookAt(fromX, fromY, fromZ,  toX, toY, toZ,  upX, upY, upZ);
	}
}
