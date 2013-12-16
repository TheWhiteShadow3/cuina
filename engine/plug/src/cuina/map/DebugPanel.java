package cuina.map;

import cuina.graphics.Graphics;
import cuina.graphics.Image;
import cuina.graphics.Images;
import cuina.graphics.Sprite;
import cuina.world.CuinaObject;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Iterator;

import org.lwjgl.util.Color;


public class DebugPanel
{
	private static DebugPanel instance;
	
	private GameMap map;
	private Sprite[][] mapSprites;
	private final HashMap<Integer, Sprite> objectSprites = new HashMap<Integer, Sprite>();
	private final HashMap<Integer, Sprite> mapAreaSprites = new HashMap<Integer, Sprite>();
	private Sprite[] CSAreaSprites;
	
	private static int tileSize;
	private static Image cMaskImage;

	public static void initDebugPanel()
	{
		instance = new DebugPanel();
	}
	
	public static void update()
	{
		if (instance == null) return;
		
		instance.update0();
	}
	
	public static void dispose()
	{
		if (instance == null) return;
		
		instance.dispose0();
	}

	private DebugPanel()
	{
		map = GameMap.getInstance();
		tileSize = map.getTileSize();
		
		createCollisionMap();
		createCollisionMask();
		createCollisionAreas();
		createMapAreas();
		
		update0();
	}
	
	private void createMapAreas()
	{
		for(Integer key : map.getAreas().keySet())
		{
			createMapAreaSprite(map.getArea(key));
		}
	}
	
	private void createCollisionAreas()
	{
		CSAreaSprites = new Sprite[map.getCollisionSystem().areaCount()];
		
		for(int i = 0; i < CSAreaSprites.length; i++)
		{
			createCollisionAreaSprite(i);
		}
	}
	
	private void createMapAreaSprite(CuinaObject area)
	{
		CollisionBox box = (CollisionBox) area.getExtension("Box");
		if (box == null) return;
		
		Rectangle bounds = box.getBounds();
		Sprite sprite = new DebugSprite(bounds.width, bounds.height, new Color(255, 255, 0, 128), true);
		sprite.setOX(-area.getX());
		sprite.setOY(-area.getY());
//		sprite.setX(area.getX() - map.getScrollX());
//		sprite.setX(area.getY() - map.getScrollY());
		sprite.setX(area.getX());
		sprite.setX(area.getY());
		sprite.setDepth(999);
		mapAreaSprites.put(area.getID(), sprite);
	}
	
	private void createCollisionAreaSprite(int index)
	{
		Rectangle rect = map.getCollisionSystem().getArea(index);
		Sprite sprite = new DebugSprite(rect.width, rect.height, new Color(255, 0, 0), false);
		sprite.setOX(-rect.x);
		sprite.setOY(-rect.y);
//		sprite.setX(-map.getScrollX());
//		sprite.setX(-map.getScrollY());
		sprite.setDepth(1000);
		CSAreaSprites[index] = sprite;
	}

	private static Image getCollisionImage()
	{
		if (cMaskImage == null)
		{
			cMaskImage = Images.createImage(tileSize, tileSize);
			cMaskImage.setColor(new Color(0, 0, 0, 176));
			cMaskImage.drawRect(0, 0, cMaskImage.getWidth(), cMaskImage.getHeight(), true);
		}
		return cMaskImage;
	}
	
