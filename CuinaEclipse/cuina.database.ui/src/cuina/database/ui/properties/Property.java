package cuina.database.ui.properties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Definiewrt Eine Eigenschaft für ein Datenbank-Objekt.
 * Die Eigenschaft kann
 * @author TheWhiteShadow
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Property
{
	/**
	 * Angezeigter Name der Eigenschaft.
	 * @return Der Name.
	 */
	public String name() default "";
	
	/**
	 * Die Getter-Methode für die Eigenschaft.
	 * Wenn keine Methode angegeben wird, wird das Feld per Reflektion abgefragt.
	 * <p>Die angegebene Methode muss die Signatur
	 * 
	 * <pre>public T <i>name</i>()</pre>
	 * 
	 * aufweisen.</p>
	 * @return Name der Getter-Methode.
	 */
	public String getter() default "";
	
	/**
	 * Die Setter-Methode für die Eigenschaft.
	 * Wenn keine Methode angegeben wird, wird das Feld per Reflektion gesetzt.
	 * <p>Die angegebene Methode muss die Signatur
	 * 
	 * <pre>public void <i>name</i>(T)</pre>
	 * 
	 * aufweisen.</p>
	 * @return Name der Setter-Methode.
	 */
	public String setter() default "";
	
	/**
	 * Marktiert die Eigenschaft als nur lesend.
	 * @return <code>true</code>, wenn die Eigenschaft nur lesenden Zugriff erlauben soll,
	 * andernfalsl <code>false</code> (default).
	 */
	public boolean readonly() default false;
}
