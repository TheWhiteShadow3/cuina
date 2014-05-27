package cuina.editor.eventx.internal.editors;

import org.eclipse.swt.widgets.Composite;

import cuina.editor.eventx.internal.CommandEditorContext;

public interface TypeEditor<E>
{
	public void createComponents(Composite parent);
	/**
	 * Initialisiert den Editor. Diese Methode wird kurz nach der Instanzierung aufgerufen.
	 * Wird kein Objekt übergeben, ist der Editor veranlasst eine neue Instanz zu erstellen.
	 * @param context Kontext des Editors.
	 * @param type Datentyp des Objekts.
	 * @param obj Objekt, was der Editor bearbeiten soll, oder <code>null</code>.
	 */
	public void init(CommandEditorContext context, String type, Object obj);
	/**
	 * Gibt das editierte Objekt zurück.
	 * Es ist nicht zwingend erforderlich, dass das Objekt das selbe ist, was bei der initialisierung übergeben wurde.
	 * @return Gibt das Objekt zurück.
	 */
	public E getValue();
	
	/**
	 * Wird aufgerufen, wenn die Daten des Editors gespiechert werden sollen.
	 * Die Methode muss <code>true</code> zurück geben, wenn sich die Daten in einen Konsistenten Zustand befinden.
	 * @return <code>true</code>, wenn sich die Daten in einen Konsistenten Zustand befinden, andernfalls <code>false</code>.
	 */
	public boolean apply();
}
