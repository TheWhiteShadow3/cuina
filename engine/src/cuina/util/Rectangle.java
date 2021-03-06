package cuina.util;

import java.io.Serializable;


/**
 * Ein Rechteck.
 * @author TheWhiteShadow
 */
public class Rectangle implements Serializable
{
	public static final int OUT_LEFT 	= 1;
	public static final int OUT_TOP 	= 2;
	public static final int OUT_RIGHT 	= 4;
	public static final int OUT_BOTTOM 	= 8;
	
	private static final long serialVersionUID = 2107980141305868313L;
	
	public int x;
	public int y;
	public int width;
	public int height;
	
	public Rectangle() {}

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

	public void set(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void set(Rectangle r)
	{
		set(r.x, r.y, r.width, r.height);
	}
	
	public void add(Rectangle r)
	{
        long tx2 = this.width;
        long ty2 = this.height;
        if ((tx2 | ty2) < 0)
        {
            set(r.x, r.y, r.width, r.height);
        }
        long rx2 = r.width;
        long ry2 = r.height;
        if ((rx2 | ry2) < 0)
        {
            return;
        }
        int tx1 = this.x;
        int ty1 = this.y;
        tx2 += tx1;
        ty2 += ty1;
        int rx1 = r.x;
        int ry1 = r.y;
        rx2 += rx1;
        ry2 += ry1;
        if (tx1 > rx1) tx1 = rx1;
        if (ty1 > ry1) ty1 = ry1;
        if (tx2 < rx2) tx2 = rx2;
        if (ty2 < ry2) ty2 = ry2;
        tx2 -= tx1;
        ty2 -= ty1;
        if (tx2 > Integer.MAX_VALUE) tx2 = Integer.MAX_VALUE;
        if (ty2 > Integer.MAX_VALUE) ty2 = Integer.MAX_VALUE;
        set(tx1, ty1, (int) tx2, (int) ty2);
	}
	
	public void setLocation(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void setSize(int width, int height)
	{
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

	public boolean contains(Rectangle r)
	{
		return contains(r.x, r.y, r.width, r.height);
	}
	
	public boolean contains(int X, int Y, int W, int H)
	{
		int w = this.width;
		int h = this.height;
		if ((w | h | W | H) < 0) return false;
		
		int x = this.x;
		int y = this.y;
		if (X < x || Y < y) return false;
		
		w += x;
		W += X;
		if (W <= X)
		{
			if (w >= x || W > w) return false;
		}
		else
		{
			if (w >= x && W > w) return false;
		}
		h += y;
		H += Y;
		if (H <= Y)
		{
			if (h >= y || H > h) return false;
		}
		else
		{
			if (h >= y && H > h) return false;
		}
		return true;
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
	
    public Rectangle intersection(Rectangle r)
    {
        int tx1 = this.x;
        int ty1 = this.y;
        int rx1 = r.x;
        int ry1 = r.y;
        long tx2 = tx1; tx2 += this.width;
        long ty2 = ty1; ty2 += this.height;
        long rx2 = rx1; rx2 += r.width;
        long ry2 = ry1; ry2 += r.height;
        if (tx1 < rx1) tx1 = rx1;
        if (ty1 < ry1) ty1 = ry1;
        if (tx2 > rx2) tx2 = rx2;
        if (ty2 > ry2) ty2 = ry2;
        tx2 -= tx1;
        ty2 -= ty1;

        if (tx2 < Integer.MIN_VALUE) tx2 = Integer.MIN_VALUE;
        if (ty2 < Integer.MIN_VALUE) ty2 = Integer.MIN_VALUE;
        return new Rectangle(tx1, ty1, (int) tx2, (int) ty2);
    }
    
	public boolean intersectsLine(int x1, int y1, int x2, int y2)
	{
		int out2 = outcode(x2, y2);
		if (out2 == 0) return true;
		
		int out1 = outcode(x1, y1);
		while (out1 != 0)
		{
			if ((out1 & out2) != 0) return false;
			
			if ((out1 & (OUT_LEFT | OUT_RIGHT)) != 0)
			{
				int x = this.x;
				if ((out1 & OUT_RIGHT) != 0)
				{
					x += this.width;
				}
				y1 = y1 + (x - x1) * (y2 - y1) / (x2 - x1);
				x1 = x;
			}
			else
			{
				int y = this.y;
				if ((out1 & OUT_BOTTOM) != 0)
				{
					y += this.height;
				}
				x1 = x1 + (y - y1) * (x2 - x1) / (y2 - y1);
				y1 = y;
			}
		}
		return true;
	}
	
	public int outcode(int x, int y)
	{
		int out = 0;
		if (this.width <= 0)
		{
			out |= OUT_LEFT | OUT_RIGHT;
		}
		else if (x < this.x)
		{
			out |= OUT_LEFT;
		}
		else if (x > this.x + (long) this.width)
		{
			out |= OUT_RIGHT;
		}
		if (this.height <= 0)
		{
			out |= OUT_TOP | OUT_BOTTOM;
		}
		else if (y < this.y)
		{
			out |= OUT_TOP;
		}
		else if (y > this.y + (long) this.height)
		{
			out |= OUT_BOTTOM;
		}
		return out;
	}
	
	public int distY(Rectangle r)
	{
		int ty1 = this.y;
		int ry1 = r.y;
		long ty2 = ty1; ty1 += this.height;
		long ry2 = ry1; ry2 += r.height;
		
		int d = (int) (ty1 < ry1 ? ry1-ty2 : ty1-ry2);
		if (d < 0) d = 0;
		return d;
	}
	
	public int distX(Rectangle r)
	{
		int tx1 = this.x;
		int rx1 = r.x;
		long tx2 = tx1; tx1 += this.width;
		long rx2 = rx1; rx2 += r.width;

		int d = (int) (tx1 < rx1 ? rx1-tx2 : tx1-rx2);
		if (d < 0) d = 0;
		return d;
	}
	
	public boolean isEmpty()
	{
		return (width <= 0) || (height <= 0);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + width;
		result = prime * result + height;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj instanceof Rectangle)
		{
			Rectangle other = (Rectangle) obj;
			return (x == other.x &&
					y == other.y &&
					width == other.width &&
					height == other.height);
		}
		return false;
	}

	@Override
	public String toString()
	{
		return "Rectangle [x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "]";
	}
}
