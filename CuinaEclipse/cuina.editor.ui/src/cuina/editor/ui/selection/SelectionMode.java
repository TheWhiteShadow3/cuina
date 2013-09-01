package cuina.editor.ui.selection;

/**
 * Ein Auswahlmodus bestimmt das Verhalten der Auswahl.
 * 
 * @author TheWhiteShadow
 */
public interface SelectionMode
{
	/** Ein Null-Auswahlmodus, der nur alle Auswahlrechtecke löscht. */
	public static final SelectionMode NULL_INSTANCE = new NullMode();

	/**
	 * Wird aufgerufen, wenn der Auswahlmodus gestartet wird. Das ist der Fall,
	 * wenn mit der Maus auf eine Position innerhalb der Domäne des
	 * SelectionManagers geklickt wird und kein Listener die Aktion abgebrochen
	 * hat, oder der Modus manuell gestartet wird.
	 * 
	 * @param manager
	 *            SelectionManager, der Das Ereigniss ausgelöst hat.
	 * @param x
	 *            X-Position der Mausposition wenn bekannt, ansonsten <code>0</code>.
	 * @param y
	 *            Y-Position der Mausposition wenn bekannt, ansonsten <code>0</code>.
	 */
	public void activate(SelectionManager manager, int x, int y);

	/**
	 * Wird aufgerufen, wenn der Aswahlmodus beendet wird. Das ist der Fall,
	 * wenn die Maustaste losgelassen wird und ein Listener die Aktion
	 * abgebrochen hat, oder der Modus manuell beendet wird. Diese Methode kann
	 * auf aufgerufen werden ohne, dass jemals
	 * {@link #activate(SelectionManager, int, int)} aufgerufnen wurde.
	 * 
	 * @param manager
	 *            SelectionManager, der Das Ereigniss ausgelöst hat.
	 * @param x
	 *            X-Position der Mausposition wenn bekannt, ansonsten <code>0</code>.
	 * @param y
	 *            Y-Position der Mausposition wenn bekannt, ansonsten <code>0</code>.
	 */
	public void deactivate(SelectionManager manager, int x, int y);

	/**
	 * Wird aufgerufen, wenn sich die Auswahl verändern könnte.
	 * Das ist der Fall, wenn die Maus bei gestartetem Auswahlmodus bewegt wird.
	 * @param manager
	 *            SelectionManager, der Das Ereigniss ausgelöst hat.
	 * @param x
	 *            X-Position der Mausposition wenn bekannt, ansonsten <code>0</code>.
	 * @param y
	 *            Y-Position der Mausposition wenn bekannt, ansonsten <code>0</code>.
	 * @return <code>true</code>, wenn sich die Auswahl geändert hat, andernfalls <code>false</code>se.
	 */
	public boolean move(SelectionManager manager, int x, int y);
}

class NullMode implements SelectionMode
{
	@Override
	public void activate(SelectionManager manager, int x, int y)
	{
		manager.clearSelections();
	}

	@Override
	public void deactivate(SelectionManager manager, int x, int y)
	{}

	@Override
	public boolean move(SelectionManager manager, int x, int y)
	{
		return false;
	}
}
