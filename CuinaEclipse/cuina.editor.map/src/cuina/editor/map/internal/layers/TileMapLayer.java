package cuina.editor.map.internal.layers;

import cuina.editor.map.ITerrainEditor;
import cuina.editor.map.MapEvent;
import cuina.editor.map.TerrainLayer;
import cuina.editor.map.TileSelection;
import cuina.editor.map.internal.TileFactory;
import cuina.editor.map.internal.TileFactory.AutotileSet;
import cuina.editor.map.internal.TileSelectionMode;
import cuina.editor.map.util.MapOperation;
import cuina.editor.map.util.MapSavePoint;
import cuina.editor.ui.selection.Selection;
import cuina.editor.ui.selection.SelectionEvent;
import cuina.editor.ui.selection.SelectionListener;
import cuina.editor.ui.selection.SelectionManager;
import cuina.editor.ui.selection.SpanSelectionMode;
import cuina.gl.GC;
import cuina.gl.Image;
import cuina.map.Map;
import cuina.map.Tileset;
import cuina.resource.ResourceException;
import cuina.resource.ResourceManager;
import cuina.resource.ResourceManager.Resource;
import cuina.resource.ResourceProvider;

import java.util.Arrays;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.lwjgl.LWJGLException;
import org.lwjgl.util.Color;

/**
 * Zeigt das Terrain des Tilesets auf einer Karte an und ermöglicht das
 * Bearbeiten an diesem. <br>
 * Die Ebene hat die Priorität <code>10</code>.
 * <p>
 * Das Terrain wird durch eine Reihen von Tiles dargestellt die auf mehreren
 * Ebenen ansotiert sin.
 * </p>
 * 
 * @author TheWhiteShadow
 */
public class TileMapLayer implements TerrainLayer, ISelectionListener, SelectionListener
{
	public static final String LAYER_NAME = "cuina.map.TileMapLayer";
	
	// Der Zeichenmodus definiert das ausgewählte Werkzeug.
	public static final int DRAWMODE_NONE = 0;
	public static final int DRAWMODE_PENCIL = 1;
	public static final int DRAWMODE_RECTANGLE = 2;
	public static final int DRAWMODE_ELLISPE = 3;
	public static final int DRAWMODE_FILLER = 4;

	// Aktionen, die vom TileMapLayer genutzt werden.
	public static final String ACTION_PENCIL = "cuina.editor.map.tilemap.pencilAction";
	public static final String ACTION_FILLER = "cuina.editor.map.tilemap.fillerAction";
	public static final String ACTION_ELLIPSE = "cuina.editor.map.tilemap.ellipseAction";
	public static final String ACTION_RECTANGLE = "cuina.editor.map.tilemap.rectangleAction";

	private Map map;
	private Tileset tileset;
	private Image tilesetImage;
	private ITerrainEditor editor;
	private TileSelectionMode tileSelectionMode = new TileSelectionMode();

	private int currentTool = DRAWMODE_NONE;
	private Rectangle tileSelection;
	private int currentLayer;
//	private TileSelection[] copyBuffer;	// Für Kopier-Operationen auf der Karte.
	// TODO: Könnte mit dem sourceLayer verbunden werden.

	private TileSelection sourceLayer;	// Für die QuellDaten.
	private TileSelection tempLayer;	// Für eine Vorschau bei Zeichnen.
	private boolean dimLayers = true;
	private boolean fadeLayers = true;
	private boolean showRaster;
	private final AutotileSet[] autotiles = new AutotileSet[Tileset.AUTOTILES_COUNT];
	private final Image[] autotileImages = new Image[Tileset.AUTOTILES_COUNT];
	private MapSavePoint savePoint;
	private boolean changed;
	private final Point drawOrigin = new Point(0, 0);

	@Override
	public String getName()
	{
		return LAYER_NAME;
	}

	@Override
	public int getPriority()
	{
		return 10;
	}

	@Override
	public void paint(GC gc)
	{
		if(map == null || tilesetImage == null) return;

		paintTiles(gc, true);
		if(showRaster)
			paintRaster(gc);
	}

