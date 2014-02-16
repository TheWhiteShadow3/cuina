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
	public CuinaObject getObjectOn(float x, float y)
	{
		return GameMap.getInstance().getCollisionSystem().testCollision((int) x, (int) y, this);
	}
	
	@Override
	public boolean isAbsolutePositionFree(float x, float y, float z)
	{
		impactObject = null;
		Rectangle rect = new Rectangle(box);
		rect.x = (int) x + bounds.x;
		rect.y = (int) y + bounds.y;
		
		GameMap map = GameMap.getInstance();
		if (through)
		{
			return map.contains(rect);
		}
		
		if (!map.isPassable(rect))
		{
			return false;
		}
		
		CuinaObject obj = getObjectOn((int) x, (int) y);
		impactObject = obj;
		return isPassable(obj);
	}
	
	@Override
	public boolean intersects(CuinaMask other)
	{
		if (other == null) return false;
		Rectangle intersects = box.intersection(other.getRectangle());
		if (!intersects.isEmpty())
		{
			return testPixelCollision(intersects, other);
		}
		return false;
	}

	private boolean testPixelCollision(Rectangle intersection, CuinaMask other)
	{
		if (!(other instanceof CollisionBox) || pixelData == null) return true;
		
		boolean[][] otherData = ((CollisionBox) other).getPixelData();
		if (otherData == null) return true;
		
		intersection.x -= this.box.x;
		intersection.y -= this.box.y;
		int ox = other.getRectangle().x - this.box.x;
		int oy = other.getRectangle().y - this.box.y;
		int maxX = intersection.x + intersection.width;
		int maxY = intersection.y + intersection.height;
		
		for(int x = intersection.x; x < maxX; x++)
		for(int y = intersection.y; y < maxY; y++)
		{
			if (pixelData[x][y] && otherData[x-ox][y-oy]) return true;
		}
		return false;
	}

	public boolean intersects(Rectangle rect)
	{
		return box.intersects(rect);
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
		GameMap.getInstance().getCollisionSystem().updateCollisionData(object);
	}
	
	@Override
	public String toString()
	{
		return box.toString();
	}
}
