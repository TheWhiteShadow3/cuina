package cuina.graphics;

import static org.lwjgl.opengl.GL11.*;

import cuina.util.Rectangle;
import cuina.world.CuinaObject;

public class View
{
	transient private static View currentView;
	
	public int x;
	public int y;
	public int width;
	public int height;
	
	public final Rectangle port;
	public Camera camera;
	
	/**
	 * Die Grafik, die der View zeichen soll.
	 * Ist das Feld <code>null</code>, wird {@link Graphics#GraphicManager} gezeichnet.
	 */
	public Graphic graphic;
	public CuinaObject target;
	public Rectangle border;
	public int threshold = 32;
	public boolean visible = true;
	public boolean flipX;
	public boolean flipY;
	
	public View()
	{
		this(0, 0, Graphics.getWidth(), Graphics.getHeight());
	}
	
	public View(int width, int height)
	{
		this(0, 0, width, height);
	}

	public View(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.port = new Rectangle(0, 0, width, height);
	}
	
	public void draw()
	{
		if (!visible) return;
		
		glViewport(port.x, port.y, port.width, port.height);
		
		scroll();
		
		GLCache.setMatrix(GL_PROJECTION);
		glLoadIdentity();
		
		if (camera != null)
		{
			camera.apply();
			glEnable(GL_DEPTH_TEST);
		}
		else
		{
			setOrtho();
			glDisable(GL_DEPTH_TEST);
		}
		
		GLCache.setMatrix(GL_MODELVIEW);
		glLoadIdentity();

		currentView = this;
		if (graphic != null)
			graphic.draw();
		else
			Graphics.GraphicManager.draw();
		currentView = null;
	}
	
	private void setOrtho()
	{
		int l, r, b, t;
		
		if (flipX)	{ l = x + width; 	r = x; }
		else 		{ l = x; 			r = x + width; }
		if (flipY)	{ b = y; 			t = y + height; }
		else 		{ b = y + height;	t = y; }
		// left, right, bottom, top, Zfar, Znear
		glOrtho(l, r, b, t, -500, 500);
	}
	
	public void scroll()
	{
		if (target == null) return;
		
		int tx = (int) target.getX() - width / 2;
		int ty = (int) target.getY() - height / 2;
	
		int diffX = tx - x;
		int diffY = ty - y;

		if (Math.abs(diffX) > threshold)
		{
			tx += (diffX < 0) ? threshold : -threshold;
		}
		if (Math.abs(diffY) > threshold)
		{
			ty += (diffY < 0) ? threshold : -threshold;
		}

		if (border != null)
		{
			this.x = Math.max(border.x, Math.min(tx, border.width - width));
			this.y = Math.max(border.y, Math.min(ty, border.height - height));
		}
	}
	
	/**
	 * Gibt den aktuellen View zurück, der gezeichnet wird.
	 * <p>
	 * Die Methode gibt nur innerhalb der Zeichenroutine im aktuellen Thread ein View-Objekt zurück.
	 * Außerhalb davon ist der Rückgabewert immer <code>null</code>.
	 * </p>
	 * @return Der aktuelle View.
	 */
	public static View getCurrent()
	{
		if (Graphics.getGraphicThread() != Thread.currentThread()) return null;
		return currentView;
	}
}
