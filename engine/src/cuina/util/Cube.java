package cuina.util;

import java.io.Serializable;

public class Cube implements Serializable
{
	private static final long serialVersionUID = 8241448046493927549L;

	public int x;
	public int y;
	public int z;
	
	public int width;
	public int height;
	public int depth;
	
	public Cube() {}
	
	public Cube(int x, int y, int z, int width, int height, int depth)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.height = height;
		this.depth = depth;
	}
	
	public Cube(Cube c)
	{
		this(c.x, c.y, c.z, c.width, c.height, c.depth);
	}
	
	public Cube(int width, int height, int depth)
	{
		this.width = width;
		this.height = height;
		this.depth = depth;
	}
	
	public void set(int x, int y, int z, int width, int height, int depth)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.height = height;
		this.depth = depth;
	}
	
	public void set(Cube c)
	{
		set(c.x, c.y, c.z, c.width, c.height, c.depth);
	}
	
	public void setLocation(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void setSize(int width, int height, int depth)
	{
		this.width = width;
		this.height = height;
		this.depth = depth;
	}
	
	public boolean contains(int x, int y, int z)
	{
		int w = this.width;
		int h = this.height;
		int d = this.depth;
		if ((w | h | d) < 0) return false;
		if (x < this.x || y < this.y || z < this.z) return false;
		
		w += this.x;
		h += this.y;
		d += this.z;
		// overflow || intersect
		return ((w < x || w > this.x) && (h < y || h > this.y) && (d < z || d > this.z));
	}
	
	public boolean contains(Vector v)
	{
		return contains((int) v.x, (int) v.y, (int) v.z);
	}
	
	public boolean intersects(Cube c)
	{
		int tw = this.width;
		int th = this.height;
		int td = this.depth;
		int rw = c.width;
		int rh = c.height;
		int rd = c.depth;
		if (rw <= 0 || rh <= 0 || rd <= 0 || tw <= 0 || th <= 0 || td <= 0) return false;
		
		int tx = this.x;
		int ty = this.y;
		int tz = this.z;
		int rx = c.x;
		int ry = c.y;
		int rz = c.z;
		rw += rx;
		rh += ry;
		rd += rz;
		tw += tx;
		th += ty;
		td += tz;

		return ((rw < rx || rw > tx) &&
				(rh < ry || rh > ty) &&
				(td < tz || td > rz) &&
				(tw < tx || tw > rx) &&
				(th < ty || th > ry) &&
				(td < tz || td > rz));
	}
}
