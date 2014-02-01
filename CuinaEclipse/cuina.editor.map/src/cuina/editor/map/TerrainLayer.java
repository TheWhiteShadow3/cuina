package cuina.editor.map;

import cuina.editor.map.internal.layers.BackgroundLayer;
import cuina.editor.ui.ViewLayer;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Eine Ebene auf der Karte zeigt spezifische Grafiken an und erlaubt das Editeren einzelner Objekte-Typen.
 * <p>
 * Karten besitzen verschiedene Objekt-Typen.
 * Jede Ebene definiert einen oder mehrere Objekt-Typen und führt
 * einen eigenen Befehlssatz zum erstellen und bearbeiten mit.
 * Die Reihenfolge der Ebenen wird durch eine Priorität angegeben.
 * </p>
 * <p>
 * <i>Beispiel:</i><br>
 * Die Klasse {@link BackgroundLayer} zeigt den Hintergrund der Karte an.
 * </p>
 * @author TheWhiteShadow
 */
public interface TerrainLayer extends ViewLayer
{
	/**
	 * Installiert das MapLayer zum angegebenen {@link ITerrainEditor}.
	 * @param editor Referenz zum Editor, dem diese Ebene angehört.
	 */
	public void install(ITerrainEditor editor);
	
	/**
	 * Wird aufgerufen, wenn mit dem Cursor-Modus eine Region ausgewählt wird.
	 * <p>
	 * Implementierungen können darauf reagieren um z.B. ein oder mehrere Objekte auszuwählen.
	 * In jedem Fall müssen sie zurückgeben, ob sie auf das Event reagiert haben um zu verhindern,
	 * dass tiefere Ebenen ebenfalls dieses Event empfangen.
	 * </p>
	 * @param rect Ausgewählte Region. Alle Ebenene erhalten eine Referenz auf das selbe Rechteck.
	 * @return <code>true</code>, wenn das Event verarbeitet wurde, andernfalls <code>false</code>.
	 */
	public boolean selectionPerformed(Rectangle rect);
	
	/**
	 * Wird aufgerufen, wenn mit dem Cursor-Modus ein Punkt ausgewählt wird.
	 * <p>
	 * Implementierungen können darauf reagieren um z.B. ein oder mehrere Objekte auszuwählen.
	 * In jedem Fall müssen sie zurückgeben, ob sie auf das Event reagiert haben um zu verhindern,
	 * dass tiefere Ebenen ebenfalls dieses Event empfangen.
	 * </p>
	 * @param point Ausgewählter Punkt. Alle Ebenene erhalten eine Referenz auf den selben Punkt.
	 * @return <code>true</code>, wenn das Event verarbeitet wurde, andernfalls <code>false</code>.
	 */
	public boolean selectionPerformed(Point point);
	
	/**
	 * Wird aufgerufen, wenn mit der linken Maustaste auf den Kartenbereich gedrück wird und keine
	 * Ebene den Fokus hat.
	 * Wenn mindestens eine Ebene eine Aktion hinzufügt, wird ein Kontextmenü angezeigt.
	 * @param menu Menü-Manager vom Kontextmenü.
	 * @param p Punkt, an dem das Kontektmenü aufgerufen wird.
	 */
	public void fillDefaultContextMenu(IMenuManager menu, Point p);
	
	
	public void keyActionPerformed(KeyEvent ev);
	
	public void refresh();
	
	public void activated();
	
	public void deactivated();
	
//	public boolean startSelection(SelectionEvent event);
//
//	public boolean updateSelection(SelectionEvent event);
//
//	public boolean endSelection(SelectionEvent event)
;
}
