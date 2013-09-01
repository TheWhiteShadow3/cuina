package cuina.world;

import java.awt.Rectangle;
import java.io.Serializable;

public interface CuinaMask extends Serializable
{
	public static final String EXTENSION_KEY = "box";
	
	public boolean	isThrough();
	public void 	setThrough(boolean value);
	/**
	 * Die Hülle, relativ zum Objekt.
	 * @return Kollisions-Hülle.
	 */
	public Rectangle getBounds();
	/**
	 * Die Hülle relativ zur Welt.
	 * @return Kollisions-Hülle.
	 */
	public Rectangle getBox();
	public boolean intersects(CuinaMask other);
	public boolean[][] getPixelData();
	
	/**
	 * Verschiebt die Kollisionsbox auf eine neue Postion und prüft ob die Bewegung dort hin erlaubt ist.
	 * @param newX neue X-Position.
	 * @param newY neue Y-Position.
	 * @param useTrigger gibt an, ob die Bewegung Trigger-Ereignisse auslösen soll.
	 * @return <code>true</code>, wenn die Bewegung nach x/y erlaubt ist, andernfalls <code>false</code>.
	 */
	public boolean move(float newX, float newY, boolean useTrigger);
}
