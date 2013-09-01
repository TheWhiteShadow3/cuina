package cuina.map;

import cuina.object.Instantiable;
import cuina.world.CuinaObject;

import java.awt.Rectangle;

public class BoxData extends Rectangle implements Instantiable
{
	private static final long serialVersionUID = 7842295701110370416L;
	
	public boolean through;
	/**
	 * Typ der Boundingbox:
	 * <ol>
	 * <li>Angepasst (default)</li>
	 * <li>Bildgröße</li>
	 * <li>3/4 zu 1/2 (passend für Charaktere)</li>
	 * <li>Custom</li>
	 * </ol>
	 */
	public int calculationType = 0;
	public int alphaMask = 1;
	
	@Override
	public CollisionBox createInstance(CuinaObject obj) throws Exception
	{
		return new CollisionBox(obj, this);
	}
}
