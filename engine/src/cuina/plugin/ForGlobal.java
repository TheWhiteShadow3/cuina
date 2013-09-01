package cuina.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Gibt an, dass eine Instanz der Klasse bei Spielstart im globalen Kontext bereit gestellt werden soll.
 * Benötigt <code>name</code> für den Zugriff auf das Objekt über <code>Game.Global.get()</code>.
 * @author TheWhiteShadow
 * @see ForSession
 * @see ForScene
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface ForGlobal
{
	/**
	 * Name des Objekts, der als Schlüssel in <code>Game.Global.get()</code>
	 * dient um auf das Objekt global zuzugreifen.
	 */
	String name();
}
