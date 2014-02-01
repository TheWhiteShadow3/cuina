package cuina.editor.map.internal;

import cuina.editor.core.CuinaProject;
import cuina.map.Tileset;
import cuina.resource.ResourceException;
import cuina.resource.ResourceManager;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class TileMaskPanel extends Canvas implements MouseListener, MouseMoveListener
{
	private static final int COLOUMNS = 4;
	private static final int FILED_SIZE = 32;
	private static final int PANEL_SIZE = FILED_SIZE * COLOUMNS;
	private static final int FILEDS = COLOUMNS * COLOUMNS;
	
	private short 	maskData;
	private int		button;
	private int 	lastField = -1;
	
	private final List<Listener> listeners = new ArrayList<Listener>(1);
	private CuinaProject project;
	private Tileset tileset;
	private Image image;
	private int id;
	
	public TileMaskPanel(Composite parent, int style)
	{
		super(parent, style);
		setSize(PANEL_SIZE, PANEL_SIZE);
		addPaintListener(getPaintListener());
		addMouseListener(this);
		addMouseMoveListener(this);
	}
	
	public void setTileset(CuinaProject project, Tileset tileset)
	{
		if (this.tileset == tileset) return;
		this.project = project;
		
		Tileset oldTileset = this.tileset;
		this.tileset = tileset;
		if (oldTileset != null && tileset != null && oldTileset.getTilesetName() == tileset.getTilesetName())
		{
			return;
		}
		refreshImage();
	}

	public void refreshImage()
	{
		if (image != null)
		{
			image.dispose();
		}
		if (tileset != null) try
		{
			image = ResourceManager.loadImage(project, tileset.getTilesetName());
		}
		catch (ResourceException e)
		{
			image = null;
		}
		id = 0;
		loadFieldData();
	}
	
	public Tileset getTileset()
	{
		return tileset;
	}
	
	public int getTileID()
	{
		return id;
	}

	public void setTileID(int id)
	{
		this.id = id;
		loadFieldData();
	}
	
	private void loadFieldData()
	{
		short newData;
		if (tileset == null || id <= 0)
			newData = 0;
		else
			newData = tileset.getPassages()[id];
		
		maskData = newData;
		redraw();
	}

	public short getMaskData()
	{
		return maskData;
	}
	
	private boolean isSet(int index)
	{
		return (maskData & (1 << index)) != 0;
	}
	
	private void set(int index, boolean on)
	{
		if (tileset == null || id == -1) return;
		
		short oldData = maskData;
		if (on)
			maskData = (short)(maskData | (1 << index));
		else
			maskData = (short)(maskData & ~(1 << index));
		
		if (oldData == maskData) return;
		
		tileset.getPassages()[id] = maskData;
		fireSelectionListener(index);

		Point p = new Point(index % COLOUMNS * FILED_SIZE, index / COLOUMNS * FILED_SIZE);
		redraw(p.x, p.y, FILED_SIZE, FILED_SIZE, false);
	}
	
	private void flip(int index)
	{
		set(index, !isSet(index));
	}
	
	public void addListener(Listener l)
	{
		listeners.add(l);
	}

	public void removeListener(Listener l)
	{
		listeners.remove(l);
	}
	
	private void fireSelectionListener(int index)
	{
		Event event = new Event();
		event.type = SWT.Selection;
		event.widget = this;
		event.index = index;
		
		for(Listener l : listeners)
		{
			l.handleEvent(event);
		}
	}
	
	private int getFieldID(int x, int y)
	{
		Point p = getSize();
		if (x < 0 || y < 0 || x >= p.x || y >= p.y) return -1;
		return (x / FILED_SIZE) + (y / FILED_SIZE) * COLOUMNS;
	}
	
	private PaintListener getPaintListener()
	{
		return new PaintListener()
		{
			@Override
			public void paintControl(PaintEvent e)
			{
				e.gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
				e.gc.fillRectangle(0, 0, PANEL_SIZE, PANEL_SIZE);
				
				if (image != null && id > 0)
				{
					ImageData data = image.getImageData();
					int gw = (data.width + tileset.getTileSize() - 1) / tileset.getTileSize();
					int ts = tileset.getTileSize();
					int x = (id - 1) % gw * ts;
					int y = (id - 1) / gw * ts;
					float hs = data.width - x < ts ? (data.width - x) / (float) ts : 1f;
					float vs = data.height - y < ts ? (data.height - y) / (float) ts : 1f;
					
					e.gc.drawImage(image,
							x, y, (int) (hs * ts), (int) (vs * ts),
							0, 0, (int) (hs * PANEL_SIZE), (int) (vs * PANEL_SIZE));
				}
				
				e.gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
				e.gc.setAlpha(128);
				for(int i = 0; i < FILEDS; i++)
				{
					if (isSet(i))
					{
						e.gc.fillRectangle(
								(i % COLOUMNS) * FILED_SIZE,
								(i / COLOUMNS) * FILED_SIZE,
								FILED_SIZE,
								FILED_SIZE);
					}
				}
			}
		};
	}

	@Override
	public void mouseDown(MouseEvent e)
	{
		if (!isEnabled()) return;
		
		lastField = getFieldID(e.x, e.y);
		flip(lastField);
		button = e.button;
	}

	@Override
	public void mouseUp(MouseEvent e)
	{
		button = 0;
	}

	@Override
	public void mouseMove(MouseEvent e)
	{
		if (!isEnabled() || button == 0) return;
		
		if (e.x < 0 || e.x >= PANEL_SIZE || e.y < 0 || e.y >= PANEL_SIZE) return;
		
		int field = getFieldID(e.x, e.y);
		if (field == -1 || lastField == field) return;
		set(field, isSet(lastField));
		lastField = field;
	}

	@Override public void mouseDoubleClick(MouseEvent e) {}
}
