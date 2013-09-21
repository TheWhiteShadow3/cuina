package cuina.editor.ui;

import cuina.editor.ui.selection.Selection;
import cuina.editor.ui.selection.SelectionManager;
import cuina.gl.GC;
import cuina.gl.GLPanel;
import cuina.gl.LWJGL;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.lwjgl.util.Color;

public abstract class AbstractSelectionPanel extends GLPanel
{
	public Color SELECTION_COLOR_1 = new Color(0, 0, 0);
	public Color SELECTION_COLOR_2 = new Color(224, 224, 160);
	
	private Point origin = new Point(0, 0);
	private Rectangle selectionArea;
	private SelectionManager selectionHandler;

	private ScrollBar hBar;
	private ScrollBar vBar;
	private Point viewSize;
	
	public AbstractSelectionPanel(Composite parent, int width, int height)
	{
		this(parent, width, height, SWT.DOUBLE_BUFFERED, LWJGL.NONE);
	}
	
	public AbstractSelectionPanel(Composite parent, int width, int height, int swtStyle, int glStyle)
	{
		super(parent, SWT.H_SCROLL | SWT.V_SCROLL | swtStyle, glStyle);
		
		this.selectionArea = new Rectangle(0, 0, width, height);
		this.selectionHandler = new SelectionManager(getGLCanvas(), selectionArea);
		this.viewSize = new Point(width, height);
		
		addScrollHandling();
	}
	
	public void setViewSize(int width, int height)
	{
		int m = 2 * getMargin();
		this.viewSize = new Point(width + m, height + m);
		handleResize(canvas.getClientArea());
	}
	
	private Listener getResizeListener()
	{
		return new Listener()
		{
			@Override
			public void handleEvent(Event event)
			{
				handleResize(canvas.getClientArea());
				canvas.redraw();
			}
		};
	}
	
	private void addScrollHandling()
	{
		canvas.addListener(SWT.Resize, getResizeListener());
		hBar = canvas.getHorizontalBar();
		vBar = canvas.getVerticalBar();
		
		if (hBar != null)
		{
			hBar.setIncrement(32);
			hBar.setPageIncrement(128);
			hBar.addListener(SWT.Selection, new Listener()
			{
				@Override
				public void handleEvent(Event e)
				{
					origin.x = -hBar.getSelection();
					canvas.scroll(-origin.x, -origin.y, 0, 0, viewSize.x, viewSize.y, false);
					updateSelectionArea();
				}
			});
		}
		
		if (vBar != null)
		{
			vBar.setIncrement(32);
			vBar.setPageIncrement(128);
			vBar.addListener(SWT.Selection, new Listener()
			{
				@Override
				public void handleEvent(Event e)
				{
					origin.y = -vBar.getSelection();
					canvas.scroll(-origin.x, -origin.y, 0, 0, viewSize.x, viewSize.y, false);
					updateSelectionArea();
				}
			});
		}
	}
	
	@Override
	public void setMargin(int margin)
	{
		int diff = margin - super.getMargin();
		super.setMargin(margin);
		viewSize.x += 2 * diff;
		viewSize.y += 2 * diff;
		updateSelectionArea();
	}
	
	public SelectionManager getSelectionManager()
	{
		return selectionHandler;
	}
	
	public int getWidth()
	{
		return viewSize.x; 
	}
	
	public int getHeight()
	{
		return viewSize.y;
	}
	
	protected void handleResize(Rectangle bounds)
	{
		if (hBar != null)
		{
			hBar.setMaximum(viewSize.x);
			hBar.setThumb(Math.min(viewSize.x, bounds.width));
			int hPage = viewSize.x - bounds.width;
			int hSelection = hBar.getSelection();
			if (hSelection >= hPage)
			{
				if (hPage <= 0) hSelection = 0;
				origin.x = -hSelection;
			}
		}
		if (vBar != null) 
		{
			vBar.setMaximum(viewSize.y);
			vBar.setThumb(Math.min(viewSize.y, bounds.height));
			int vPage = viewSize.y - bounds.height;
			int vSelection = vBar.getSelection();
			if (vSelection >= vPage)
			{
				if (vPage <= 0) vSelection = 0;
				origin.y = -vSelection;
			}
		}
		
		canvas.scroll(-origin.x, -origin.y, 0, 0, viewSize.x, viewSize.y, false);
		updateSelectionArea();
	}
	
	private void updateSelectionArea()
	{
		int margin = getMargin();
		selectionArea.x = margin + origin.x;
		selectionArea.y = margin + origin.y;
		selectionArea.width = viewSize.x - 2 * margin;
		selectionArea.height = viewSize.y - 2 * margin;
		selectionHandler.setSelectionArea(selectionArea);
	}

	protected void paintCursor(GC gc)
	{
		List<Selection> selections = selectionHandler.getSelectionList();
//		System.out.println(s);
				
		for (Selection s : selections)
		{
			if (s.getWidth() <= 3 || s.getHeight() <= 3) continue;
	
			Rectangle rect = s.getBounds();
	//		if (s.getImage() != null) gc.drawImage(s.getImage(), rect.x, rect.y);
	
			gc.setColor(SELECTION_COLOR_1);
			gc.drawRectangle(rect.x, rect.y, rect.width, rect.height);
			gc.setColor(SELECTION_COLOR_2);
			gc.drawRectangle(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2);
			gc.setColor(SELECTION_COLOR_1);
			gc.drawRectangle(rect.x + 2, rect.y + 2, rect.width - 4, rect.height - 4);
		}
	}
}