	/**
	 * Gibt die aktuelle Tile-Ebene zurück.
	 * 
	 * @return aktuelle Tile-Ebene.
	 */
	public int getCurrentLayer()
	{
		return currentLayer;
	}

	/**
	 * Setzt die aktuelle Tile-Ebene.
	 * 
	 * @param currentLayer
	 *            neue aktuelle Tile-Ebene.
	 */
	public void setCurrentLayer(int currentLayer)
	{
		this.currentLayer = currentLayer;
	}
	
	void toogleDimFade()
	{
		dimLayers = !dimLayers;
		fadeLayers = !fadeLayers;
		editor.getGLCanvas().redraw();
	}
	
	void setDimFade(boolean value)
	{
		dimLayers = value;
		fadeLayers = value;
		editor.getGLCanvas().redraw();
	}

	private void paintTiles(GC gc, boolean useTempLayer)
	{
		// int ts = tileset.getTileSize();
		int minX = 0;// Math.max(e.x, 0) / ts;
		int minY = 0;// Math.max(e.y, 0) / ts;
		int maxX = map.width;// Math.min((e.x + e.width) / ts + 1, map.width);
		int maxY = map.height;// Math.min((e.y + e.height) / ts + 1,
								// map.height);
		// System.out.println("Paint Tiles in: " + minX + ", " + minY + ", " +
		// maxX + ", " + maxY);
		Rectangle bounds = editor.getViewBounds();
		for(int z = 0; z < Map.LAYERS; z++)
		{
			if (currentLayer == z && dimLayers)
			{
				gc.setColor(Color.BLACK);
				gc.setAlpha(127);
				gc.fillRectangle(0, 0, bounds.width, bounds.height);
				gc.setColor(Color.WHITE);
			}
			else if(z > currentLayer && fadeLayers)
			{
				gc.setAlpha(127);
			}
			for(int x = minX; x < maxX; x++)
			for(int y = minY; y < maxY; y++)
			{
				if(z == this.currentLayer && tempLayer != null && tempLayer.contains(x, y))
				{
					paintTempLayer(gc, x, y);
				}
				else
				{
					paintTile(gc, map.data[x][y][z], x, y);
				}
			}
		}
		gc.setAlpha(255);
	}

	private void paintTempLayer(GC gc, int x, int y)
	{
		short id = tempLayer.get(x - tempLayer.getX(), y - tempLayer.getY());
		if(id > 0)
		{
			// System.out.println( "Temp-Layer-Tile: " + (x
			// - rect.x / ts) +
			// ", " + (y - rect.y / ts) );
			if (id >= Tileset.AUTOTILES_OFFSET)
				paintTile(gc, id, x, y);
			else
				paintTile(gc, id, x, y);
		}
	}

	private void paintTile(GC gc, int tileId, int x, int y)
	{
		if (tileId == 0 || tilesetImage.getWidth() == 0) return;

		int ts = tileset.getTileSize();
		if (tileId >= Tileset.AUTOTILES_OFFSET)
		{
			tileId -= Tileset.AUTOTILES_OFFSET;
			if (tileId / 48 < autotiles.length)
			{
				Image img = autotileImages[tileId / 48];
				if(img == null) return;

				int srcX = (tileId % 8);
				int srcY = (tileId / 8);
				gc.drawImage(img, srcX * ts, srcY * ts, ts, ts, x * ts, y * ts, ts, ts);
			}
		}
		else
		{
			tileId--;
			int srcX = (tileId % (tilesetImage.getWidth() / ts)) * ts;
			int srcY = (tileId / (tilesetImage.getWidth() / ts)) * ts;
			gc.drawImage(tilesetImage, srcX, srcY, ts, ts, x * ts, y * ts, ts, ts);
		}
	}

