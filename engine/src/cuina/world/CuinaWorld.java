package cuina.world;

import cuina.graphics.GraphicContainer;

import java.io.Serializable;
import java.util.Set;

/**
 * Das Interface für die Spielwelt.
 * @author TheWhiteShadow
 */
public interface CuinaWorld extends Serializable
{
	/**
	 * Der Schlüssel für den Session-Kontext um auf die Welt zuzugreifen.
	 * Implementierungen sind nicht gezwungen diese Konstante als Schlüssel zu benutzen.
	 * Allerdings macht es Sinn sich einheitlich daran zu halten.
	 */
	public static final String INSTANCE_KEY = "World";

	/**
	 * Gibt ein Set von IDs aller in der Welt existierender Objekte zurück.
	 * @return Set aller Objekte.
	 */
	public Set<Integer> getObjectIDs();
	
	/**
	 * Gibt die Anzahl aller in der Welt existierender Objekte zurück.
	 * @return Anzahl aller Objekte.
	 */
	public int getObjectCount();
	
	/**
	 * Gibt eine verfügbare Objekt-ID zurück.
	 * @return eine verfügbare Objekt-ID.
	 */
	public int getAvilableID();
	
	/**
	 * Fügt der Welt ein neues Objekt hinzu.
	 * Das Objekt muss eine verfügbare ID besitzen.
	 * Um eine verfügbare ID zu erhalten kann getAvilableID() aufgerufen werden.
	 * @param obj Das Objekt.
	 * @return <code>true</code>, wenn das Objkte zur Welt hinzugefügt werden konnte, andernfalls <code>false</code>.
	 */
	public boolean addObject(CuinaObject obj);
	
	/**
	 * Gibt das Objekt mit der angegebenen ID zurück.
	 * Wenn das Objekt nicht existiert wird <code>null</code> zurückgegeben.
	 * @param id ID des Objekts.
	 * @return Objekt zur angegebenen ID oder <code>null</code>.
	 */
	public CuinaObject getObject(int id);
	
	/**
	 * Entfernt das angegebenen Objekt aus der Welt.
	 * Wenn das angegebene Objekt nicht in der Welt existiert, macht die Methode nichts.
	 * @param obj Das Objekt.
	 */
	public void removeObject(CuinaObject obj);
	
	/**
	 * Entfernt das Objekt mit der angegebenen ID aus der Welt.
	 * Wenn das entsprechende Objekt nicht in der Welt existiert, macht die Methode nichts.
	 * @param id Die Objekt-ID.
	 */
	public void removeObject(int id);

	/**
	 * Aktualisiert die Welt um ein Frame.
	 */
	public void update();
	
	/**
	 * Zerstört die Welt und alle beinhaltenden Objekte.
	 */
	public void dispose();
	
	/**
	 * Friert die Welt ein.
	 * Eine eingefrorende Welt ignoriert Aufrufe von update().
	 */
	public boolean isFreezed();
	
	/**
	 * Setzt den Einfrierzustand der Welt.
	 * 
	 * @param value true, um die Welt einzufrieren oder false um sie aufzutauen.
	 */
	public void setFreeze(boolean value);
	
	/**
	 * Gibt die Breite der Welt zurück.
	 * @return die Breite der Welt.
	 */
	public int getWidth();
	
	/**
	 * Gibt die Breite der Höhe zurück.
	 * @return die Höhe der Welt.
	 */
	public int getHeight();

	/**
	 * Gibt den Grafik-Kontainer der Welt zurück.
	 * Dieser wird benutzt um die Umgebung und alle Objekte anzuzeigen.
	 * <p>
	 * Bei Objekten, die einen anderen Kontainer benutzen kann es zu Darstellungsproblemen kommen.
	 * </p>
	 * @return Der Grafik-Kontainer der Welt.
	 */
	public GraphicContainer getGraphicContainer();
}
