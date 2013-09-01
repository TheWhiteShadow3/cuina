package cuina.editor.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

@Deprecated
public class ScrollPane extends Canvas
{
	private Point origin = new Point(0, 0);
	private Point viewSize;
	
	public ScrollPane(Composite parent, int width, int height)
	{
		this(parent, width, height, SWT.NO_REDRAW_RESIZE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.DOUBLE_BUFFERED);
	}
	
	public ScrollPane(Composite parent, int width, int height, int style)
	{
		super(parent, style);
		
		this.viewSize = new Point(width, height);
		
		final ScrollBar hBar = getHorizontalBar();
		final ScrollBar vBar = getVerticalBar();
		
		if (hBar != null)
		{
			
			hBar.setIncrement(32);
			hBar.setPageIncrement(128);
			hBar.addListener(SWT.Selection, new Listener()
			{
				@Override
				public void handleEvent(Event e)
				{
					int hSelection = hBar.getSelection();
					int destX = -hSelection - origin.x;
					scroll(destX, 0, 0, 0, viewSize.x, viewSize.y, false);
					origin.x = -hSelection;
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
					int vSelection = vBar.getSelection();
					int destY = -vSelection - origin.y;
					scroll(0, destY, 0, 0, viewSize.x, viewSize.y, false);
					origin.y = -vSelection;
				}
			});
		}
		
		addListener(SWT.Resize, new Listener()
		{
			@Override
			public void handleEvent(Event e)
			{
				Rectangle client = getClientArea();
				if (hBar != null)
				{
					hBar.setMaximum(viewSize.x);
					hBar.setThumb(Math.min(viewSize.x, client.width));
					int hPage = viewSize.x - client.width;
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
					vBar.setThumb(Math.min(viewSize.y, client.height));
					int vPage = viewSize.y - client.height;
					int vSelection = vBar.getSelection();
					if (vSelection >= vPage)
					{
						if (vPage <= 0) vSelection = 0;
						origin.y = -vSelection;
					}
				}
				redraw();
			}
		});
	}
	
	public void setViewSize(int x, int y)
	{
		viewSize.x = x;
		viewSize.y = y;
		if(!isDisposed())
			redraw();
	}
	
	public Point getViewSize()
	{
		return new Point(viewSize.x, viewSize.y);
	}
	
	public Point getOrigin()
	{
		return origin;
	}
}
