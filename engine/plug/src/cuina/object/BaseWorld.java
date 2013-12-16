package cuina.object;

import cuina.Game;
import cuina.Logger;
import cuina.graphics.GraphicContainer;
import cuina.graphics.GraphicSet;
import cuina.graphics.Graphics;
import cuina.plugin.LifeCycle;
import cuina.plugin.LifeCycleAdapter;
import cuina.world.CuinaObject;
import cuina.world.CuinaWorld;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BaseWorld implements LifeCycle, Serializable, CuinaWorld
{
	private static final long serialVersionUID = 1L;

	private static final String WORLD_GRAPHIC_CONTAINER_KEY = "cuina.world";
	
	private int width;
	private int height;
//	private int scrollX = 0;
//	private int scrollY = 0;
	
	private boolean freeze;
	private final ConcurrentHashMap<Integer, CuinaObject> objects =
			new ConcurrentHashMap<Integer, CuinaObject>(20, 0.8f, 4);
	private final String SPIRIT_KEY = "$" + Integer.toHexString(hashCode());

	private int aviableID;
	
	/**
	 * Gibt die Instanz der Spielwelt zurück.
	 * @return Die Instanz der Spielwelt.
	 * @throws ClassCastException Wenn die Spielwelt kein Instanz von BaseWorld ist.
	 */
	public static BaseWorld getInstance()
	{
		return (BaseWorld) Game.getWorld();
	}
	
	@Override
	public void init() {}
	
	@Override
	public void update()
	{
		for (CuinaObject obj : objects.values())
		{
			obj.update();
		}
	}

	@Override
	public void postUpdate()
	{
		for (CuinaObject obj : objects.values())
		{
			obj.postUpdate();
		}
	}

	@Override
	public void dispose()
	{
		for (CuinaObject obj : objects.values())
		{
			if (!obj.isPersistent())
			{
				obj.dispose();
				objects.remove(obj);
			}
		}
	}

	public void clear()
	{
		objects.clear();
	}

	@Override
	public int getObjectCount()
	{
		return objects.size();
	}

	@Override
	public int getAvilableID()
	{
		return aviableID;
	}

	@Override
	public boolean addObject(CuinaObject obj)
	{
		if (obj == null)
		{
			Logger.log(BaseWorld.class, Logger.WARNING, "Objekt ist null.");
			return false;
		}

		if (objects.containsKey(obj.getID()))
		{
			Logger.log(BaseWorld.class, Logger.WARNING, "ID-Konflikt! Objekt " + obj.getID() + " abgelehnt.");
			return false;
		}

        aviableID = Math.min(aviableID, obj.getID() + 1);
		obj.addExtension(SPIRIT_KEY, new Spirit(this, obj));
		return true;
	}

	@Override
	public Set<Integer> getObjectIDs()
	{
		return objects.keySet();
	}

	@Override
	public CuinaObject getObject(int id)
	{
		return objects.get(id);
	}

	@Override
	public void removeObject(int id)
	{
		removeObject(objects.get(id));
	}

	@Override
	public void removeObject(CuinaObject obj)
	{
		if (obj == null) return;

		obj.dispose();
	}

	@Override
	public void setFreeze(boolean value)
	{
		freeze = value;
	}

	@Override
	public boolean isFreezed()
	{
		return freeze;
	}

	@Override
	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}
//
//	@Override
//	public int getScrollX()
//	{
//		return scrollX;
//	}

//	@Override
//	public void setScrollX(int scrollX)
//	{
//		this.scrollX = scrollX;
//	}

//	@Override
//	public int getScrollY()
//	{
//		return scrollY;
//	}
//
//	@Override
//	public void setScrollY(int scrollY)
//	{
//		this.scrollY = scrollY;
//	}
	
//	public void scrollTo(int x, int y, int threshold)
//	{
//		int diffX = x - Graphics.getWidth() / 2 - scrollX;
//		int diffY = y - Graphics.getHeight() / 2 - scrollY;
//
//		if (Math.abs(diffX) > threshold)
//		{
//			x += (diffX < 0) ? threshold : -threshold;
//			scrollX = Math.max(0, Math.min(x - Graphics.getWidth() / 2 , getWidth() - Graphics.getWidth()));
//		}
//		if (Math.abs(diffY) > threshold)
//		{
//			y += (diffY < 0) ? threshold : -threshold;
//			scrollY = Math.max(0, Math.min(y - Graphics.getHeight() / 2 , getHeight() - Graphics.getHeight()));
//		}
//	}
//	
//	public void scrollTo(int x, int y)
//	{
//		scrollTo(x, y, 0);
//	}

	/**
	 * Prüft, ob die Position auf der Karte gültig ist. Wird auch in
	 * <code>isPassable</code> geprüft, daher ist ein direkter Aufruf meißt unnötig.
	 * 
	 * @param x
	 *            X-Position in Pixel
	 * @param y
	 *            Y-Position in Pixel
	 */
	public boolean isValid(int x, int y)
	{
		return (x >= 0 && y >= 0 && x < width && y < height);
	}

	/**
	 * Prüft, ob das Rechteck auf der Karte gültig ist. Wird auch in
	 * <code>isPassable</code> geprüft, daher ist ein direkter Aufruf meißt unnötig.
	 * 
	 * @param rect
	 *            Kartenbereich in Pixel
	 */
	public boolean isValid(Rectangle rect)
	{
		return (rect.x >= 0 && rect.y >= 0 && rect.x + rect.width <= width && rect.y + rect.height <= height);
	}
	
	/**
	 * Der Geist ist eine Erweiterung eines Objekts, der es mit der Welt verbindet.
	 * Nur solange ein Objekt einen Geist hat, ist es mit der Welt verdunden.
	 * Stirbt ein Objekt, bringt der Geist es aus der Welt.
	 * @author TheWhiteShadow
	 */
	public static class Spirit extends LifeCycleAdapter implements Serializable
	{
		private static final long serialVersionUID = 4861290551009035892L;
		
		private CuinaObject obj;
		private BaseWorld word;

		private Spirit(BaseWorld word, CuinaObject obj)
		{
			this.word = word;
			this.obj = obj;
		}

		@Override
		public void init()
		{
			word.objects.put(obj.getID(), obj);
		}

		@Override
		public void dispose()
		{
			word.objects.remove(obj.getID());
		}
	}

	@Override
	public GraphicContainer getGraphicContainer()
	{
		GraphicContainer container = Graphics.GraphicManager.getContainer(WORLD_GRAPHIC_CONTAINER_KEY);
		
		if (container == null)
		{
			container = new GraphicSet(WORLD_GRAPHIC_CONTAINER_KEY, 0, Graphics.GraphicManager);
		}
		return container;
	}
}
