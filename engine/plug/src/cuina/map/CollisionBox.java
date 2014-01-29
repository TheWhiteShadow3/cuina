package cuina.map;

import cuina.animation.Model;
import cuina.object.CollisionMask;
import cuina.util.Rectangle;
import cuina.world.CuinaMask;
import cuina.world.CuinaModel;
import cuina.world.CuinaObject;

import java.awt.image.BufferedImage;

/**
 * Die Kollisions-Maske für ein MapObjekt dient zur erkennung von Kollisionen auf der Spielkarte.
 * @author TheWhiteShadow
 * @version 1.0
 */
public class CollisionBox extends CollisionMask
{
	private static final long	serialVersionUID	= -878552828964907423L;
	
	/** Maskengröße für Personen. (Voreingestellte Größe auf 20x16) */
	public static final int MASK_CHARACTER = 1;
	/** Maskengröße passt genau auf einem Feld. */
	public static final int MASK_TILE = 2;
	/** Maskengröße passt genau auf 1/4 Feld. */
	public static final int MASK_HALF_TILE = 3;
	
	private boolean[][] pixelData;

	public CollisionBox(CuinaObject object, Rectangle rect, boolean through, int alphaLevel)
	{
		super(object, rect, through);
		if (alphaLevel > 0)
			createPixelData(alphaLevel);
	}
	
	public CollisionBox(CuinaObject object, CollisionBox clone)
	{
		super(object, clone.bounds, clone.through);
		// Für Pixeldaten reicht die Referenz.
		this.pixelData = clone.pixelData;
	}
	
	public CollisionBox(CuinaObject object, BoxData cMask)
	{
		this(object, new Rectangle(cMask.x, cMask.y, cMask.width, cMask.height), cMask.through, cMask.alphaMask);
	}
	
	private void createPixelData(int alphaLevel)
	{
		Object ext = object.getExtension(CuinaModel.EXTENSION_KEY);
		if (ext == null || !(ext instanceof Model)) return;
		Model model = (Model) ext;
		
		BufferedImage image = model.getRawImage();
		
		int minX = Math.max((int) model.getOX() + bounds.x, 0);
		int minY = Math.max((int) model.getOY() + bounds.y, 0);
		
		pixelData = new boolean[bounds.width][bounds.height];
		int[] pixel = new int[4];
		for(int x = 0; x < bounds.width; x++)
		for(int y = 0; y < bounds.height; y++)
		{
			image.getRaster().getPixel(minX + x, minY + y, pixel);
			if (pixel[3] >= alphaLevel)
			{
				pixelData[x][y] = true;
			}
		}
	}

	public boolean[][] getPixelData()
	{
		return pixelData;
	}
	
	/**
	 * Gibt an, ob die angegebene Position Kollisions-Frei ist.
	 * @param x X-Position in Pixel.
	 * @param y Y-Position in Pixel.
	 * @return <code>true</code>, wenn die angegebene Position frei ist, andernfalls <code>false</code>.
	 */
	public boolean isFree(int x, int y)
	{
		useTempPosition(x, y);
		
		GameMap map = GameMap.getInstance();
		if (map.isPassable(getRectangle()))
		{
			System.out.println("isFree");
			if (map.getCollisionSystem().testCollision(this) == null)
			{
				clearTempOffset();
				return true;
			}
		}
		clearTempOffset();
		return false;
	}

	@Override
	public boolean move(float x, float y, float z, boolean useTrigger)
	{
		useTempPosition((int) x, (int) y);
		impactObject = null;
		Rectangle rect = getRectangle();
		
		GameMap map = GameMap.getInstance();
		if (!through && !map.isPassable(rect))
		{
			clearTempOffset();
			return false;
		}
		else
		{
			impactObject = map.getCollisionSystem().testCollision(this);
			if (impactObject != null)
			{
				CollisionBox targetBox = (CollisionBox) impactObject.getExtension(EXTENSION_KEY);
				if (useTrigger)
				{
					impactObject.testTriggers(GameMap.TOUCHED_BY_OBJECT, object.getID(), impactObject, object);
					object.testTriggers(GameMap.OBJECT_TOUCH, impactObject.getID(), object, impactObject);
				}
				if (!through && !targetBox.isThrough())
				{
					clearTempOffset();
					return false;
				}
			}
		}
		map.getCollisionSystem().updatePosition(object);
		clearTempOffset();
		return true;
	}
	
	@Override
	public CuinaObject testAbsolutePosition(int x, int y, int z)
	{
		useTempPosition(x, y);
		
		System.out.println("[CollisionBox] testPosition");
		CuinaObject cObject = GameMap.getInstance().getCollisionSystem().testCollision(this);

		clearTempOffset();
		return cObject;
	}
	
	@Override
	public boolean intersects(CuinaMask other)
	{
		if (other == null) return false;
		Rectangle intersects = getRectangle().intersection(other.getRectangle());
		if (!intersects.isEmpty())
		{
			return testPixelCollision(intersects, other);
		}
		return false;
	}
	
	private boolean testPixelCollision(Rectangle intersection, CuinaMask other)
	{
		if (!(other instanceof CollisionBox)) return true;
		
		boolean[][] otherData = ((CollisionBox) other).getPixelData();
		if (otherData == null) return true;
		
		intersection.x -=  this.box.x;
		intersection.y -=  this.box.y;
		int ox = other.getRectangle().x - this.box.x;
		int oy = other.getRectangle().y - this.box.y;
		int maxX = intersection.x + intersection.width;
		int maxY = intersection.y + intersection.height;
		
		for(int x = intersection.x; x < maxX; x++)
		for(int y = intersection.y; y < maxY; y++)
		{
			if ((pixelData == null || pixelData[x][y]) &&
				(otherData[x-ox][y-oy])) return true;
		}
		return false;
	}

	public boolean intersects(Rectangle rect)
	{
		return getRectangle().intersects(rect);
	}
	
	/**
	 * Testet ob die Position zu einer Kollision mit der Karte führt.
	 * @return true, wenn das Objekt auf der angegebenen Position kollidiert, andernfalls false.
	 */
	public boolean testTileMap()
	{	// negiert, da auf Kollision geprüft wird
		return !GameMap.getInstance().isPassable(getRectangle());
	}
	
	public void refresh()
	{
		GameMap.getInstance().getCollisionSystem().updatePosition(object);
	}
	
	@Override
	public String toString()
	{
		return box.toString();
	}
}
