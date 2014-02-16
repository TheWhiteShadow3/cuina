package cuina.object;

import cuina.util.Rectangle;
import cuina.world.CuinaMask;
import cuina.world.CuinaObject;

import java.util.ArrayList;
import java.util.List;

public class CollisionMask implements CuinaMask
{
	private static final long serialVersionUID = -5578338724857738561L;
	
	/** Kollisions-Fläche relativ zum Objekt. */
	protected final Rectangle bounds = new Rectangle();
	/** Kollisionsfläche auf der Map. */
	protected final Rectangle box = new Rectangle();
	
	private float x;
	private float y;
	
//	private static Rectangle tempBox;
	
	protected CuinaObject object;
	protected CuinaObject impactObject;
//	protected Rectangle impactBounds;
	protected boolean through = false;
	
//	private int tx;
//	private int ty;
//	private boolean useTempOffset;

	

	public CollisionMask(CuinaObject object, Rectangle rect, boolean through)
	{
		this.object = object;
		this.through = through;
		setBounds(rect);
		setPosition((int) object.getX(), (int) object.getY());
	}
	
	public CollisionMask(CuinaObject object, CollisionMask clone)
	{
		this(object, clone.bounds, clone.through);
	}
	
	@Override
	public boolean isThrough()
	{
		return through;
	}

	public void setThrough(boolean through)
	{
		this.through = through;
	}
	
	@Override
	public Rectangle getBounds()
	{
		return this.bounds;
	}
	
	@Override
	public float getX()
	{
		return this.x;
	}

	@Override
	public float getY()
	{
		return this.y;
	}

	public void setPosition(float x, float y)
	{
		this.x = x;
		this.y = y;
		box.setLocation((int) x + bounds.x, (int) y + bounds.y);
	}

	@Override
	public Rectangle getRectangle()
	{
//		box.set(bounds);
//		if (object != null)
//		{
//			if (useTempOffset)
//				box.setLocation(box.x + tx, box.y + ty);
//			else
//				box.setLocation(box.x + (int) object.getX(), box.y + (int) object.getY());
//		}
		return this.box;
	}
	
//	/**
//	 * Verschiebt die Hülle temporär auf die angegebene Position.
//	 * Die Verschiebung muss mit {@link #clearTempOffset()} wieder gelöscht werden.
//	 * @param tx X-Position
//	 * @param ty Y-Position
//	 * @see #clearTempOffset()
//	 */
//	protected void useTempPosition(int tx, int ty)
//	{
//		this.tx = tx;
//		this.ty = ty;
//		this.useTempOffset = true;
//	}
//	
//	/**
//	 * Macht die temporäre Verschiebung rückgängig.
//	 * Ein mehrfacher Aufruf dieser Methode hat keine Wirkung.
//	 * @see #useTempPosition(int, int)
//	 */
//	protected void clearTempOffset()
//	{
//		this.useTempOffset = false;
//	}
	
	public void setBounds(Rectangle rect)
	{
		if (rect == null)
			this.bounds.set(0, 0, 0, 0);
		else
			this.bounds.set(rect);
		this.box.setSize(bounds.width, bounds.height);
	}
	
	/**
	 * Gibt das Objekt der letzten Kollsions zurück.
	 * @return Kollisions-Objekt wenn beim letzten Aufruf von <code>move</code>
	 * eine Kollsions statt gefunden hat, andernfalls <code>null</code>.
	 */
	@Override
	public CuinaObject getImpactObject()
	{
		return impactObject;
	}
	
	@Override
	public boolean move(float x, float y, float z)
	{
		if (!isAbsolutePositionFree(x, y, z))
		{
			if (impactObject == null || !isPassable(impactObject))
			{
				moveToContact(x, y, z);
				return false;
			}
		}
		setPosition(x, y);
		return true;
	}
	
//	private void moveToContact(int x, int y, int z, Rectangle other, Rectangle result)
//	{
//		Rectangle rect = box;
//		float dy = (y - rect.y) / (x - rect.x);
//		if (x == rect.x)
//		{
//			int ly = rect.distY(other);
//			result.x = x;
//			result.y = rect.y + ly;
//		}
//		else if (y == rect.y)
//		{
//			int lx = rect.distX(other);
//			result.x = rect.x + lx;
//			result.y = y;
//		}
//		else
//		{
//			int lx = rect.distX(other);
//			int ly = rect.distY(other);
//			if (lx*dy >= ly)
//			{
//				result.x = rect.x + lx;
//				result.y = (int) (rect.y + lx*dy);
//			}
//			else
//			{
//				result.x = (int) (rect.x + ly/dy);
//				result.y = rect.y + ly;
//			}
//		}
//	}
	
	protected void moveToContact(float x, float y, float z)
	{
		float tx = getX();
		float ty = getY();
		float dx, dy;
		
		if (getX() == x)
		{
			dx = 0;
			dy = (ty < y) ? +1 : -1;
		}
		else
		{
			if (tx < x)
			{
				dx = 1;
				dy = (y - ty) / (x - tx);
			}
			else
			{
				dx = -1;
				dy = (y - ty) / (tx - x);
			}
		}
		float fx = tx;
		float fy = ty;
		do
		{
			fx += dx;
			fy += dy;
		}
		while(isAbsolutePositionFree((int) fx, (int) fy, z));
		setPosition((int) (fx - dx), (int) (fy - dy));
	}
	
