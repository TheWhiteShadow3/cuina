package cuina.database.ui;

import cuina.database.NamedItem;
import cuina.database.ui.tree.TreeRoot;

/**
 * <i>Benutze das Extension-Point Attribut TreeContentProvider Der Datenbank-Typ Definition
 * um die Baumstruktur von Daten-Elementen anzugeben.</i>
 */
@Deprecated
public interface TreeItem extends NamedItem
{
	public boolean hasChildren();
	
	/**
	 * Gibt eine Array mit den Kind-Elementen zurück.
	 * Die Elemnte sollten mindestens NamedItem implementieren.
	 * Wenn nicht, wird überall dort wo ein Name verlangt wird, <code>toString()</code> aufgerufen.
	 * @param root Das Wurzelelement.
	 * @return Array mit den Kind-Elementen.
	 */
	public Object[] getChildren(TreeRoot root);
}
