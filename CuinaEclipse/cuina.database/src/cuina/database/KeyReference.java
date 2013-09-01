package cuina.database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Gibt eine Referenz zu einem Schlüssel in einer anderen Datenbank-Tabelle an.
 * Diese Information wird benötigt, um die referentielle Integrität zu erhalten.
 * @author TheWhiteShadow
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface KeyReference
{
	/** Tabellenname zu der der Schlüssel gehört. */
	public String name();
}
