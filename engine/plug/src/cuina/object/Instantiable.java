package cuina.object;

import cuina.world.CuinaObject;

public interface Instantiable
{
	/**
	 * Erzeugt eine Instanz einer Objekt-Erweiterung.
	 * @param obj Objekt, für das die Erweiterung instanziert werden soll.
	 * @return Instanz der Objekterweiterung. 
	 */
	public Object createInstance(CuinaObject obj) throws Exception;
}
