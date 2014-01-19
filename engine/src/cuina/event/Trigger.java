package cuina.event;


import java.io.Serializable;

/**
 * Ein Auslöser stellt eine Schnittstelle da zwischen einem Ereignis
 * und einer darauf folgenden Aktion.
 * @author TheWhiteShadow
 */
public interface Trigger extends Serializable
{	
	/**
	 * Gibt an, ob der Auslöser aktive ist und auf Ereignisse reagieren soll.
	 * @return <code>true</code>, wenn der Auslöser aktiv ist andernfalls <code>false</code>.
	 */
	public boolean isActive();
	
	/**
	 * Testet ob das Ereignis zum Auslöser passt.
	 * @param event Das Event
	 * @param arg Das Event-Argument.
	 * @return <code>true</code>, wenn der Auslöser ausgelöst wirtd, andernfalls <code>false</code>.
	 */
	public boolean test(Event event, Object arg);
	
	/**
	 * Startet den im Auslöser definierte Prozess.
	 * @param args Argumente, mit denen der Auslöser ausgelöst werden soll.
	 */
	public void run(Object... args);
}
