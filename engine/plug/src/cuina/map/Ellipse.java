package cuina.map;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/** <XXX:/> <b>Unvollständig und Unbenutzt!</b>
 * Sollte ursprünglich eine Alternative Kollisions-Masken-Form darstellen.
 */
public class Ellipse implements Shape
{
	private int x;
	private int y;
	private int a;
	private int b;
	
	public Ellipse(int x, int y, int radius)
	{
		this.x = x;
		this.y = y;
		this.a = radius;
		this.b = radius;
	}
	
	public Ellipse(int x1, int y1, int x2, int y2)
	{
		this.a = (x2 - x1) / 2;
		this.b = (y2 - y1) / 2;
		this.x = a + x1;
		this.y = b + y1;
	}
	
	public Ellipse(Rectangle rect)
	{
		this.a = (rect.width) / 2;
		this.b = (rect.height) / 2;
		this.x = a + rect.x;
		this.y = b + rect.y;
	}
	
	@Override
	public Rectangle getBounds()
	{
		return new Rectangle(x - a, y - b, 2*a, 2*b);
	}

	@Override
	public Rectangle2D getBounds2D()
	{
		return getBounds();
	}

	@Override
	public boolean contains(double x, double y)
	{
		if (x < this.x)
		{
			return x*x > this.x - this.a + (this.a * this.y * this.y / this.b);
		}
		return false;
	}

	@Override
	public boolean contains(Point2D p)
	{
		return contains(p.getX(), p.getY());
	}

	@Override
	public boolean intersects(double x, double y, double w, double h)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean intersects(Rectangle2D r)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(double x, double y, double w, double h)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(Rectangle2D r)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at)
	{
		return null;
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness)
	{
		return null;
	}

}
