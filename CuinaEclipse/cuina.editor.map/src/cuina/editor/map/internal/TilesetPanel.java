package cuina.editor.map.internal;

import cuina.editor.core.CuinaProject;
import cuina.editor.map.TileSelection;
import cuina.editor.map.internal.layers.TilemapUtil;
import cuina.editor.ui.AbstractSelectionPanel;
import cuina.editor.ui.selection.SelectionEvent;
import cuina.editor.ui.selection.SelectionListener;
import cuina.editor.ui.selection.SpanSelectionMode;
import cuina.gl.GC;
import cuina.gl.Image;
import cuina.gl.PaintListener;
import cuina.map.Tileset;
import cuina.resource.ResourceException;
import cuina.resource.ResourceManager;
import cuina.resource.ResourceManager.Resource;
import cuina.resource.ResourceProvider;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.lwjgl.LWJGLException;
import org.lwjgl.util.Color;

public class TilesetPanel extends AbstractSelectionPanel implements
			SelectionListener, ISelectionProvider
{
	private static final SpanSelectionMode TILEVIEW_SELECTION_MODE = new SpanSelectionMode();
	private static final Color MASK_COLOR = new Color(0, 0, 0, 128);
	
	public static final int VIEW_DEFAULT	= 0;
	public static final int VIEW_PASSAGES	= 1;
	public static final int VIEW_MASK		= 2;
	public static final int VIEW_PRIORITIES = 3;
	public static final int VIEW_FLAGS 		= 4;
	public static final int VIEW_TAGS 		= 5;
	
	private TileSelection tileSelection = TileSelection.EMPTY;
	private final ArrayList<ISelectionChangedListener> listeners = new ArrayList<ISelectionChangedListener>();
	
	private Image passableImageO;
	private Image passableImageX;
	private Image passableImageU;
	private Image numberImage;
	private Image priorityImage;

	private Tileset tileset;
	private Image image;
	private int button;
	private int viewMode = VIEW_DEFAULT;
	private boolean GridVisible;
	private int tileCount;
	private boolean fireEvent;

	public TilesetPanel(Composite parent, int width, int height)
	{
		super(parent, width, height);
		getSelectionHandler().addSelectionListener(this);
		addPaintListener(createPaintListener());
		
		try
		{
			passableImageO 	= loadIconImage("tp_pass_O.png");
			passableImageX 	= loadIconImage("tp_pass_X.png");
			passableImageU 	= loadIconImage("tp_pass_U.png");
			priorityImage 	= loadIconImage("tp_prio.png");
			numberImage 	= loadIconImage("numbers2.png");
		}
		catch (ResourceException | LWJGLException e)
		{
			e.printStackTrace();
		}
	}
	
	public int getViewMode()
	{
		return viewMode;
	}

	public void setViewMode(int viewMode)
	{
		this.viewMode = viewMode;
		refresh();
	}

	public int getTileCount()
	{
		return image == null ? 0 : tileCount;
	}

	public boolean isGridVisible()
	{
		return GridVisible;
	}

	public void setGridVisible(boolean GridVisible)
	{
		this.GridVisible = GridVisible;
	}

	public void setTileset(Tileset tileset, CuinaProject project)
	{
		this.tileset = tileset;
//		if (tileset.getTilesetName().length() > 0)
		try
		{
			Resource res = project.getService(ResourceProvider.class).
						getResource(ResourceManager.KEY_GRAPHICS, tileset.getTilesetName());
			
			this.image = new Image(getGLCanvas(), res.getPath().toString());
			setViewSize(image.getWidth(), image.getHeight());
		}
		catch (ResourceException | LWJGLException e)
		{
//			e.printStackTrace();
			this.image = null;
			setViewSize(tileset.getTileSize(), tileset.getTileSize());
		}
		refresh();
	}
	
	/**
	 * Gibt an, ob das Gitter exakt auf die Grafik passt.
	 * @return true, wenn die Gitter-Ränder mit denen, der Graifk überreinstimmt.
	 */
	public boolean isGridMatchingImage()
	{
		return image.getWidth() % tileset.getTileSize() == 0 && image.getHeight() % tileset.getTileSize() == 0;
	}
	
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener l)
	{
		listeners.add(l);
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener l)
	{
		listeners.remove(l);
	}
	
	@Override
	public ISelection getSelection()
	{
		return tileSelection;
	}

	@Override
	public void setSelection(ISelection selection)
	{
		if (!(selection instanceof TileSelection)) throw new IllegalArgumentException();
		
		this.tileSelection = (TileSelection) selection;
		fireSelectionChanged();
	}
	
	protected void fireSelectionChanged()
	{
		fireEvent = true;
		
		SelectionChangedEvent event = new SelectionChangedEvent(this, tileSelection);
		for (ISelectionChangedListener l : listeners)
		{
			l.selectionChanged(event);
		}
		
		fireEvent = false;
	}
	
	public TileSelection getSelectedTiles()
	{
		if (getSelectionHandler().getSelection().getWidth() <= 0) return TileSelection.EMPTY;
		
		short[][] data = null;
		Rectangle bounds = getSelectionHandler().getSelection().getBounds();
		int ts = tileset.getTileSize();
		data = new short[bounds.width / ts][bounds.height / ts];
		if (data.length != 0)
		{
			for (int y = 0; y < data[0].length; y++)
			for (int x = 0; x < data.length; x++)
			{
				data[x][y] = (short) (getIndex(bounds.x / ts + x, bounds.y / ts + y) + 1);
			}
		}
		
		return new TileSelection(bounds.x / ts, bounds.y / ts, data);
	}
	
	public Rectangle getSelectedRect()
	{
		Rectangle bounds = getSelectionHandler().getSelection().getBounds();
		int ts = tileset.getTileSize();
		bounds.x /= ts;
		bounds.y /= ts;
		bounds.width /= ts;
		bounds.height /= ts;
		return bounds;
	}
	
	public int getLastMouseButton()
	{
		return button;
	}
	
	private Image loadIconImage(String name) throws ResourceException, LWJGLException
	{
		return new Image(getGLCanvas(), Activator.getBundleFile("icons/" + name).getAbsolutePath());
	}
	
	/** Gibt die Breite des Grids zurück. Das Grid ist immer >= der Breite der Grafik. */
	private int getGridWidth()
	{
		return (image.getWidth() + tileset.getTileSize() - 1) / tileset.getTileSize();
	}
	
	private int getIndex(int x, int y)
	{
		int index = x + y * getGridWidth() + 1;
		if (index >= tileCount)
			return -1;
		else
			return index;
	}
	
	private short getPassage(int x, int y)
	{
		int index = getIndex(x, y);
		if (index < 0) return -1;
		return tileset.getPassages()[index];
	}
	
	private byte getPriority(int x, int y)
	{
		int index = getIndex(x, y);
		if (index < 0) return -1;
		return tileset.getPriorities()[index];
	}
	
	private byte getFlag(int x, int y)
	{
		int index = getIndex(x, y);
		if (index < 0) return -1;
		return tileset.getFlags()[index];
	}
	
	private byte getTerrainTag(int x, int y)
	{
		int index = getIndex(x, y);
		if (index < 0) return -1;
		return tileset.getTerrainTags()[index];
	}

