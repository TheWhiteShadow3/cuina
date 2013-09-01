package cuina.data;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public class Area extends Rectangle
{
	private static final long serialVersionUID = -3558891400635336565L;
	
	private int id = -1;
	public String caption = "Area";
	
	public Area()
	{
		super();
	}

	public Area(int x, int y, int width, int height)
	{
		super(x, y, width, height);
	}

	public Area(Point p, Dimension d)
	{
		super(p, d);
	}

	public Area(Point p)
	{
		super(p);
	}

	public Area(Rectangle r)
	{
		super(r);
	}

	public int getID()
	{
		return id;
	}

	public void setID(int id)
	{
		this.id = id;
	}
}
