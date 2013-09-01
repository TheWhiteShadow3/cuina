package cuina.map;

import cuina.animation.ModelIF;
import cuina.data.Area;
import cuina.world.CuinaMask;
import cuina.world.CuinaModel;
import cuina.world.CuinaObject;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * Die Kollisions-Maske für ein MapObjekt dient zur erkennung von Kollisionen auf der Spielkarte.
 * @author TheWhiteShadow
 * @version 1.0
 */
public class CollisionBox implements CuinaMask
{
	private static final long	serialVersionUID	= -878552828964907423L;
	
	/** Maskengröße für Personen. (Voreingestellte Größe auf 20x16) */
	public static final int MASK_CHARACTER = 1;
	/** Maskengröße passt genau auf einem Feld. */
	public static final int MASK_TILE = 2;
	/** Maskengröße passt genau auf 1/4 Feld. */
	public static final int MASK_HALF_TILE = 3;
	
	/** Kollisions-Fläche relativ zum Objekt. */
	private Rectangle bounds = null;
	private boolean[][] pixelData;
	/** Kollisionsfläche auf der Map. */
	private final Rectangle box = new Rectangle();
	private CuinaObject object;
	private CuinaObject impactObject = null;
	private boolean through = false;
	
	private boolean useTempPosition;
	private int ox;
	private int oy;

	public CollisionBox(CuinaObject object, Rectangle rect, int alphaLevel, boolean through)
	{
		this.object = object;
		setBounds(rect);
		this.through = through;
		if (alphaLevel > 0)
			createPixelData(alphaLevel);
	}
	
	public CollisionBox(CuinaObject object, CollisionBox clone)
	{
		this.object = object;
		setBounds(clone.bounds);
		this.through = clone.through;
		pixelData = clone.pixelData;
	}
	
	public CollisionBox(CuinaObject object, BoxData cMask)
	{
		this.object = object;
		this.through = cMask.through;
		setBounds(cMask);
//		//XXX: Zum Testen, bis der Editor es einstellen kann.
//		cMask.alphaMask = 1;
		if (cMask.alphaMask > 0) 
			createPixelData(cMask.alphaMask);
	}
	
	private void createPixelData(int alphaLevel)
	{
		Object ext = object.getExtension(CuinaModel.EXTENSION_KEY);
		if (ext == null || !(ext instanceof ModelIF)) return;
		ModelIF model = (ModelIF) ext;
		
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
	
//	public CollisionMask(MapObject object, int type)
//	{
//		this.object = object;
//		switch(type)
//		{
//			case MASK_CHARACTER:
//					// TODO: Größe sollte entsprechend dem benutzen Tileraster und Characterformat angepasst werden.
//					// Daher empfielt es sich die Masken eher als Datenbank zu verwalten, anstatt als Konstanten.
//					setBounds(new Rectangle(4, 16, 24, 16));
//					break;
//			case MASK_TILE:
//					int tileSize = GameMap.getInstance().getTileSize();
//					setBounds(new Rectangle(0, 0, tileSize, tileSize));
//					break;
//			case MASK_HALF_TILE:
//					int halfTileSize = GameMap.getInstance().getTileSize() / 2;
//					setBounds(new Rectangle(halfTileSize / 2, halfTileSize / 2, halfTileSize, halfTileSize));
//					break;
//		}
//	}
	
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
		return this.bounds.getBounds();
	}

	@Override
	public Rectangle getBox()
	{
		if (useTempPosition)
			box.setBounds(bounds.x + ox, bounds.y + oy, bounds.width, bounds.height);
		else
		{
			box.setBounds(bounds);
			if (object != null)
			{
				box.setLocation(box.x + (int)object.getX(), box.y + (int)object.getY());
			}
		}
		return this.box;
	}
	
	@Override
	public boolean[][] getPixelData()
	{
		return pixelData;
	}
	
	private void setTempPosition(int ox, int oy)
	{
		this.useTempPosition = true;
		this.ox = ox;
		this.oy = oy;
	}
	
