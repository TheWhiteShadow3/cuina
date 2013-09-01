package cuina.editor.map.internal;

import cuina.editor.ui.selection.Selection;
import cuina.editor.ui.selection.SelectionManager;
import cuina.editor.ui.selection.SelectionMode;

import org.eclipse.swt.graphics.Point;

public class TileSelectionMode implements SelectionMode
{
	private int tileSize;
	private Point size = new Point(1, 1);
	
	public int getTileSize()
	{
		return tileSize;
	}

	public void setTileSize(int tileSize)
	{
		this.tileSize = tileSize;
	}

	public Point getSize()
	{
		return new Point(size.x, size.y);
	}

	public void setSize(int width, int height)
	{
		size.x = width;
		size.y = height;
	}

	@Override
	public void activate(SelectionManager handler, int x, int y)
	{
		Selection sel = handler.getSelection();
		if (sel == null) return;
		
//		System.out.println("[TileSelectionMode] Tile on");
		setBounds(sel, x, y, size.x, size.y);
	}

	@Override
	public void deactivate(SelectionManager handler, int x, int y)
	{
		Selection sel = handler.getSelection();
		if (sel == null) return;
		
		sel.setSize(0, 0);
//		System.out.println("[TileSelectionMode] Tile off");
	}

	@Override
	public boolean move(SelectionManager handler, int x, int y)
	{
		Selection sel = handler.getSelection();
		if (sel == null) return false;
		
		setBounds(sel, x, y, size.x, size.y);
		return sel.needRefresh();
	}

	private void setBounds(Selection sel, int x, int y, int width, int height)
	{
		sel.setBounds(x, y, width, height, tileSize, 0, 0);
	}
}