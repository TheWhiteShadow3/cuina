package cuina.editor.ui.selection;

import org.eclipse.swt.graphics.Point;

public class FollowSelectionMode implements SelectionMode
{
	private int			rasterSize;
    private int			dragX;
    private int			dragY;
	private boolean 	active;
	
	public FollowSelectionMode()
	{
		this(1, 0, 0);
	}

	public FollowSelectionMode(int rasterSize, int dragX, int dragY)
	{
		this.rasterSize = rasterSize;
		this.dragX = dragX;
		this.dragY = dragY;
	}

	public int getRasterSize()
	{
		return rasterSize;
	}

	public void setRasterSize(int rasterSize)
	{
		this.rasterSize = rasterSize;
	}

	@Override
	public void activate(SelectionManager manager, int x, int y)
	{
		this.active = true;
		move(manager, x, y);
	}

	@Override
	public void deactivate(SelectionManager manager, int x, int y)
	{
		this.active = false;
	}

	@Override
	public boolean move(SelectionManager manager, int x, int y)
	{
		manager.getSelection().setLocation(x - dragX, y - dragY, rasterSize, 0, 0);
		
		return active;
	}
	
	/**
	 * Setzt den Anfassungspunkt der Maus. Der Anfassungspunkt gibt an, wo die
	 * Auswahl von der oberen linken Ecke aus an der Maus "kleben" soll.
	 * 
	 * @param dragX
	 *            X-Position relativ zum Rechteck.
	 * @param dragY
	 *            Y-Position relativ zum Rechteck.
	 * @see #getDragOffset()
	 */
	public void setDragOffset(int dragX, int dragY)
	{
		this.dragX = dragX;
		this.dragY = dragY;
	}

	/**
	 * Gibt den Anfassungspunkt der Maus zurück. Der Anfassungspunkt gibt an, wo
	 * die Auswahl von der oberen linken Ecke aus an der Maus "kleben" soll.
	 * 
	 * @return Anfassungspunkt relativ zum Rechteck.
	 * @see #setDragOffset(int, int)
	 */
	public Point getDragOffset()
	{
		return new Point(dragX, dragY);
	}
}
