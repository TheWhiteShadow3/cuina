package cuina.editor.core;


/**
 * Stellt den Kontext für einen Projektbezogenen Editor da.
 * @author TheWhiteShadow
 */
public interface IEditorContext
{
	/**
	 * Gibt das Cuina-Projekt zurück, für das der Editor agiert.
	 * @return Das Cuina-Projekt.
	 */
	public CuinaProject getCuinaProject();
	
	/**
	 * Signalisiert, dass sich die Daten des Editors geändert haben.
	 */
	public void fireDataChanged();
	
	/**
	 * Setzt eine Fehlermeldung.
	 * @param message Fehlermeldung oder <code>null</code> um die Fehlermeldung zu löschen.
	 */
	public void setErrorMessage(String message);
}
