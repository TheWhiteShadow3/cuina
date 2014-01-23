package cuina.util;

/**
 * @author TheWhiteShadow
 */
public class Rectangle
{
	public int x;
	public int y;
	public int width;
	public int height;
	
    public Rectangle()
    {
        this(0, 0, 0, 0);
    }
    
    public Rectangle(Rectangle r)
    {
        this(r.x, r.y, r.width, r.height);
    }
    
    public Rectangle(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

	public Rectangle(int width, int height)
	{
		this(0, 0, width, height);
	}

//	public int getX()
//	{
//		return x;
//	}
//
//	public int getY()
//	{
//		return y;
//	}
//
//	public int getWidth()
//	{
//		return width;
//	}
//
//	public int getHeight()
//	{
//		return height;
//	}

	public void set(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void translate(int dx, int dy)
	{
		this.x += dx;
		this.y += dy;
	}

	public boolean contains(int x, int y)
	{
		int w = this.width;
		int h = this.height;
		if ((w | h) < 0) return false;
		if (x < this.x || y < this.y) return false;
		
		w += this.x;
		h += this.y;
		// overflow || intersect
		return ((w < x || w > this.x) && (h < y || h > this.y));
	}

	public boolean intersects(Rectangle r)
	{
		int tw = this.width;
		int th = this.height;
		int rw = r.width;
		int rh = r.height;
		if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) return false;
		
		int tx = this.x;
		int ty = this.y;
		int rx = r.x;
		int ry = r.y;
		rw += rx;
		rh += ry;
		tw += tx;
		th += ty;

		return ((rw < rx || rw > tx) &&
				(rh < ry || rh > ty) &&
				(tw < tx || tw > rx) &&
				(th < ty || th > ry));
	}
	
    public boolean isEmpty()
    {
        return (width <= 0) || (height <= 0);
    }
    
    @Override
	public String toString()
    {
        return "Rectangle [x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "]";
    }
}
