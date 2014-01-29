package cuina.world;

import java.io.Serializable;

/**
 * Ein Model für ein Spielobjekt.
 * <p>
 * Ein Model ist im Gegensatz zu einer Grafik nicht auf ein einzelnes Bild beschränkt
 * und erlaubt es auch diese zu animieren. 
 * </p>
 * @author TheWhiteShadow
 */
public interface CuinaModel extends Serializable
{
	public static final String EXTENSION_KEY = "model";
	
	/**
	 * Erneuert die Grafiken des Models.
	 */
	public void refresh();
	/**
	 * Setzt die Position des Models.
	 * Deise Methode sollte nach jeder Bewegung des darzustellenden Objekts aufgerufen werden. 
	 * @param x X-Position
	 * @param y Y-Position
	 * @param z Z-Position
	 */
	public void setPosition(float x, float y, float z);
	
	public void dispose();
	public void setVisible(boolean value);
	public boolean isVisible();
	
	public float getZ();
	public float getX();
	public float getY();

	public int getWidth();
	public int getHeight();
	
	/**
	 * Aktualisiert Animationen des Models.
	 */
	public void update();
}
