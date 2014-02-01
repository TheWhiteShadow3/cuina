package cuina.editor.map.internal.layers;

import cuina.editor.map.ITerrainEditor;
import cuina.editor.map.TerrainLayer;
import cuina.gl.GC;
import cuina.gl.Image;
import cuina.map.Tileset;
import cuina.resource.ResourceException;

import java.util.Objects;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Zeigt den Hintergrund des Tilesets auf einer Karte an.
 * Wenn kein Hintergrund vorhanden ist, zeigt die Ebene ein Kachelmuster an.
 * <br>Die Ebene hat die Priorität <code>0</code>.
 * <p>
 * <b>Achtung!</b>
 * Andere Ebenen sollten eine höhere Priorität haben, da der BackgroundLayer flächendeckend ist.
 * </p>
 * @author TheWhiteShadow
 */
public class BackgroundLayer implements TerrainLayer
{
	private ITerrainEditor editor;
	private Rectangle rect;
	private int tileSize;
	private String imageName;
	private Image backgroundImage;

	@Override
	public String getName()
	{
		return "BackgroundLayer";
	}

	@Override
	public int getPriority()
	{
		return 0;
	}

	@Override
	public void install(ITerrainEditor editor)
	{
		this.editor = editor;
		refresh();
	}

	@Override
	public void refresh()
	{
		this.rect = editor.getViewBounds();
		
		Tileset tileset = editor.getTileset();
		this.tileSize = (tileset == null) ? 32 : tileset.getTileSize();
		
		try
		{
			if (Objects.equals(this.imageName, tileset.getBackgroundName())) return;
			
			this.imageName = tileset.getBackgroundName();
			if (imageName != null && imageName.length() > 0)
				backgroundImage = editor.loadImage(imageName);
		}
		catch (ResourceException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void paint(GC gc)
	{
		if (backgroundImage != null)
			paintBackground(gc);
		else
			paintRaster(gc);
	}
	
	private void paintBackground(GC gc)
	{
		if (backgroundImage.getWidth() < 1 || backgroundImage.getHeight() < 1) return;
		
		int maxX = rect.width;
		int maxY = rect.height;
		for (int x = 0; x < rect.width; x += backgroundImage.getWidth())
		{
			for (int y = 0; y < rect.height; y += backgroundImage.getHeight())
			{	// Prüfe, ob das Bild im Sichtbereich liegt.
				paintImage(gc, backgroundImage, x, y, maxX, maxY);
			}
		}
	}
	
	private void paintImage(GC gc, Image image, int x, int y, int maxX, int maxY)
	{
		int drawWidth = Math.min(backgroundImage.getWidth(), maxX - x);
		int drawHeight = Math.min(backgroundImage.getHeight(), maxY - y);
		gc.drawImage(backgroundImage, 0, 			0,
										drawWidth, 	drawHeight,
										x, 			y,
										drawWidth, drawHeight);
	}

	private void paintRaster(GC gc)
	{
		Rectangle clip = gc.getClip();
		// Zeichne Raster
		int bgTileSize = tileSize / 2;
		int x1 = Math.max(clip.x, 0);
		int y1 = Math.max(clip.y, 0);
		int x2 = Math.min(clip.x + clip.width + bgTileSize, rect.width);
		int y2 = Math.min(clip.y + clip.height + bgTileSize, rect.height);
		
		TilemapUtil.paintGrid(gc, x1, y1, x2, y2, bgTileSize);
	}

	@Override
	public void dispose()
	{
		if (backgroundImage != null) backgroundImage.dispose();
	}

	@Override
	public void fillContextMenu(IMenuManager menu, Point p) {}

	@Override
	public boolean selectionPerformed(Rectangle rect)
	{
		return false;
	}

	@Override
	public boolean selectionPerformed(Point point)
	{
		return false;
	}

	@Override
	public void fillDefaultContextMenu(IMenuManager menu, Point p) {}

	@Override
	public void keyActionPerformed(KeyEvent ev) {}

	@Override
	public void activated() {}

	@Override
	public void deactivated() {}
}
