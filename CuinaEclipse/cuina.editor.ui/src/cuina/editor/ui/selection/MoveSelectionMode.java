package cuina.editor.ui.selection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * Ermöglicht das Verschieben aller existierenden Auswahl-Bereiche.
 * @author TheWhiteShadow
 */
public class MoveSelectionMode implements SelectionMode
{
	public static final int NONE		= 0;
	public static final int SOUTH_WEST	= 1;
	public static final int SOUTH		= 2;
	public static final int SOUTH_EAST	= 3;
	public static final int WEST		= 4;
	public static final int CENTER		= 5;
	public static final int EAST		= 6;
	public static final int NORTH_WEST	= 7;
	public static final int NORTH 		= 8;
	public static final int NORTH_EAST	= 9;

	private Control control;
	int lastX;
	int lastY;
	private int dx;
	private int dy;
	
	private Rectangle anchor;
	private int mode = NONE;
	private int borderRange;
	private int gridSize;

	public MoveSelectionMode(Control control)
	{
		this(control, -1, 1);
	}
	
	public MoveSelectionMode(Control control, int borderRange, int gridSize)
	{
		this.control = control;
		this.borderRange = borderRange;
		this.gridSize = gridSize;
	}
	
	public int getBorderRange()
	{
		return borderRange;
	}

	public void setBorderRange(int borderRange)
	{
		this.borderRange = borderRange;
	}

	public int getDX()
	{
		return dx;
	}

	public int getDY()
	{
		return dy;
	}
	
	public void resetDelta()
	{
		dx = 0;
		dy = 0;
	}
	
//    private void adjustAnchor(Selection sel)
//    {
//        if (mode == CENTER) return;
// 
//        if (anchor == null) anchor = new Rectangle(0, 0, 0, 0);
//        anchor.x = sel.getX();
//        anchor.y = sel.getY();
//        
//        if (mode == WEST || mode == NORTH_WEST || mode == SOUTH_WEST)
//            anchor.x += sel.getWidth() - rasterSize;
//        if (mode == NORTH || mode == NORTH_WEST || mode == NORTH_EAST)
//            anchor.y += sel.getHeight() - rasterSize;
//        
//        if (mode == NORTH || mode == SOUTH)
//            anchor.width = sel.getWidth();
//        if (mode == WEST || mode == EAST)
//            anchor.height = sel.getHeight();
//    }

	@Override
	public void activate(SelectionManager manager, int x, int y)
	{
		Selection sel = manager.getSelection();
		if (sel == null)
		{
			mode = NONE;
			return;
		}
		resetDelta();

		if (sel.contains(x, y))
		{
			if (borderRange >= 0)
			{
				if (sel.getX() > x - borderRange)
				{
					if (sel.getY() > y - borderRange)							mode = NORTH_WEST;
					else if (sel.getY() + sel.getHeight() < y + borderRange)	mode = SOUTH_WEST;
					else														mode = WEST;
				}
				else if (sel.getX() + sel.getWidth() < x + borderRange)
				{
					if (sel.getY() > y - borderRange)							mode = NORTH_EAST;
					else if (sel.getY() + sel.getHeight() < y + borderRange)	mode = SOUTH_EAST;
					else														mode = EAST;
				}
				else
				{
					if (sel.getY() > y - borderRange)							mode = NORTH;
					else if (sel.getY() + sel.getHeight() < y + borderRange)	mode = SOUTH;
					else														mode = CENTER;
                }
            }
			lastX = x;
			lastY = y;
			if (mode == CENTER)
			{
				setCursor(SWT.CURSOR_SIZEALL);
			}
			else
			{
				anchor = manager.getSelection().getBounds();
			}
		}
	}

	@Override
	public void deactivate(SelectionManager manager, int x, int y)
	{
		mode = NONE;
		setCursor(SWT.CURSOR_ARROW);
	}

	@Override
	public boolean move(SelectionManager manager, int x, int y)
	{
		if (mode == NONE) return false;
		
		if (mode == CENTER)
		{
			return moveCenter(manager, x, y);
		}
		else
		{
			return resize(manager, x, y);
		}
	}
	
	private boolean resize(SelectionManager manager, int x, int y)
	{
		Selection sel = manager.getSelection();
		if (sel == null) return false;

		int xx = (x / gridSize * gridSize);
		int yy = (y / gridSize * gridSize);

		Rectangle rect = new Rectangle(anchor.x, anchor.y, anchor.width, anchor.height);
		rect.add(new Rectangle(xx, yy, gridSize, gridSize));
		sel.setBounds(rect);
		return sel.needRefresh();
	}

	private boolean moveCenter(SelectionManager manager, int x, int y)
	{
		dx = x - lastX;
		dy = y - lastY;
		lastX = x;
		lastY = y;
		
		for(Selection s : manager.getSelectionList())
		{
			s.setLocation(s.getX() + dx, s.getY() + dy, gridSize, 0, 0);
		}
		return Math.abs(dx) + Math.abs(dy) > 0;
	}
	
	private void setCursor(int mode)
	{
		control.setCursor(Display.getDefault().getSystemCursor(mode));
	}
}
