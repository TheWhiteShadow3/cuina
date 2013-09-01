package cuina.world;

import java.io.Serializable;

public interface CuinaModel extends Serializable
{
	public static final String EXTENSION_KEY = "model";
	
	public void refresh();
	/**
	 * Setzt die Position des Models.
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
	 * Aktualisiert Animationen.
	 */
	public void update();
}
