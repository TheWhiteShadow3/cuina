package cuina.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Gibt an, dass eine Instanz der Klasse für einen Spielstand bereit gestellt werden soll.
 * Klassen, die diese Annotation tragen müssen serialisierbar sein.
 * <p>
 * Benötigt das Attribut <code>name</code> für den Zugriff auf das Objekt über den Session-Kontext.
 * Defaultmäßig ist die Instanz über <code>GameSession.get(name)</code> ansprechbar.
 * </p>
 * @author TheWhiteShadow
 * @see ForGlobal
 * @see ForScene
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface ForSession
{
	/**
	 * Name des Objekts, der als Schlüssel in <code>Game.Session.get()</code>
	 * dient um auf das Objekt global zuzugreifen.
	 */
	String name();
	/**
	 * Name der Szenen in der das Objekt gültig ist.
	 * Wenn das Objekt für alle Szenen gültig sein soll,
	 * kann das durch den Schlüsselwert <code>{"_all_"}</code> angegeben werden.
	 */
	String[] scenes() default {"_all_"};
}
