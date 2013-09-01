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
	 * Setzt die ID des Objekts. Die ID sollte keine Eigenschaft sein,
	 * die im entsprechendem Objekt-Editor gesetzt werden kann,
	 * da sie zur eindeutigen Identifizierung des Objekts benötigt wird.
	 * @param key Der Schlüssel vom Objekt.
	 */
	public void setKey(String key);
	public String getKey();
	public void setName(String name);
}