//	private void setPassage(int x, int y, short value)
//	{
//		int index = getIndex(x, y);
//		if (index < 0) return;
//		tileset.getPassages()[index] = value;
//	}
	
//		@Override
//		public void selectionChanged(Object source, Selection selection)
//		{
//			if (editor == null) return;
//			
//			if (panel.getSelectionHandler().getSelection().getWidth() <= 0)
//			{
//				setSelection(TileSelection.EMPTY_SELECTION);
//			}
//			else
//			{
//				setSelection(new TileSelection(getSelectedTiles()));
//			}
//		}
//
//		@Override
//		public void selectionModeChanged(Object source, SelectionMode oldMode, SelectionMode newMode)
//		{
//			if (!editMode) return;
//			
//			if (oldMode == TILEVIEW_SELECTION_MODE && newMode == null)
//			{
//				Rectangle rect = getSelectedRect();
//				short value = getPassage(rect.x, rect.y);
//				value = (short) (value != 0 ? 0 : -1);
//				
//				for(int x = rect.x; x < rect.x + rect.width; x++)
//				for(int y = rect.y; y < rect.y + rect.height; y++)
//				{
//					setPassage(x, y, value);
//				}
//				panel.refresh();
//			}
//		}

	
	@Override
	public void refresh()
	{
		if (tileset == null || fireEvent) return;
		
		if (image != null)
		{
			this.tileCount = image.getWidth() / tileset.getTileSize() * image.getHeight() / tileset.getTileSize() + 1;
			if (tileCount > tileset.getPassages().length + 1)
			{
				tileset.resizeTileset(tileCount);
			}
		}
		else
			this.tileCount = tileset.getPassages().length + 1;
		
		TILEVIEW_SELECTION_MODE.setGridSize(tileset.getTileSize());
		super.refresh();
	}

	protected void paintBackground(GC gc)
	{
		TilemapUtil.paintGrid(gc, 0, 0, image.getWidth(),
				image.getHeight(), tileset.getTileSize() / 2);
	}

	protected void paintTileset(GC gc)
	{
		gc.setColor(Color.WHITE);
		gc.drawImage(image, 0, 0);
	}
	
	protected void paintOverlayImages(GC gc)
	{
		switch(viewMode)
		{
			case VIEW_PASSAGES: 	paintPassables(gc); break;
			case VIEW_MASK:			paintPassablesMask(gc); break;
			case VIEW_PRIORITIES: 	paintPriorities(gc); break;
			case VIEW_TAGS: 		paintTerrainTags(gc); break;
			case VIEW_FLAGS: 		paintFlags(gc); break;
		}
	}
	
	private void paintPassables(GC gc)
	{
		int ts = tileset.getTileSize();
		int maxX = image.getWidth() / ts;
		int maxY = image.getHeight() / ts;
		for (int y = 0; y < maxY; y++)
		for (int x = 0; x < maxX; x++)
		{
			Image img;
			
			if (getPassage(x, y) == 0)
				img = passableImageO;
			else if (getPassage(x, y) == -1)
				img = passableImageX;
			else
				img = passableImageU;
				
			gc.drawImage(img, x * ts + 4, y * ts + 4);
		}
	}
	
	private void paintPassablesMask(GC gc)
	{
		int ts = tileset.getTileSize();
		int maxX = image.getWidth() / ts;
		int maxY = image.getHeight() / ts;
		for (int y = 0; y < maxY; y++)
		for (int x = 0; x < maxX; x++)
		{
			drawTileMask(gc, x, y);
		}
	}
	
	private void drawTileMask(GC gc, int x, int y)
	{
		int value = getPassage(x, y);
		if (value == 0) return;
		
		gc.setColor(MASK_COLOR);
		int ts = tileset.getTileSize();
		if (value == -1)
		{
			gc.fillRectangle(x * ts, y * ts, ts, ts);
		}
		else
		{
			int maskSize = ts / 4;
			for(int i = 0; i < 16; i++)
			{
				if ((value & (1 << i)) != 0)
					gc.fillRectangle(x * ts + (i % 4) * maskSize, y * ts + (i / 4) * maskSize, maskSize, maskSize);
			}
		}
	}
	
	private void paintPriorities(GC gc)
	{
		int ts = tileset.getTileSize();
		int maxX = image.getWidth() / ts;
		int maxY = image.getHeight() / ts;
		for (int y = 0; y < maxY; y++)
		for (int x = 0; x < maxX; x++)
		{
			gc.drawImage(priorityImage, x * ts, y * ts);
			gc.drawImage(numberImage, getPriority(x, y) * 16, 0, 16, 16, x * ts + 16, y * ts + 4, 16, 16);
		}
	}
	
	private void paintTerrainTags(GC gc)
	{
		int ts = tileset.getTileSize();
		int maxX = image.getWidth() / ts;
		int maxY = image.getHeight() / ts;
		for (int y = 0; y < maxY; y++)
		for (int x = 0; x < maxX; x++)
		{
			gc.drawImage(numberImage, getTerrainTag(x, y) * 24, 0, 24, 24, x * ts, y * ts, 24, 24);
		}
	}
	
	private void paintFlags(GC gc)
	{
		int ts = tileset.getTileSize();
		int maxX = image.getWidth() / ts;
		int maxY = image.getHeight() / ts;
		for (int y = 0; y < maxY; y++)
		for (int x = 0; x < maxX; x++)
		{
			gc.drawImage(numberImage, getFlag(x, y) * 24, 0, 24, 24, x * ts, y * ts, 24, 24);
		}
	}

	protected void paintGrid(GC gc)
	{
		if (!GridVisible) return;
		
		gc.setColor(Color.GREY);
		int ts = tileset.getTileSize();
		int maxX = image.getWidth() / ts;
		int maxY = image.getHeight() / ts;

		for (int x = 1; x < maxX; x++)
		{
			gc.drawLine(x * ts, 0, x * ts, image.getHeight());
		}
		
		for (int y = 1; y < maxY; y++)
		{
			gc.drawLine(0, y * ts, image.getWidth(), y * ts);
		}
	}
	
	private PaintListener createPaintListener()
	{
		return new PaintListener()
		{
			@Override
			public void paint(GC gc)
			{
				if (image == null) return;
				
				paintBackground(gc);
				paintTileset(gc);
				paintOverlayImages(gc);
				paintGrid(gc);
				paintCursor(gc);
			}
		};
	}

	@Override
	public void startSelection(SelectionEvent event)
	{
		event.manager.clearSelections();
		if (event.mouseEvent.button == 1 || event.mouseEvent.button == 3)
		{
			this.button = event.mouseEvent.button;
			event.manager.setSelectionMode(TILEVIEW_SELECTION_MODE, false);
		}
		else
		{
			event.manager.clearSeletionMode();
		}
		refresh();
	}

	@Override
	public void updateSelection(SelectionEvent event)
	{}

	@Override
	public void endSelection(SelectionEvent event)
	{
		if (tileset == null) return;

		setSelection(getSelectedTiles());
		event.manager.clearSeletionMode();
	}
}