	private void clearTempPosition()
	{
		this.useTempPosition = false;
	}
	
	public void setBounds(Rectangle rect)
	{
		if (rect == null)
			this.bounds = new Rectangle();
		else
			this.bounds = new Rectangle(rect);
	}
	
	/**
	 * Gibt das Objekt der letzten Kollsions zurück.
	 * @return Kollisions-Objekt wenn beim letzten Aufruf von <code>testObjects</code>
	 * eine Kollsions statt gefunden hat, andernfalls null.
	 */
	public CuinaObject getImpactObject()
	{
		return impactObject;
	}
	
	/**
	 * Gibt an, ob die angegebene Position Kollisions-Frei ist.
	 * @param x X-Position in Pixel.
	 * @param y Y-Position in Pixel.
	 * @return <code>true</code>, wenn die angegebene Position frei ist, andernfalls <code>false</code>.
	 */
	public boolean isFree(int x, int y)
	{
		setTempPosition(x, y);
		Rectangle rect = getBox();
		GameMap map = GameMap.getInstance();
		if (map.isPassable(rect))
		{
			System.out.println("isFree");
			if (map.getCollisionSystem().testCollision(object) == null)
			{
				clearTempPosition();
				return true;
			}
		}
		clearTempPosition();
		return false;
	}

//	/**
//	 * Versucht die Kollisions-Maske zu bewegen. Gelingt dies nicht werden die Trigger der
//	 * zusammengestoßenen Objekte ausgelöst.
//	 * @param x X-Position in Pixel.
//	 * @param y X-Position in Pixel.
//	 * @return true, wenn das Objekt auf der angegebenen Position beweg wurde, andernfalls false (bei Kollision).
//	 */
//	@Override
//	public boolean move(float x, float y, boolean useTrigger)
//	{
//		impactObject = null;
//		setTempPosition((int)x, (int)y);
//		Rectangle rect = getBox();
//		
//		GameMap map = GameMap.getInstance();
//		if (!through && !map.isPassable(rect))
//		{
//			clearTempPosition();
//			return false;
//		}
//		else
//		{
//			impactObject = map.getCollisionSystem().testCollision(object);
//			CollisionBox targetBox = (CollisionBox) impactObject.getExtension(EXTENSION_KEY);
//			if (useTrigger && impactObject != null)
//			{
//				impactObject.testTriggers(GameMap.TOUCHED_BY_OBJECT, object.getID(), impactObject, object);
//				object.testTriggers(GameMap.OBJECT_TOUCH, impactObject.getID(), object, impactObject);
//				
//				if (!through && !targetBox.isThrough())
//				{
//					clearTempPosition();
//					return false;
//				}
//			}
////				HashMap<Integer, MapObject> areas = GameMap.getInstance().getAreas();
////				for(Integer key : areas.keySet())
////				{
////					if (testObject(areas.get(key)))
////					{
////						object.testTriggers(TriggerType.ENTERS_AREA, key);
////					}
////				}
//		}
//		map.getCollisionSystem().updatePosition(object);
//		clearTempPosition();
//		return true;
//	}
	
	@Override
	public boolean move(float dx, float dy, boolean useTrigger)
	{
		impactObject = null;
		setTempPosition((int) (object.getX() + dx), (int) (object.getY() + dy));
		Rectangle rect = getBox();
		
		GameMap map = GameMap.getInstance();
		if (!through && !map.isPassable(rect))
		{
			clearTempPosition();
			return false;
		}
		else
		{
			impactObject = map.getCollisionSystem().testCollision(object);
			CollisionBox targetBox = (CollisionBox) impactObject.getExtension(EXTENSION_KEY);
			moveToRectangle(targetBox.getBox(), dx, dy);
			if (useTrigger && impactObject != null)
			{
				impactObject.testTriggers(GameMap.TOUCHED_BY_OBJECT, object.getID(), impactObject, object);
				object.testTriggers(GameMap.OBJECT_TOUCH, impactObject.getID(), object, impactObject);
				
				if (!through && !targetBox.isThrough())
				{
					clearTempPosition();
					return false;
				}
			}
//				HashMap<Integer, MapObject> areas = GameMap.getInstance().getAreas();
//				for(Integer key : areas.keySet())
//				{
//					if (testObject(areas.get(key)))
//					{
//						object.testTriggers(TriggerType.ENTERS_AREA, key);
//					}
//				}
		}
		map.getCollisionSystem().updatePosition(object);
		clearTempPosition();
		return true;
	}
	
