package cuina.world;


import cuina.database.NamedItem;
import cuina.event.Event;
import cuina.event.Trigger;
import cuina.plugin.Upgradeable;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Das Interface für Spielobjekte.
 * @author TheWhiteShadow
 */
public interface CuinaObject extends Serializable, Upgradeable, NamedItem
{
	/**
	 * Gibt die ID des Objekts zurück.
	 * Die ID ist innerhalb einer Welt eindeutig.
	 * @return die ID des Objekts.
	 */
	public int 		getID();
	
	@Override
	public String 	getName();
	
	/**
	 * Setzt den Namen des Objekts.
	 * @param name Name.
	 */
	public void 	setName(String name);
	
	public float 	getX();
	public void 	setX(float x);
	
	public float 	getY();
	public void 	setY(float y);
	
	public float 	getZ();
	public void 	setZ(float z);
	
	public void 	addTrigger(Trigger trigger);
	public boolean 	removeTrigger(Trigger trigger);
	public List<Trigger> getTriggers();
	
	@Override
	public Set<String> 	getExtensionKeys();
	@Override
	public void 	addExtension(String key, Object ext);
	@Override
	public Object 	getExtension(String key);
	
	public void 	update();
	public void 	postUpdate();
	public void 	dispose();
	public boolean 	exists();
	public boolean 	isPersistent();
	
	/**
	 * Testet das übergebenes Ereignis gegen alle gesetzten Auslöser.
	 * Der Auslöser kann durch ein Ereignis-Argument bedingt sein.
	 * Es ist Aufgabe der Auslösers das Ereignis spezifische Argument zu verarbeiten.
	 * <p>
	 * Der Methode können beliebige Parameter für den Auslöser übergeben werden,
	 * die zum Ereignis gehören und den Kontext abbilden.
	 * Z.B. wird bei einem Kollisionsereignis der Kollisions-Partner mit übergeben.
	 * </p>
	 * @param event Das Ereignis.
	 * @param eventArg Das Ereignis-Argument.
	 * @param callArgs Auslöser-Parameter.
	 */
	public void testTriggers(Event event, Object eventArg, Object... callArgs);
}
