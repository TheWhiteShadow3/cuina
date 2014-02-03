package cuina.object;

import cuina.util.Rectangle;
import cuina.world.CuinaMask;
import cuina.world.CuinaObject;

public class CollisionMask implements CuinaMask
{
	private static final long serialVersionUID = -5578338724857738561L;
	
	/** Kollisions-Fläche relativ zum Objekt. */
	protected final Rectangle bounds = new Rectangle();
	/** Kollisionsfläche auf der Map. */
	protected final Rectangle box = new Rectangle();
	
	protected CuinaObject object;
	protected CuinaObject impactObject = null;
	protected boolean through = false;
	
	private int tx;
	private int ty;
	private boolean useTempOffset;

	public CollisionMask(CuinaObject object, Rectangle rect, boolean through)
	{
		this.object = object;
		setBounds(rect);
		this.through = through;
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

	@Override
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
	public Rectangle getRectangle()
	{
		box.set(bounds);
		if (object != null)
		{
			if (useTempOffset)
				box.setLocation(box.x + tx, box.y + ty);
			else
				box.setLocation(box.x + (int) object.getX(), box.y + (int) object.getY());
		}
		return this.box;
	}
	
	/**
	 * Verschiebt die Hülle temporär auf die angegebene Position.
	 * Die Verschiebung muss mit {@link #clearTempOffset()} wieder gelöscht werden.
	 * @param tx X-Position
	 * @param ty Y-Position
	 * @see #clearTempOffset()
	 */
	protected void useTempPosition(int tx, int ty)
	{
		this.tx = tx;
		this.ty = ty;
		this.useTempOffset = true;
	}
	
	/**
	 * Macht die temporäre Verschiebung rückgängig.
	 * Ein mehrfacher Aufruf dieser Methode hat keine Wirkung.
	 * @see #useTempPosition(int, int)
	 */
	protected void clearTempOffset()
	{
		this.useTempOffset = false;
	}
	
	public void setBounds(Rectangle rect)
	{
		if (rect == null)
			this.bounds.set(0, 0, 0, 0);
		else
			this.bounds.set(rect);
	}
	
	/**
	 * Gibt das Objekt der letzten Kollsions zurück.
	 * @return Kollisions-Objekt wenn beim letzten Aufruf von <code>move</code>
	 * eine Kollsions statt gefunden hat, andernfalls null.
	 */
	public CuinaObject getImpactObject()
	{
		return impactObject;
	}
	
	@Override
	public boolean move(float x, float y, float z, boolean useTrigger)
	{
		return !testObject(testAbsolutePosition((int) x, (int) y, (int) z), useTrigger);
	}
	
	public boolean testObject(CuinaObject other, boolean useTrigger)
	{
		if (other == null) return false;
		
		this.impactObject = other;
		CuinaMask otherMask = (CuinaMask) impactObject.getExtension(EXTENSION_KEY);
		
		if (useTrigger)
		{
			impactObject.testTriggers(BaseWorld.TOUCHED_BY_OBJECT, object.getID(), impactObject, object);
			object.testTriggers(BaseWorld.OBJECT_TOUCH, impactObject.getID(), object, impactObject);
		}
		return !(through || otherMask.isThrough());
	}
	
	@Override
	public CuinaObject testRelativePosition(int x, int y, int z)
	{
		return testAbsolutePosition((int) object.getX() + x, (int) object.getY() + y, (int) object.getZ() + z);
	}
	
	@Override
	public CuinaObject testAbsolutePosition(int x, int y, int z)
	{
		useTempPosition(x, y);
		
		BaseWorld world = BaseWorld.getInstance();
		for (Integer id : world.getObjectIDs())
		{
			CuinaObject obj = world.getObject(id);
			Object ext = obj.getExtension(EXTENSION_KEY);
			if (ext instanceof CuinaMask && intersects((CuinaMask) ext))
			{
				clearTempOffset();
				return obj;
			}
		}
		clearTempOffset();
		return null;
	}
	
	@Override
	public boolean intersects(CuinaMask other)
	{
		if (other == null) return false;

		return getRectangle().intersects(other.getRectangle());
	}
	
	@Override
	public String toString()
	{
		return box.toString();
	}
}