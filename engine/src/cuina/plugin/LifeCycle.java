package cuina.plugin;


/**
 * Definiert Methoden für ein LifeCycle-Management, welches die 3 Zustände
 * Geburt, Leben und Tod beinhalten.
 * Diese Zustände werden durch die definierten Methoden dargestellt.
 * Der Lebenszyklus erstreckt sich über die Lebenszeit einer Spielszene,
 * welche das LifeCycle-Objekt auch Verwaltet.
 * <p>
 * Klassen, die dieses Interface implementieren müssen einen Konstruktor ohne Parameter besitzen.
 * </p>
 * <p>
 * Für Plugin-Klassen sieht der Ablauf so aus, dass ein Objekt einem der drei Kontexte zugewiesen wird.
 * Sobalt eine Szene erstellt wird in der das Objekt gültig ist,
 * wird auch das LifeCycle-Objekt aus dem Kontext aktiviert.
 * Zuerst wird immer die Methode <code>init()</code> aufgerufen,
 * danach beliebig oft <code>update()</code> und <code>postUpdate()</code> und zum Schluss <code>dispose()</code>.
 * </p>
 * <p>
 * Die Instanz des Objekts kann durchaus mehrere Szenen durchleben. Und zwar, wenn es sich um ein globales
 * oder Session-Objekt handelt oder als Szenen-Objekt mit {@link ForScene#persistent()} gekennzeichnet ist.
 * </p>
 * @see cuina.Scene
 * @see Priority
 * @see Plugin
 * @author TheWhiteShadow
 */
public interface LifeCycle
{
	/**
	 * Diese Methode wird zu Beginn einer Szene aufgerufen um das Objekt zu initialisieren.
	 */
	public void init();
	
	/**
	 * Diese Methode wird jeden Frame aufgerufen.
	 */
	public void update();
	
	/**
	 * Diese Methode wird nach {@link #update()} aufgerufen.
	 * Der Zustand des Objekts wird hier festgemacht aber nicht mehr verändert.
	 * Die Methode dient hauptsächlich für Updates von grafischen Elementen.
	 */
	public void postUpdate();
	
	/**
	 * Diese Methode wird aufgerufen, wenn die Szene beendet wird.
	 */
	public void dispose();
}
