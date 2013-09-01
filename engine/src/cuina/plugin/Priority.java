package cuina.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 
 * Beschreibut die Prioritäten einer Klasse für das LifeCycle-Management (Optional).
 * Die Klasse muss dazu das Interface LifeCycle implementieren.
 * Wenn diese Annotation benutzt wird, können die Aufruf-Prioritäten der drei Methoden aus dem Interface
 * im Verhältnis zu den übrigen LifeCycle-Objekten festgelegt werden.
 * <p>
 * Es können für die Werte beliebige Integer-Zahlen genommen werden.
 * Empfohlen ist ein Wert zwischen -1000 und +1000, wobei die Grenzwerte möglichst vermieden werden sollten.
 * Prioritäten der Build-in-Objekte können aus der Dokumentation entnommen werden.
 * TODO: Doku erstellen!
 * </p>
 * 
 * @see LifeCycle
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface Priority
{
	/** Priorität, wann die Initialisierung erfolgen soll. Der Defaultwert ist <code>0</code>. */
	int initPriority() default 0;
	
	/** Priorität, wann der Update erfolgen soll. Der Defaultwert ist <code>0</code>. */
	int updatePriority() default 0;
	
	/** Priorität, wann der Dispose erfolgen soll. Der Defaultwert ist <code>0</code>. */
	int disposePriority() default 0;
}
