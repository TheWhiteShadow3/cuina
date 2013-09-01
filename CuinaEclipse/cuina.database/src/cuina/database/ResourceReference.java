package cuina.database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Gibt eine Referenz zu einer Projekt-Resource an.
 * Diese Information wird benötigt, um die referentielle Integrität zu erhalten.
 * @author TheWhiteShadow
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceReference
{
	public String type();
}
