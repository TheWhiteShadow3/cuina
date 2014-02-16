package cuina.world;

import cuina.plugin.Upgradeable;
import cuina.util.Rectangle;

import java.io.Serializable;

/**
 * Eine Kollisions-Maske, welche den physikalischen Körper für ein Objekt dar stellt.
 * @author TheWhiteShadow
 */
public interface CuinaMask extends Serializable
{
	/**
	 * Der Name für eine mögliche Objekterweiterung.
	 * Falls das Objekt das Interface {@link Upgradeable} implementiert,
	 * sollte die Maske unter diesen Namen am Objekt verankert werden.
	 */
	public static final String EXTENSION_KEY = "box";
	
	/**
	 * Gibt an, ob die Maske Durchlässig ist.
	 * Eine durchlässige Maske kann zwar kollidieren, stellt aber kein Hindernis für andere Masken da.
	 * @return <code>true</code>, wenn die Maske durchlässig ist, anderenfalls <code>false</code>.
	 */
	public boolean isThrough();
	
//	/**
//	 * Setzt den Durchlässigkeits-Status für die Maske.
//	 * Eine durchlässige Maske kann zwar kollidieren, stellt aber kein Hindernis für andere Masken da.
//	 * @param value <code>true</code> für Durchlässig oder <code>false</code> für Undurchlässig.
//	 */
//	public void setThrough(boolean value);
	
	/**
	 * Gibt die Hülle, relativ zum Objekt zurück.
	 * @return Kollisions-Hülle.
	 * @see #getRectangle()
	 */
	public Rectangle getBounds();
	
	/**
	 * Gibt die absolute Position der Hülle in der Welt zurück.
	 * Die Position des Rechtecks entspricht der Hüllen-Position addiert mit der Objekt-Position.
	 * @return Rechteck der Kollisionsmaske.
	 * @see #getBounds()
	 */
	public Rectangle getRectangle();
	
	/**
	 * Gibt an, ob sich die Kollisionsmaske mit einer anderen überschneidet.
	 * <p>
	 * Wenn sich die Rechtecke zweier Masken überschneiden heißt das nicht, dass es die Masken auch tun.
	 * Anders herum wenn sich zwei Masken überschneiden, überschneiden sich auch immer die Rechtecke.
	 * </p>
	 * @param other Die Maske, die mit dieser verglichen werden soll.
	 * @return <code>true</code>, wenn sich die MAsken überschneiden, andernfalls <code>false</code>.
	 * @see #intersectsOn(float, float, float, CuinaMask)
	 */
	public boolean intersects(CuinaMask other);
	
	/**
	 * Ähnlich wie intersects(CuinaMask), aber mit einem Offset für die eigene Position
	 * gibt die Methode an ob sich die Kollisionsmaske mit einer anderen überschneidet.
	 * @param x X-Position, an der die Maske getestet werden soll.
	 * @param y Y-Position, an der die Maske getestet werden soll.
	 * @param z Z-Position, an der die Maske getestet werden soll.
	 * @param other Die Maske, die mit dieser verglichen werden soll.
	 * @return <code>true</code>, wenn sich die MAsken überschneiden, andernfalls <code>false</code>.
	 * @see #intersects(CuinaMask)
	 */
	public boolean intersectsOn(float x, float y, float z, CuinaMask other);
	
	/**
	 * Testet die Maske zu der angegebenen Position und gibt bei erfolg das erste gefundene Objekt zurück.
	 * @param x X-Position für den Test.
	 * @param y X-Position für den Test.
	 * @param z X-Position für den Test.
	 * @return Das erste gefundene Objekt oder <code>null</code>, wenn nichts gefunden wurde.
	 * @see #isRelativePositionFree(float, float, float)
	 */
	public boolean isAbsolutePositionFree(float x, float y, float z);
	
	/**
	 * Testet die Maske zu der relativ zur Maske angegebenen Position 
	 * und gibt bei erfolg das erste gefundene Objekt zurück.
	 * @param x X-Position für den Test.
	 * @param y X-Position für den Test.
	 * @param z X-Position für den Test.
	 * @return Das erste gefundene Objekt oder <code>null</code>, wenn nichts gefunden wurde.
	 * @see #isAbsolutePositionFree(float, float, float)
	 */
	public boolean isRelativePositionFree(float x, float y, float z);
	
	/**
	 * Verschiebt die Kollisionsbox auf eine neue Postion und prüft auf mögliche Kollisionen.
	 * Die Methode gibt nur true zurück, wenn keine Kollisionen auftraten.
	 * @param newX neue X-Position.
	 * @param newY neue Y-Position.
	 * @param newZ neue Z-Position.
	 * @return <code>true</code>, wenn die Bewegung kollisionsfrei möglich war, andernfalls <code>false</code>.
	 */
	public boolean move(float newX, float newY, float newZ);

	public float getX();

	public float getY();

	public CuinaObject getImpactObject();
}
