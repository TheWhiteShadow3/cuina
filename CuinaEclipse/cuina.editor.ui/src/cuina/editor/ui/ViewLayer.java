package cuina.editor.ui;

import cuina.gl.GC;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Point;

/**
 * Ein ViewLayer stellt eine Ebene auf einer Zeichenfläche da.
 * @author TheWhiteShadow
 */
public interface ViewLayer
{
	/**
	 * Name des Layers
	 * @return Name
	 */
	public String getName();
	
	/**
	 * Gibt die Priorität der Ebene an.
	 * Dieser sollte <code>&gt;=0</code> sein und Spielraum für andere Ebenen lassen.
	 * Ebenen mit hoher Priorität überdecken Ebenen mit niedriger Priorität.
	 * Bei Ebenen gleicher Priorität ist die Reihenfolge nicht definiert.
	 * @return Die Priorität der Ebene.
	 */
	public int getPriority();
	
	/**
	 * Wird aufgerufen, wenn die Ebene neu gezeichnet werden muss.
	 * @param gc 
	 */
	public void paint(GC gc);
	
	public void dispose();
	
	public void fillContextMenu(IMenuManager menu, Point point);

//	public void fillActionBars(IActionBars actionBars);
}
