package cuina.editor.eventx.internal.editors;

import org.eclipse.swt.widgets.Composite;

public interface TypeEditor<E>
{
	public void createComponents(Composite parent);
	/**
	 * Initialisiert den Editor. Diese Methode wird kurz nach der Instanzierung aufgerufen.
	 * Wird kein Objekt übergeben, ist der Editor veranlasst eine neue Instanz zu erstellen.
	 * @param obj Objekt, was der Editor bearbeiten soll, oder <code>null</code>.
	 */
	public void init(Object obj);
	/**
	 * Gibt das editierte Objekt zurück.
	 * Es ist nicht zwingend erforderlich, dass das Objekt das selbe ist, was bei {@link #init(Object)} übergeben wurde.
	 * @return Gibt das Objekt zurück.
	 */
	public E getValue();
	public boolean apply();
}