	private void paintRaster(GC gc)
	{
		Rectangle drawRect = gc.getClip();
		gc.setColor(Color.GREY);
		gc.setAlpha(127);
		int ts = tileset.getTileSize();
		int maxX = Math.min((drawRect.x + drawRect.width / ts), map.width - 1);
		int maxY = Math.min((drawRect.y + drawRect.height / ts), map.height - 1);
		for(int x = drawRect.x / ts; x <= maxX; x++)
		{
			for(int y = drawRect.y / ts; y <= maxY; y++)
			{
				gc.drawRectangle(x * ts, y * ts, ts - 1, ts - 1);
			}
		}
		gc.setAlpha(255);
	}

	@Override
	public void dispose()
	{
		tilesetImage.dispose();
		for(Image img : autotileImages)
		{
			if(img != null) img.dispose();
		}
	}

	@Override
	public void install(final ITerrainEditor editor)
	{
		this.editor = editor;

		this.map = editor.getMap();
		this.tileset = editor.getTileset();
		tileSelectionMode.setTileSize(tileset.getTileSize());
		loadAutotiles();

		try
		{
			tilesetImage = editor.loadImage(editor.getGLCanvas(), tileset.getTilesetName());
		}
		catch(ResourceException e)
		{
			e.printStackTrace();
		}

		editor.getSelectionManager().addSelectionListener(this);
		// addMouseHandling();

		// XXX: Eclipse Indigo Service Release 2 creates an active page too
		// late,
		// getActivePage() returns null - consequently it throws a null pointer
		// exception
		// workaround: run this in Display thread
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if(page != null)
		{
			page.addSelectionListener(this);
		}
		else
		{
			System.out
					.println("[TileMapLayer] Info: Run \"getActivePage() returns null\" workaround to register SelectionListener");
			Display.getDefault().asyncExec(new SelectionRunnable(this));
		}

		sourceLayer = new TileSelection(1);
	}

	private Action createTileSelectionAction(final Point p)
	{
		return new Action("Tile aufnehmen")
		{
			@Override
			public void run()
			{
				int x = p.x / tileset.getTileSize();
				int y = p.y / tileset.getTileSize();
				short id = 0;
				for(int i = Map.LAYERS - 1; i >= 0; i--)
				{
					id = map.data[x][y][i];
					if(id > 0) break;
				}
				setTileSourceData(new TileSelection(id + 1));
			}
		};
	}

	// class for "getActivePage() returns null" workaround
	private class SelectionRunnable implements Runnable
	{
		ISelectionListener listener = null;

		public SelectionRunnable(ISelectionListener listener)
		{
			this.listener = listener;
		}

		@Override
		public void run()
		{
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			page.addSelectionListener(listener);
		}
	}

	private void loadAutotiles()
	{
		String[] autoTileNames = tileset.getAutotiles();
		if(autoTileNames == null) return;

		for(int i = 0; i < autoTileNames.length; i++)
		{
			if(autoTileNames[i] == null) continue;

			try
			{
				Resource res = editor.getProject().getService(ResourceProvider.class)
						.getResource(ResourceManager.KEY_GRAPHICS, autoTileNames[i]);
				autotiles[i] = TileFactory.createAutotileSet(res, tileset.getTileSize());
				autotileImages[i] = new Image(editor.getGLCanvas(), autotiles[i].getFrame(0));
			} catch(ResourceException | LWJGLException e)
			{
				e.printStackTrace();
			}
		}
	}

	int getTool()
	{
		return currentTool;
	}

	void setTool(int tool)
	{
		if(currentTool == tool) return;
		currentTool = tool;
		
		System.out.println("[TileMapLayer] Zeichenmodus: " + tool);
		initTool();
	}
	
	private void initTool()
	{
		SelectionManager sh = editor.getSelectionManager();
		sh.clearSelections();
		switch(currentTool)
		{
//			case DRAWMODE_NONE:
//			sh.clearSeletionMode();
//				break;
			case DRAWMODE_PENCIL:
			{
				if(sourceLayer != null)
					tileSelectionMode.setSize(sourceLayer.getWidth() * tileset.getTileSize(), sourceLayer.getHeight()
							* tileset.getTileSize());
				else
					tileSelectionMode.setSize(tileset.getTileSize(), tileset.getTileSize());
				sh.setSelectionMode(tileSelectionMode, true);
				break;
			}
			case DRAWMODE_RECTANGLE:
			case DRAWMODE_ELLISPE:
			case DRAWMODE_FILLER:
			{
				tileSelectionMode.setSize(tileset.getTileSize(), tileset.getTileSize());
				sh.setSelectionMode(tileSelectionMode, true);
				break;
			}
		}
	}

