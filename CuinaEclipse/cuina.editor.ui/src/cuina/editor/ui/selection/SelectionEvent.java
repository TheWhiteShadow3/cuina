package cuina.editor.ui.selection;

import org.eclipse.swt.events.MouseEvent;

public class SelectionEvent
{
	/**
	 * Der SelectionManager, der das Event getriggert hat.
	 */
	public SelectionManager manager;
	
	/**
	 * Die aktuelle Auswahl zu dem das Event gehört.
	 */
	public Selection selection;
	
	/**
	 * Das Maus-Ereignis, welches das Auswahlevent ausgelöst hat.
	 */
	public MouseEvent mouseEvent;

	/**
	 * Gibt an, ob die Auswahl beibehalten oder verworfen werden soll.
	 */
	public boolean doIt;
	
	public SelectionEvent(SelectionManager manager, Selection selection, MouseEvent mouseEvent)
	{
		this.manager = manager;
		this.selection = selection;
		this.mouseEvent = mouseEvent;
		this.doIt = true;
	}
}
