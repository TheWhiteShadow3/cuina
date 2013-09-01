package cuina.rpg.inventory;


import java.io.Serializable;

/**
 * Das <code>Inventory</code>-Interface definiert methoden zur grundliegenden
 * Verwaltung von Items.
 * @author TheWhiteShadow
 * @version 1.0
 */
public interface Inventory extends Serializable
{
	/**
	 * Fügt dem Inventar ein Item hinzu.<br><br>
	 * <i>Es ist nicht sicher gestellt, dass das Inventar tatsächlich das übergebene Item aufnimmt.
	 * Ob das Inventar mit Objekten oder nur mit Schlüseln arbeitet ist Sache der Implementierung.</i>
	 * @param item Neues Item.
	 * @return <code>true</code>, wenn das Item vom Inventar aufgenommen werden konnte,
	 * andernfalls <code>false</code>.
	 * @see #addItems(String, int)
	 */
	public boolean addItem(Item item);
	
	/**
	 * Fügt dem Inventar die angegebene Menge an Items hinzu.
	 * @param key ID des neuen Items.
	 * @param count Anzahl.
	 * @return Anzahl der Items, die vom Inventar aufgenommen werden konnten.
	 * @see #addItem(Item)
	 */
	public int addItems(String key, int count);
	
	/**
	 * Gibt an, ob das Inventar das angegebene Item besitzt.<br><br>
	 * <i>Es ist nicht sicher gestellt, dass das Inventar tatsächlich das übergebene Item beinhaltet.
	 * Ob das Inventar mit Objekten oder nur mit Schlüseln arbeitet ist Sache der Implementierung.</i>
	 * @param item Zu testenden Items.
	 * @return <code>true</code>, wenn das Item im Inventar vorhanden ist,
	 * andernfalls <code>false</code>.
	 * @see #getItemCount(String)
	 */
	public boolean containsItem(Item item);
	
	/**
	 * Gibt die Anzahl des angegeben Items im Inventar zurück.
	 * @param key ID des zu testenden Items.
	 * @return Anzahl der angegeben Items im Inventar.
	 * @see #containsItem(Item)
	 */
	public int getItemCount(String key);
	
	/**
	 * Entfernt aus dem Inventar ein Item.<br><br>
	 * <i>Es ist nicht sicher gestellt, dass das Inventar tatsächlich das übergebene Item beinhalten muss.
	 * Ob das Inventar mit Objekten oder nur mit Schlüseln arbeitet ist Sache der Implementierung.</i>
	 * @param item Das zu entfernendes Item.
	 * @return <code>true</code>, wenn das Item aus dem Inventar entfernen werden konnte,
	 * andernfalls <code>false</code>.
	 * @see #removeItems(String, int)
	 */
	public boolean removeItem(Item item);
	
	/**
	 * Entfernt aus dem Inventar die angegebene Menge an Items.
	 * @param key ID des zu entfernenden Items.
	 * @param count Anzahl der zu entfernenden Items.
	 * @return Anzahl der Items, die aus dem Inventar entfernt werden werden konnten.
	 * @see #removeItem(Item)
	 */
	public int removeItems(String key, int count);
}