	private void moveOut(int x, int y, int z, Rectangle other)
	{

		Rectangle rect = this.box;
		if (x == rect.x)
		{
			rect.x = x;
			if (y > rect.y)
				rect.y = other.y - bounds.height;
			else
				rect.y = other.y + other.height;
		}
		else if (y == rect.y)
		{
			rect.y = y;
			if (x > rect.x)
				rect.x = other.x - bounds.height;
			else
				rect.x = other.x + other.height;
		}
		else
		{
			float dy = (y - rect.y) / (x - rect.x);
			
			int mx, my;
			if (x > rect.x)
				mx = other.x - bounds.height - rect.x;
			else
				mx = other.x + other.height - rect.x;
			
			if (y > rect.y)
				my = other.y - bounds.height - rect.y;
			else
				my = other.y + other.height - rect.y;
			
			if (mx*dy >= my)
			{
				rect.x += mx;
				rect.y += (int) (mx*dy);
			}
			else
			{
				rect.x += (int) (my/dy);
				rect.y += my;
			}
		}
	}

	private CuinaObject testPath(int x, int y)
	{
		Rectangle rect = getRectangle();
		// Steigung
		float dy = (y - rect.y) / (float) (x - rect.x);
		System.out.println("DY: " + dy);

		Rectangle box = new Rectangle(rect.x + x, rect.y + y, rect.width, rect.height);
		box.add(rect);
		for(CuinaObject obj : getObjects(box))
		{
			if (obj == object) continue;
			
			Object ext = obj.getExtension(EXTENSION_KEY);
			if (!(ext instanceof CuinaMask)) continue;
			
			CuinaMask mask = (CuinaMask) ext;
			Rectangle other = mask.getRectangle();
			if (x == rect.x)
			{
				// Sonderfall
				Rectangle r;
				if (y > rect.y)
					r = new Rectangle(rect.x, rect.y, rect.width, rect.height + y);
				else
					r = new Rectangle(rect.x, y, rect.width, rect.y + rect.height);
				
				if (r.intersects(other)) return obj;
			}
			else
			{
				// obere Bewegungslinie
				int x1 = (dy > 0) ? rect.x + rect.width : rect.x;
				int y1 = rect.y;
				// untere Bewegungslinie
				int x2 = (dy > 0) ? rect.x : rect.x + rect.width;
				int y2 = rect.y + rect.width;
				
				int cy1, cy2;
				cy1 = (int) (y1 + (other.x - x1) * dy);
				cy2 = (int) (y2 + (other.x - x2) * dy);
				if (other.y + other.height > cy1 && other.y < cy2) return obj;
				
				cy1 = (int) (y1 + (other.x + other.width - x1) * dy);
				cy2 = (int) (y2 + (other.x + other.width - x2) * dy);	
				if (other.y > cy1 && other.y + other.height < cy2) return obj;
			}
		}
		return null;
	}
	
	private List<CuinaObject> getObjects(Rectangle rect)
	{
		List<CuinaObject> objects = new ArrayList<CuinaObject>(8);
		BaseWorld world = BaseWorld.getInstance();
		for (Integer id : world.getObjectIDs())
		{
			CuinaObject obj = world.getObject(id);
			Object ext = obj.getExtension(EXTENSION_KEY);
			if (ext == this) continue;
			
			if (ext instanceof CuinaMask && (rect == null || rect.intersects(((CuinaMask) ext).getRectangle())))
			{
				objects.add(obj);
			}
		}
		return objects;
	}
	
//	public CuinaObject getObject(Rectangle rect)
//	{
//		BaseWorld world = BaseWorld.getInstance();
//		for (Integer id : world.getObjectIDs())
//		{
//			CuinaObject obj = world.getObject(id);
//			Object ext = obj.getExtension(EXTENSION_KEY);
//			if (ext instanceof CuinaMask && (rect == null || rect.intersects(((CuinaMask) ext).getRectangle())))
//			{
//				return obj;
//			}
//		}
//		return null;
//	}
	
	protected boolean isPassable(CuinaObject other)
	{
		if (other == null) return true;

		CuinaMask otherMask = (CuinaMask) other.getExtension(EXTENSION_KEY);
		return otherMask.isThrough();
	}
	
	@Override
	public boolean isRelativePositionFree(float x, float y, float z)
	{
		return isAbsolutePositionFree(getX() + x, getY() + y, z);
	}
	
	@Override
	public boolean isAbsolutePositionFree(float x, float y, float z)
	{
		this.impactObject = null;
		for (CuinaObject obj : getObjects(null))
		{
			Object ext = obj.getExtension(EXTENSION_KEY);
			if (ext instanceof CuinaMask)
			{
				CuinaMask mask = (CuinaMask) ext;
				if (intersectsOn(x, y, z, mask))
				{
					this.impactObject = obj;
					return isPassable(obj);
				}
			}
		}
		return true;
	}

	@Override
	public boolean intersects(CuinaMask other)
	{
		if (other == null) return false;

		return box.intersects(other.getRectangle());
	}

	@Override
	public boolean intersectsOn(float x, float y, float z, CuinaMask other)
	{
		if (other == null) return false;
		
		float tx = this.x;
		float ty = this.y;
		try
		{
			setPosition(x, y);
			return intersects(other);
		}
		finally
		{
			setPosition(tx, ty);
		}
	}
	
	@Override
	public String toString()
	{
		return box.toString();
	}
}