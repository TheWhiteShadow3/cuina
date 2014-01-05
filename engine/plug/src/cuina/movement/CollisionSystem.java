package cuina.movement;

import cuina.Logger;
import cuina.world.CuinaMask;
import cuina.world.CuinaObject;
import cuina.world.CuinaWorld;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class CollisionSystem implements Serializable
{
	private static final long	serialVersionUID	= 6854687512867404789L;

	public static final int AREA_CAPACITY = 16;
	
	public static final int NONE = 0;
	public static final int KILL_OUTSIDE_ROOM = 1;
	
	private static int objectPolicy = KILL_OUTSIDE_ROOM;
	
	private CollisionArea[] areas;
	private int areaWidth;
	private int areaHeight;
	private int xCount;
	
	static private class CollisionArea extends Rectangle
	{
		private static final long serialVersionUID = -7810887030018292181L;
		
		HashMap<Integer, CuinaObject> objects = new HashMap<Integer, CuinaObject>();

		public CollisionArea(Rectangle r)
		{
			super(r);
		}

		public CuinaObject addObject(CuinaObject object)
		{
			return objects.put(object.getID(), object);
		}
		
		public CuinaObject removeObject(CuinaObject object)
		{
			return objects.remove(object.getID());
		}
	}
	
	private CollisionSystem() {}
	
	public static CollisionSystem newInstance(CuinaWorld map)
	{
		CollisionSystem cs = new CollisionSystem();
		cs.init(map);
		return cs;
	}
	
	public static CollisionSystem newInstance(CuinaWorld map, int areaCount)
	{
		CollisionSystem cs = new CollisionSystem();
		cs.init(map, areaCount);
		return cs;
	}
	
	public void destroy()
	{
		this.areas = null;
	}
	
	private void init(CuinaWorld map)
	{
		int areaCount = Math.max(1, map.getObjectCount() / AREA_CAPACITY);
		
		init(map, areaCount);
	}
	
	private void init(CuinaWorld map, int areaCount)
	{
//		HashMap<Integer, MapObject> objects = map.getObjects();
		
//		System.out.println("Objektanzahl: " + objects.size());
//		System.out.println("Breite: " + map.getWidth());
//		System.out.println("Höhe: " + map.getHeight());
//		System.out.println("Benötigte Bereiche für " + AREA_CAPACITY + " Objete pro Bereich: " + areaCount);
		// berechne Aufteilung der Karte
		xCount = (int) Math.round( Math.sqrt(areaCount / (map.getHeight() / (float)map.getWidth() )) );
		int yCount = (int) Math.round( Math.sqrt(areaCount / (map.getWidth()  / (float)map.getHeight())) );
		xCount = Math.max(xCount, 1);
		yCount = Math.max(yCount, 1);
		
		Logger.log(CollisionSystem.class, Logger.DEBUG, "Kartenaufteilung (w/h): " +
					xCount + " / " + yCount + " Gesamt: " + (xCount * yCount) + " Bereiche.");
		Logger.log(CollisionSystem.class, Logger.DEBUG,
				"Abweichung der Bereichs-Aufteilung: " + (areaCount / (float)(xCount * yCount)) );
		
		areas = new CollisionArea[xCount * yCount];
		areaWidth = (map.getWidth() + xCount - 1) / xCount;
		areaHeight = (map.getHeight() + yCount - 1) / yCount;
		for(int y = 0; y < yCount; y++)
		{
			for(int x = 0; x < xCount; x++)
			{
				Rectangle rect = new Rectangle(areaWidth * x, areaHeight * y, areaWidth, areaHeight);
//				System.out.println("Erstelle Bereich: " + rect);
				areas[x + xCount * y] = new CollisionArea(rect);
			}
		}
		
//		int count = 0;
//		for(Integer i : objects.keySet())
//		{
//			MapObject obj = objects.get(i);
//			if (obj.getCollisionMask() == null) continue;
//			
//			for(int j = 0; j < instance.areas.length; j++)
//			{
//				if (obj.getCollisionMask().getRect().intersects(areas[j]))
//				{
//					instance.areas[j].addObject(obj);
//					
////					count++;
//				}
//			}
//		}
//		System.out.println("Gesamt-Anzahl an Objekten: " + count);
//		System.out.println("Überlappungen: " + (count - objects.size()));
//		
//		for(int j = 0; j < areas.length; j++)
//		{
//			System.out.println("Objekte in Bereich " + j + ": " + areas[j].objects.size());
//		}
		Logger.log(CollisionSystem.class, Logger.DEBUG, "Kollisions-System initialisiert.");
	}
	
	public CuinaObject testCollision(CuinaObject object)
	{
//		System.out.println("[CS] Aufruf (testCollision) durch: " + CollisionSystem.class.getClassLoader());
		CuinaMask box = getBoxFrom(object);
		if (box == null) return null;
		// finde relevante Bereiche
		for(CollisionArea area : getAreas( box.getBox()) )
		{	// teste alle Objekte innerhalb dieses Bereichs
			CuinaObject otherObj;
			for(Integer id : area.objects.keySet())
			{
				otherObj = area.objects.get(id);
				
				if ( intersectsMask(object, otherObj) )
				{
//					System.out.println("Kollision: " + object.getID() + " und " + otherObj.getID());
					return otherObj;
				}
			}
		}
		return null;
	}
	
	private static boolean intersectsMask(CuinaObject o1, CuinaObject o2)
	{
		if (o1 == o2) return false;
		CuinaMask box1 = getBoxFrom(o1);
		CuinaMask box2 = getBoxFrom(o2);
		
		if (box1 == null || box2 == null) return false;
		return (box1.intersects(box2));
	}
	
	public void removeObject(CuinaObject object)
	{
		for(int i = 0; i < areas.length; i++)
		{
			areas[i].removeObject(object);
		}
	}
	
	public void updatePosition(CuinaObject object)
	{
		CuinaMask box = getBoxFrom(object);
		if (box == null) return;
		
		Rectangle rect = box.getBox();
		if (rect.isEmpty()) return;
		boolean saved = false; // zum nachgucken ob es Lücken in der Bereichs-Abdeckung gibt
		
		// finde relevante Bereiche
		for(int j = 0; j < areas.length; j++)
		{
			if (rect.intersects(areas[j]))
			{
				if (areas[j].addObject(object) == null)
				{
//					System.out.println("Bereich " + j + " vergrößert zu: " + areas[j].objects.size());
				}
				saved = true;
			}
			else
			{
				if (areas[j].removeObject(object) != null)
				{
//					System.out.println("Bereich " + j + " verkleinert zu: " + areas[j].objects.size());
				}
			}
		}
		if (saved == false)
		{
			if ((objectPolicy & KILL_OUTSIDE_ROOM) != 0)
			{
//				Game.getWorld().removeObject(object);
			}
			else
				Logger.log(CollisionSystem.class, Logger.WARNING, "Objekt ohne Kollisions-Region. ID = " + object.getID());
		}
	}
	
	public CuinaObject[] findObjects(CollisionArea a)
	{
		if (a  == null) return null;
		HashSet<CuinaObject> founds = new HashSet<CuinaObject>();
		// finde relevante Bereiche
		for(CollisionArea area : getAreas(a))
		{
			if (a.intersects(area))
			{	// teste alle Objekte innerhalb dieses Bereichs
				CuinaObject otherObj;
				for(Integer id : area.objects.keySet())
				{
					otherObj = area.objects.get(id);
					CuinaMask box = getBoxFrom(otherObj);
					if (box == null) continue;
					
					if (box.getBox().contains(a))
					{
						founds.add(otherObj);
					}
				}
			}
			else
			{
				Logger.log(CollisionSystem.class, Logger.WARNING, "Fehlerhaftes Ergebnis von CollisionSystem.getAreas()");
			}
		}
		return founds.toArray(new CuinaObject[founds.size()]);
	}
	
	public int areaCount()
	{
		return areas.length;
	}
	
	public CollisionArea getArea(int index)
	{
		return areas[index];
	}
	
	public CollisionArea getArea(int x, int y)
	{
		return areas[x / areaWidth * xCount + y / areaHeight];
	}
	
	public CollisionArea[] getAreas(Rectangle rect)
	{
		int x1 =  rect.x / areaWidth;
		int x2 = (rect.x + rect.width) / areaWidth;
		int y1 =  rect.y / areaHeight;
		int y2 = (rect.y + rect.height) / areaHeight;
		
		if (x1 < 0 || y1 < 0 || x2 >= xCount || y2 >= areas.length / xCount)
		{
			return new CollisionArea[0];
		}
	
//		int temp;
//		if (x1 > x2)
//		{
//			temp = x1;
//			x1 = x2;
//			x2 = temp;
//		}
//		if (y1 > x2)
//		{
//			temp = y1;
//			y1 = y2;
//			y2 = temp;
//		}
		CollisionArea[] subAreas = new CollisionArea[(x2-x1 + 1) * (y2-y1 + 1)];
		
		int temp = 0;
		for(int y = y1; y <= y2; y++)
		{
			for(int x = x1; x <= x2; x++)
			{
				subAreas[temp++] = areas[x + y * xCount];
			}
		}
		return subAreas;
	}
	
	public boolean trace(int x1, int y1, int x2, int y2)
	{
		int x, y, width, height;
		if (x2 > x1) { x = x1; width  = x2 - x1; }
		else 		 { x = x2; width  = x1 - x2; }
		if (y2 > y1) { y = y1; height = y2 - y1; }
		else 		 { y = y2; height = y1 - x2; }
		
		Rectangle rect = new Rectangle(x, y, width, height);
		for (CollisionArea area : getAreas(rect))
		{
			CuinaObject[] objects = findObjects(area);
			for (CuinaObject obj : objects)
			{
				CuinaMask box = getBoxFrom(obj);
				if (box == null) continue;
				rect = box.getBox();
				
				if (rect.intersectsLine(x1, y1, x2, y2))
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean scan(int x1, int y1, int x2, int y2, boolean useTrace)
	{
		/* TODO: Implementation fehlt (Parameter müssen noch angepasst werden.)
		 * Ziel der Methode ist es, einen Kegelförmigen Bereich nach Objekten zu scannen.
		 * Damit soll u.a. ein Sichtskript möglich werden.
		 */
		
		return true;
	}
	
//	private void readObject(ObjectInputStream s) throws IOException
//	{
//		try
//		{
//			s.defaultReadObject();
//			instance = this;
//		}
//		catch (ClassNotFoundException e) { /* kommt eigentlich nicht vor */ }
//	}
	
	private static CuinaMask getBoxFrom(CuinaObject obj)
	{
		return (CuinaMask) obj.getExtension(CuinaMask.EXTENSION_KEY);
	}
}