	private void moveToRectangle(Rectangle target, float dx, float dy)
	{
		int newX = (int) (object.getX() + dx);
		int newY = (int) (object.getY() + dy);
		if (dx > 0)
		{
			float dif = target.x - (box.x + box.width);
			if (dif < 0) newX = target.x - bounds.width;
		}
		else if (dx < 0)
		{
			float dif = box.x - (target.x + target.width);
			if (dif < 0) newX = target.x + target.width - bounds.x;
		}
		if (dy > 0)
		{
			float dif = target.y - (box.y + box.height);
			if (dif < 0) newY = target.y - bounds.height;
		}
		else if (dy < 0)
		{
			float dif = box.y - (target.y + target.height);
			if (dif < 0) newY = target.y + target.height - bounds.y;
		}
		object.setX(newX);
		object.setY(newY);
	}
	
	public CuinaObject testRelativePosition(int x, int y)
	{
		return testPosition((int) object.getX() + x, (int) object.getY() + y);
	}
	
	public CuinaObject testPosition(int x, int y)
	{
		setTempPosition(x, y);
		
		System.out.println("[CollisionBox] testPosition");
		CuinaObject cObject = GameMap.getInstance().getCollisionSystem().testCollision(object);
		
		clearTempPosition();
		return cObject;
	}
	
	@Override
	public boolean intersects(CuinaMask other)
	{
		if (other == null) return false;
		Rectangle intersects = getBox().intersection(other.getBox());
		if (!intersects.isEmpty())
		{
			return isPixelCollision(intersects, other);
		}
		return false;
	}
	
	private boolean isPixelCollision(Rectangle intersection, CuinaMask other)
	{
		boolean[][] otherData = other.getPixelData();
		
		intersection.x -=  this.box.x;
		intersection.y -=  this.box.y;
		int ox = other.getBox().x - this.box.x;
		int oy = other.getBox().y - this.box.y;
		int maxX = intersection.x + intersection.width;
		int maxY = intersection.y + intersection.height;
		
		for(int x = intersection.x; x < maxX; x++)
		for(int y = intersection.y; y < maxY; y++)
		{
			if ((pixelData == null || pixelData[x][y]) &&
				(otherData == null || otherData[x-ox][y-oy])) return true;
		}
		return false;
	}
	
//	public boolean testObject(MapObject otherObj, int dist)
//	{
//		if (otherObj == null || otherObj.getCollisionMask() == null) return false;
//		// erstelle Kopie der Kollisionshülle zum bearbeiten
//		Rectangle other_rect = (Rectangle)otherObj.getCollisionMask().rect.clone();
//		// erweitere Rechteck um dist in alle Richtungen
//		other_rect.setBounds(other_rect.x - dist,
//							 other_rect.y - dist,
//							 other_rect.width + dist * 2,
//							 other_rect.height + dist * 2);
//		if(rect.intersects(other_rect))
//		{
//			return true;
//		}
//		else
//			return false;
//	}
	
	public boolean inArea(Area area)
	{
		return area.intersects(getBox());
	}
	
	/**
	 * Testet ob die Position zu einer Kollision mit der Karte führt.
	 * @return true, wenn das Objekt auf der angegebenen Position kollidiert, andernfalls false.
	 */
	public boolean testTileMap()
	{	// negiert, da auf Kollision geprüft wird
		return !GameMap.getInstance().isPassable(getBox());
	}
	
	public void refresh()
	{
		clearTempPosition();
		GameMap.getInstance().getCollisionSystem().updatePosition(object);
	}
	
	@Override
	public String toString()
	{
		return box.toString();
	}
}