	private void updateSelection()
	{
		tileSelection = editor.getSelectionManager().getSelection().getBounds();
		int ts = tileset.getTileSize();
		tileSelection.x /= ts;
		tileSelection.y /= ts;
		tileSelection.width /= ts;
		tileSelection.height /= ts;
	}

	private void addSavePoint()
	{
		if(!changed) return;

		MapSavePoint newSavePoint = createTerrainBackup();
		editor.addOperation(new MapOperation("Change Tiles", savePoint, newSavePoint));
		savePoint = newSavePoint;
		changed = false;
	}

	/**
	 * Erstellt eine Backup-Aktion mit allen Terrain-Daten der Map.
	 * 
	 * @return Wiederherstellungs-Aktion
	 */
	private MapSavePoint createTerrainBackup()
	{
		MapSavePoint action = new MapSavePoint()
		{
			short[][][] aData = copyData(map.data);

			@Override
			public void apply()
			{
				map.data = copyData(aData);
				savePoint = this;
			}
		};
		return action;
	}

	/**
	 * Gibt eine Kopie der aktuellen Mapdaten zurück.
	 * Diese Methode ist wichtig für die Erstellung eines Backups.
	 * 
	 * @return Kopie der Map-Daten.
	 */
	private short[][][] copyData(final short[][][] data)
	{
		int width = data.length;
		int height = data[0].length;
		short[][][] copy = new short[width][height][];

		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
			{
				copy[x][y] = Arrays.copyOf(data[x][y], data[0][0].length);
			}
		return copy;
	}

	/**
	 * Setzt die Tiles des angegebenen TileLayer auf der Map.
	 * Die ID's im TileLayer müssen beim Index 1 anfangen. Nullen werden als
	 * nicht gesetzt gewertet und ignoriert.
	 * <p>
	 * Bei Autotiles wird automatisch die passende ID aus dem Set gewählt und
	 * alle umliegenden Tiles angepasst.
	 * </p>
	 * 
	 * @param x0
	 *            TileLayer-Offset auf der Karte.
	 * @param y0
	 *            TileLayer-Offset auf der Karte.
	 * @param tileLayer
	 *            TileLayer, der als Datenquelle benutzt werden soll.
	 * @param origin
	 *            Ursprungs-Koordinate im TileLayer.
	 *            Bei null, wird die zuletzt gesetzte Koordinate benutzt.
	 */
	public void setTiles(int x0, int y0, TileSelection tileLayer, Point origin)
	{
		if(tileLayer == null) return;

		if(savePoint == null)
			savePoint = createTerrainBackup();
		if(origin != null)
		{
			drawOrigin.x = origin.x;
			drawOrigin.y = origin.y;
		}

		for(int x = 0; x < tileLayer.getWidth(); x++)
		for(int y = 0; y < tileLayer.getHeight(); y++)
		{
			short id = (short) (tileLayer.getTiled(x, y, x0 - drawOrigin.x, y0 - drawOrigin.y) - 1);
			if(id == -1) continue;

			map.data[x0 + x][y0 + y][currentLayer] = id;
		}

		TilemapUtil.updateAutotiles(map, tileLayer, x0, y0, currentLayer);

		changed = true;
		editor.fireMapChanged(this, MapEvent.PROP_TILES);
	}

	@Override
	public void fillContextMenu(IMenuManager menu, Point point)
	{
		fillDefaultContextMenu(menu, point);
	}

	@Override
	public void fillDefaultContextMenu(IMenuManager menu, Point point)
	{
		menu.add(createTileSelectionAction(point));
	}

