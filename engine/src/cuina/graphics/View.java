package cuina.graphics;

import static org.lwjgl.opengl.GL11.*;

import cuina.util.Rectangle;
import cuina.world.CuinaObject;

public class View
{
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
			// left, right, bottom, top, Zfar, Znear
			glOrtho(x, x + width, y + height, y, -500, 500);
			glDisable(GL_DEPTH_TEST);
		}
		
		GLCache.setMatrix(GL_MODELVIEW);
		glLoadIdentity();

		if (graphic != null)
			graphic.draw();
		else
			Graphics.GraphicManager.draw();
	}
	
	public void scroll()
	{
		if (target == null) return;
		
		int tx = (int) target.getX() - width / 2;
		int ty = (int) target.getY() - height / 2;
		
		if (border == null)
		{
			this.x = tx;
			this.y = ty;
		}
		else
		{
			int diffX = tx - x;
			int diffY = ty - y;

			if (Math.abs(diffX) > threshold)
			{
				tx += (diffX < 0) ? threshold : -threshold;
				this.x = Math.max(border.x, Math.min(tx, border.width - width));
			}
			if (Math.abs(diffY) > threshold)
			{
				ty += (diffY < 0) ? threshold : -threshold;
				this.y = Math.max(border.y, Math.min(ty, border.height - height));
			}
		}
	}
}
