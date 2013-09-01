package cuina.editor.ui.selection;

public interface SelectionListener
{
	/**
	 * Wird aufgerufen, wenn eine Auswahl gestartet wird.
	 * @param event Auswahl-Event.
	 */
	public void startSelection(SelectionEvent event);
	
	/**
	 * Wird aufgerufen, wenn sich der Bereich der aktuellen Auswahl ändert.
	 * @param event Auswahl-Event.
	 */
	public void updateSelection(SelectionEvent event);
	
	/**
	 * Wird aufgerufen, wenn die Auswahl fertig erstellt wurde.
	 * @param event Auswahl-Event.
	 */
	public void endSelection(SelectionEvent event);
}
