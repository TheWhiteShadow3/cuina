package cuina.database;

import java.io.Serializable;

/**
 * Stellt die grundliegenden Funktionen für ein Daten-Objekt zur Verfügung.<br>
 * Daten-Objekte werden für das Spiel benötigt um Dinge dar zu stellen wie:
 * Klassen, Items, Ausrüstung, Fähigkeiten, ...
 * @see Database
 */
public interface DatabaseObject extends NamedItem, Serializable
{
	/**
	 * Setzt die ID des Objekts. Der Schlüssel sollte keine Eigenschaft sein,
	 * die im entsprechendem Objekt-Editor gesetzt werden kann,
	 * da er zur eindeutigen Identifizierung des Objekts benötigt wird.
	 * <p>
	 * Der Wert ist beschränkt auf die Zeichen [_a-zA-Z0-9] und darf nicht mit einer Zahl beginnen.
	 * </p>
	 * @param key Den Schlüssel vom Objekt.
	 */
	public void setKey(String key);
	
	/**
	 * Gibt den Schlüssel des Objekts zurück.
	 * @return Den Schlüssel.
	 */
	public String getKey();
	
	/**
	 * Gibt den Namen des Objekts zurück.
	 * @param name Den Namen.
	 */
	public void setName(String name);
}
