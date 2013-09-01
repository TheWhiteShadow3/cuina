package cuina.event;


import java.io.Serializable;

/**
 * Ein Trigger stellt eine Schnittstelle da zwischen einem Ereignis
 * und einer darauf folgenden Aktion.
 * @author TheWhiteShadow
 */
public interface Trigger extends Serializable
{
	/**
	 * Ereignis, bei dem der Trigger auslösen soll.
	 * @return den Typ.
	 */
	public Event getEvent();
	
	/**
	 * Ein zusätzliches Argument, welches zum Ereignis gehört.
	 * @return das Event Argument.
	 */
	public Object getEventArg();
	
	/**
	 * Gibt an, ob der Trigger aktive ist und auf Ereignisse reagieren soll.
	 * @return <code>true</code>, wenn der Trigger aktiv ist andernfalls <code>false</code>.
	 */
	public boolean isActive();
	
	/**
	 * Testet ob das Ereignis zum Trigger passt.
	 * @param event Das Event
	 * @param arg Das Event-Argument.
	 * @return <code>true</code>, wenn der Trigger ausgelöst wirtd, andernfalls <code>false</code>.
	 */
	public boolean test(Event event, Object arg);
	
	/**
	 * Startet das im Trigger definierte Ereignis.
	 * @param args Argumente, mit denen der Trigger ausgelöst werden soll.
	 */
	public void run(Object... args);
}
