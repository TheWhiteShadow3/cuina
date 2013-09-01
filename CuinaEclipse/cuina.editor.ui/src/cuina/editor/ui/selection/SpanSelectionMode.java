package cuina.editor.ui.selection;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Ermöglicht das Aufspannen eines Auswahlrechtecks. Optional kann die Auswahl
 * verschiebbar oder skalierbar sein.
 * 
 * @author TheWhiteShadow
 */
public class SpanSelectionMode implements SelectionMode
{
	private int gridSize;
	private boolean active;
	private Point anchor = new Point(0, 0);

	/**
	 * Erstellt ein neuen SpanSelectionMode.
	 * 
	 * @param rasterSize
	 *            Größe der Rastewrung beim Auswählen. Muss größer als 0 sein.
	 */
	public SpanSelectionMode(int rasterSize)
	{
		if (rasterSize <= 0) throw new IllegalArgumentException("rasterSize must be >= 1");

		this.gridSize = rasterSize;
	}

	public SpanSelectionMode()
	{
		this(1);
	}

	public int getGridSize()
	{
		return gridSize;
	}

	public void setGridSize(int rasterSize)
	{
		this.gridSize = rasterSize;
	}

	@Override
	public void activate(SelectionManager handler, int x, int y)
	{
		Selection s = handler.addSelection();
		s.setBounds(x, y, gridSize, gridSize, gridSize, 0, 0);
		anchor.x = s.getX();
		anchor.y = s.getY();
		active = true;
	}

	@Override
	public void deactivate(SelectionManager handler, int x, int y)
	{
		active = false;
	}

	@Override
	public boolean move(SelectionManager handler, int x, int y)
	{
		if (!active) return false;

		Selection sel = handler.getSelection();
		if (sel == null) return false;

		int xx = (x / gridSize * gridSize);
		int yy = (y / gridSize * gridSize);

		Rectangle rect = new Rectangle(anchor.x, anchor.y, gridSize, gridSize);
		rect.add(new Rectangle(xx, yy, gridSize, gridSize));
		sel.setBounds(rect);
		return sel.needRefresh();
	}
}