	private void createCollisionMap()
	{
		int width = map.getWidth() / map.getTileSize();
		int height = map.getHeight() / map.getTileSize();
		
		mapSprites = new Sprite[width][height];
		Sprite sprite;
		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				short s = map.isTilePassable(x, y);
				if (s == 0) continue;
				if (s == -1)
				{
					sprite = new DebugSprite();
				}
				else
				{
					sprite = new DebugSprite(map, s);
				}
//				sprite.setX(x * map.getTileSize() - map.getScrollX());
//				sprite.setY(y * map.getTileSize() - map.getScrollY());
				sprite.setX(x * map.getTileSize());
				sprite.setY(y * map.getTileSize());
				sprite.setDepth((int)sprite.getY() + 1);
				
				mapSprites[x][y] = sprite;
			}
		}
	}
	
	private void createCollisionMask()
	{
		CuinaObject object;
		for(Integer key : map.getObjectIDs())
		{
			object = map.getObject(key);
			CollisionBox box = (CollisionBox) object.getExtension("box");
			if (box != null)
			{
				createCMaskSprite(object);
			}
		}
	}
	
	private void createCMaskSprite(CuinaObject object)
	{
//		Rectangle box = object.getMask().getBox();
		CollisionBox box = (CollisionBox) object.getExtension("box");
		if (box == null) return;
		Rectangle bounds = box.getBounds();
		Sprite sprite = new DebugSprite();
//		sprite.ox = -bounds.x;
//		sprite.oy = -bounds.y;
//		sprite.x = box.x - map.getScrollX();
//		sprite.y = box.y - map.getScrollY();
//		sprite.z = sprite.y + map.getTileSize();
		sprite.setZoomX((bounds.width) / (float)cMaskImage.getWidth());
		sprite.setZoomY((bounds.height) / (float)cMaskImage.getHeight());
		objectSprites.put(object.getID(), sprite);
	}
	
	private void update0()
	{
		if (mapSprites == null || map == null) return;
		
		Sprite sprite;
		for(int x = 0; x < mapSprites.length; x++)
		{
			for(int y = 0; y < mapSprites[0].length; y++)
			{
				sprite = mapSprites[x][y];
				if (sprite != null)
				{
					sprite.setX(x * map.getTileSize());
					sprite.setY(y * map.getTileSize());
					sprite.setDepth((int)sprite.getY() + 31);
				}
			}
		}
		
		CuinaObject object;
		for(Integer key : map.getObjectIDs())
		{
			object = map.getObject(key);
			sprite = objectSprites.get(key);
			CollisionBox box = (CollisionBox) object.getExtension("box");
			if (box != null)
			{
				if (sprite == null)
				{
					createCMaskSprite(object);
					return;
				}
				
				Rectangle bounds = box.getBox();
				sprite.setX(bounds.x);
				sprite.setY(bounds.y);
				sprite.setDepth((int) sprite.getY() + map.getTileSize());
			}
		}
		
		Iterator<Integer> itr = objectSprites.keySet().iterator();
		while(itr.hasNext())
		{
			Integer key = itr.next();
			if (map.getObject(key) == null)
			{
				objectSprites.get(key).dispose();
				itr.remove();
			}
		}
		
		for(int i = 0; i < CSAreaSprites.length; i++)
		{
			sprite = CSAreaSprites[i];
		}
		
//		HashMap<Integer, MapObject> areas = map.getAreas();
//		MapObject area;
//		for(Integer key : areas.keySet())
//		{
//			area = areas.get(key);
//			sprite = mapAreaSprites.get(key);
//			if (sprite == null)
//			{
//				createMapAreaSprite(area);
//				return;
//			}
//			
//			sprite.x = -map.getScrollX();
//			sprite.y = -map.getScrollY();
////			sprite.z = sprite.y + map.getTileSize();
//		}
	}
	
	private void dispose0()
	{
		map = null;
		Graphics.disposeGraphics(mapSprites);
		Graphics.disposeGraphics(objectSprites);
		Graphics.disposeGraphics(mapAreaSprites);
		Graphics.disposeGraphics(CSAreaSprites);
		cMaskImage.dispose();
	}
	
	private static class DebugSprite extends Sprite
	{
		private static final long	serialVersionUID	= 1;
		
		private int width;
		private int height;
		private Color color;
		private boolean fill;
		
		public DebugSprite()
		{
			super(getCollisionImage());
		}
		
		public DebugSprite(GameMap map, short bits)
		{
			super(Images.createImage(map.getTileSize(), map.getTileSize()));
			
			int ts = map.getTileSize();
			this.width = ts;
			this.height = ts;
			this.color = new Color(0, 0, 0, 176);
			this.fill = true;
			
			getImage().setColor(color);
			int maskSize = ts / 4;
			for(int i = 0; i < 16; i++)
			{
				if ((bits & (1 << i)) != 0)
					getImage().drawRect((i % 4) * maskSize, (i / 4) * maskSize, maskSize, maskSize, fill);
			}
		}
		
		public DebugSprite(int width, int height, Color color, boolean fill)
		{
			super(Images.createImage(width, height));
			
			this.width = width;
			this.height = height;
			this.color = color;
			this.fill = fill;
			
			getImage().setColor(color);
			getImage().drawRect(0, 0, width, height, fill);
		}

		@Override
		public void refresh()
		{
			if (width == 0)
			{
				setImage(getCollisionImage());
			}
			else
			{
				Image image = Images.createImage(width, height);
				image.setColor(color);
				image.drawRect(0, 0, width, height, fill);
				setImage(image);
			}
		}
		
	}
}
