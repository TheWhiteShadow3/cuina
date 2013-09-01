package cuina.graphics;

/**
 * Kamera für den 3D-Modus
 * 
 * Es wird bereits eine in Graphics erstellt.
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
}
