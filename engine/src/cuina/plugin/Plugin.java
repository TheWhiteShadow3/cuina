package cuina.plugin;

import java.io.Serializable;

/**
 * Das Plugin-Interface dient als Kennzeichnung für ein dynamisch einzubindendes Spielobjekt aus einem Plugin.
 * Pluginklassen können über eine Kontext-Annotation einem Spielkontext zugewieden werden.
 * <br>Erlaubte Kontext-Annotationen sind:
 * <ul>
 * <li>{@link ForGlobal}</li>
 * <li>{@link ForSession}</li>
 * <li>{@link ForScene}</li>
 * </ul>
 * @author TheWhiteShadow
 */
public interface Plugin extends Serializable
{
//	/**
//	 * Objekt-Inizialisierung.
//	 * Im Gegensatz zum Konstruktor kann an dieser Stelle davon ausgegangen werden,
//	 * dass alle anderen gültigen Objekte bereits erstellt sind.
//	 * <p>
//	 * Wird aufgerufen, wenn die Spielszene initialisiert wurde. Dazu muss das Plugin einen gültigen Kontext besitzen.
//	 * Dies ist der Fall, wenn die implementierende Klasse eine Kontext-Annotation besitzt
//	 * oder manuell einem Kontext hinzugefügt wird.
//	 * </p>
//	 */
//	public void init();
}
