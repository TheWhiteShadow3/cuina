package cuina.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Gibt an, dass eine Instanz der Klasse für eine bestimmte Szene bereit gestellt werden soll.
 * Benötigt das Attribut <code>name</code> für den Zugriff auf das Objekt über
 * <code>Game.getScene().getObject()</code> und optional das Attribut
 * <code>scenes</code> für den Namen der Szene.
 * Optional kann mit <code>persist</code> angegeben werden, ob die Instanz für jede Szene neu
 * erstellt werden soll (default), oder weiter verwendet werden kann.
 * @author TheWhiteShadow
 * @see ForGlobal
 * @see ForSession
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface ForScene
{
	/**
	 * Name des Objekts, der als Schlüssel in <code>Game.getScene().getObject()</code>
	 * dient um auf das Objekt global zuzugreifen.
	 */
	String name();
	/**
	 * Name der Szenen in der das Objekt gültig ist.
	 * Wenn das Objekt für alle Szenen gültig sein soll,
	 * kann das durch den Schlüsselwert <code>{"_all_"}</code> angegeben werden.
	 */
	String[] scenes() default {"_all_"};
	/**
	 * Dauerhafte Gültigkeit für jeden Szenen-Aufruf.
	 * <p>
	 * Normalerweise wird ein Objekt beim Szenenwechsel zerstört und im Fall,
	 * dass es in der folge-Szene wieder gültig ist, neu instanziert.
	 * Mit setzten dieses Flags wird gewährleistet, dass das Objekt in jeder gültigen Szene
	 * wiederverwendet wird.
	 * </p>
	 */
	boolean persistent() default false;
}
