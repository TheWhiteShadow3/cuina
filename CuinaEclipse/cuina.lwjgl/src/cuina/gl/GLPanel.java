package cuina.gl;


import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;

public class GLPanel
{
	protected GLCanvas canvas;
	private boolean use3D; //XXX: 3D wird (noch) nicht unterstützt.
	private Rectangle viewport;
	private int margin = 0;
	private ReadableColor backgroundColor = Color.LTGREY;
	
	private final ArrayList<PaintListener> listeners = new ArrayList<PaintListener>(4);
	
	public GLPanel(Composite parent)
	{
		this(parent, SWT.NONE, LWJGL.NONE);
	}
	
	public GLPanel(Composite parent, int swtStyle, int glStyle)
	{
		if ((glStyle & LWJGL.ENABLE_3D) != 0) 		use3D = true;
		
		if (use3D) System.err.println("3D wird (noch) nicht unterstützt!");
		
		GLData data = new GLData();
		data.doubleBuffer = true;
		canvas = new CuinaGLCanvas(parent, swtStyle, data, getPainter());
		canvas.setDragDetect(true);
		
		parent.layout();
		init();
	}

	/**
	 * Gibt das Control zurück.
	 * <p>
	 * Dieses Objekt stellt gleichzeitig den GLContext da.
	 * </p>
	 * @return Das GLCanvas
	 */
	public GLCanvas getGLCanvas()
	{
		return canvas;
	}
	
	public void refresh()
	{
		canvas.redraw();
	}
	
	public int getMargin()
	{
		return margin;
	}

	public void setMargin(int margin)
	{
		this.margin = margin;
	}
	
	public Rectangle getViewportBounds()
	{
		return new Rectangle(viewport.x, viewport.y, viewport.width, viewport.height);
	}

	public ReadableColor getBackgroundColor()
	{
		return backgroundColor;
	}

	public void setBackgroundColor(ReadableColor color)
	{
		this.backgroundColor = color;
	}

	public Rectangle getBounds()
	{
		return canvas.getBounds();
	}

	public boolean addPaintListener(PaintListener l)
	{
		return listeners.add(l);
	}

	public boolean removePaintListener(PaintListener l)
	{
		return listeners.remove(l);
	}
	
	private Runnable getPainter()
	{
		return new Runnable()
		{
			@Override
			public void run()
			{
				if (canvas.isDisposed() || listeners.size() == 0) return;
				
//				System.out.println("[GLPanel] firePaintEvent");
				clear();
				
				GC gc = new GC(canvas, viewport);
				
				gc.translate2D(-viewport.x + margin, -viewport.y + margin);
				for (PaintListener l : listeners)
				{
					l.paint(gc);
				}
					canvas.swapBuffers();
			}
		};
	}
	
	private void init()
	{
		LWJGL.init(null);
		setCurrent();
		
		glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		glEnable(GL_NORMALIZE);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	private void clear()
	{
		setCurrent();

		glClearColor(backgroundColor.getRed() / 255f,
					 backgroundColor.getGreen() / 255f,
					 backgroundColor.getBlue() / 255f, 1f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		glMatrixMode(GL11.GL_MODELVIEW);
		glLoadIdentity();
	}
	
	public void setCurrent()
	{
		if (canvas.isDisposed()) return;
		
		canvas.setCurrent();
		try
		{
			GLContext.useContext(canvas);
		}
		catch (LWJGLException e)
		{
			e.printStackTrace();
			return;
		}
		viewport = canvas.getClientArea();
		if (viewport.width == 0 || viewport.height == 0) return;
		
		glViewport(0, 0, viewport.width, viewport.height);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, viewport.width, viewport.height, 0, -1, 1);
	}
	
	public Point toControl(int x, int y)
	{
		Point result = canvas.toControl(x, y);
		result.x -= margin;
		result.y -= margin;
		return result;
	}
	
	private static class CuinaGLCanvas extends GLCanvas implements FocusListener, Listener
	{
		private final Runnable painter;
		private final Point scrollPos = new Point(0, 0);
		
		public CuinaGLCanvas(Composite parent, int style, GLData data, Runnable painter)
		{
			super(parent, SWT.NO_BACKGROUND | style, data);
			this.painter = painter;
			this.addFocusListener(this);
			this.addListener(SWT.RESIZE, this);
			
//			//XXX: Teste Drag-Support. Bisher ohne Erfolg. Keins der Events wird getriggert.
//			Listener listener = getListener();
			this.addListener(DND.DragEnter, this);
			this.addListener(DND.DragLeave, this);
			this.addListener(DND.DragEnd, this);
			this.addListener(DND.DragOver, this);
			this.addListener(DND.DragStart, this);
			this.addListener(SWT.DragDetect, this);
		}
		
		@Override
		public void redraw()
		{
			painter.run();
		}
		
		@Override
		public void redraw(int x, int y, int width, int height, boolean all)
		{
			redraw();
		}

		@Override
		public void scroll(int destX, int destY, int x, int y, int width, int height, boolean all)
		{
			if (destX == scrollPos.x && destY == scrollPos.y) return;
			
//			System.out.println("Scroll: " + destX + ", " + destY);
			scrollPos.x = destX;
			scrollPos.y = destY;
			redraw();
		}
		
		@Override
		public Rectangle getClientArea()
		{
			Rectangle rect = super.getClientArea();
			rect.x = scrollPos.x;
			rect.y = scrollPos.y;
			return rect;
		}

		@Override
		public void focusGained(FocusEvent e)
		{
			redraw();
		}

		@Override
		public void focusLost(FocusEvent e)
		{
			redraw();
		}

		@Override
		public void handleEvent(Event event)
		{
			redraw();
		}
	}
}