	/**
	 * Setzt die Daten für das Quell-TileLayer.
	 * 
	 * @param tiles
	 *            Die Tiles.
	 */
	public void setTileSourceData(TileSelection tiles)
	{
		if (sourceLayer == tiles) return;
		
		sourceLayer = tiles;
		if (currentTool == DRAWMODE_NONE || editor.getActiveLayer() != this)
		{
			System.out.println("[TileMapLayer] Zeichenmodus (durch Quell-Daten): DRAWMODE_PENCIL");
			editor.setActiveTool(ACTION_PENCIL);
		}
		initTool();
	}

	private void setTempLayer(int x0, int y0, int width, int height)
	{
		if(sourceLayer == null) return;

		short[][] data = new short[width][height];
		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				data[x][y] = sourceLayer.getTiled(x, y, 0, 0);
			}
		}
		tempLayer = new TileSelection(x0, y0, data);
//		System.out.println("Temp-Rechteck: " +
//					tempLayer.getX() +", " +
//					tempLayer.getY() + ", " +
//					tempLayer.getWidth() + ", " +
//					tempLayer.getHeight());
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
		if(selection instanceof TileSelection)
		{
			
			setTileSourceData((TileSelection) selection);
		}
	}

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
	public void startSelection(SelectionEvent event)
	{
		if (editor.getActiveLayer() != TileMapLayer.this || currentTool == DRAWMODE_NONE) return;
		
		if (event.mouseEvent.button == 1)
		{
			updateSelection();
			switch (currentTool)
			{
				case DRAWMODE_PENCIL:
					Point p = new Point(tileSelection.x, tileSelection.y);
					setTiles(tileSelection.x, tileSelection.y, sourceLayer, p);
					break;

				case DRAWMODE_RECTANGLE:
					event.manager.setSelectionMode(new SpanSelectionMode(tileset.getTileSize(), false), true);
					// Hole Selection direkt vom Manager, da evtl. nicht im Event vorhanden.
					Selection selection = event.manager.getSelection();
					int x = selection.getX() / tileset.getTileSize();
					int y = selection.getY() / tileset.getTileSize();
					setTempLayer(x, y, tileSelection.width, tileSelection.height);
					editor.getGLCanvas().redraw();
			}
		}

		if(event.mouseEvent.button == 3 && tempLayer != null)
		{
			tempLayer = null;
			editor.getGLCanvas().redraw();
		}
	}

	@Override
	public void updateSelection(SelectionEvent event)
	{
		if (editor.getActiveLayer() != TileMapLayer.this || currentTool == DRAWMODE_NONE) return;
		
		if (event.mouseEvent.button == 1)
		{
			updateSelection();
			switch (currentTool)
			{
				case DRAWMODE_PENCIL:
					setTiles(tileSelection.x, tileSelection.y, sourceLayer, null);
					break;

				case DRAWMODE_RECTANGLE:
					int x = event.selection.getX() / tileset.getTileSize();
					int y = event.selection.getY() / tileset.getTileSize();
					setTempLayer(x, y, tileSelection.width, tileSelection.height);
					break;
			}
		}
	}

	@Override
	public void endSelection(SelectionEvent event)
	{
		if (editor.getActiveLayer() != TileMapLayer.this)
			return;

		if (tempLayer != null)
		{
			// Point p = new Point(tileSelection.x, tileSelection.y);
			setTiles(tileSelection.x, tileSelection.y, tempLayer, null);
			tempLayer = null;
		}
		addSavePoint();
		initTool();
	}

	@Override
	public void keyActionPerformed(KeyEvent ev)
	{
		/* TODO: Wird doch getriggert, aber erst wenn die Ebene aktiviert ist.
		 * Da ich den Cursor benutzen will (mit STRG soll er dem Raster folgen),
		 * muss der Editor Events immer durchreichen.
		 */
		if(ev.keyCode == 'c' && (ev.stateMask & SWT.CONTROL) != 0)
		{
			System.out.println("copy");
		}
	}

	@Override
	public void activated()
	{
		initTool();
	}

	@Override
	public void deactivated()
	{
		this.currentTool = DRAWMODE_NONE;
	}
}
