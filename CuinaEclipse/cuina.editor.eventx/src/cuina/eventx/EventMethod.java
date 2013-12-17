package cuina.eventx;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Gibt an, dass eine Methode vom Interpreter aufgerufen werden kann.
 * Die so gekennzeichneten Methoden können einen Rückgabewert vom Typ {@link Interpreter.Result} haben
 * um dem Interpreter Anweisungen zum nächsten Befehl zu geben.
 * Entspricht der Rückgabewert einem anderen Typ oder <code>null</code>,
 * macht der Interpreter mit der nächsten Anweisung weiter.
 * @author TheWhiteShadow
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface EventMethod
{
	public String alias() default "";
	public String[] args() default {};
}