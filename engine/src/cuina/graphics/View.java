package cuina.graphics;

import static org.lwjgl.opengl.GL11.*;

import cuina.world.CuinaObject;

import java.awt.Rectangle;

public class View
{
	public int x;
	public int y;
	public int width;
	public int height;
	
	public final Rectangle port;
//	public Camera camera;
	
	public CuinaObject target;
	public Rectangle border;
	public int threshold = 32;
	
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
		glViewport(port.x, port.y, port.width, port.height);
		
		scroll();
		
		GLCache.setMatrix(GL_PROJECTION);
		glLoadIdentity();
		// left, right, bottom, top, Zfar, Znear
		glOrtho(x, x + width, y + height, y, -500, 500);

		GLCache.setMatrix(GL_MODELVIEW);
		glLoadIdentity();
		
		glDisable(GL_DEPTH_TEST);
		
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
