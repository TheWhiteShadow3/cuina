package cuina.object;

import cuina.Game;
import cuina.Logger;
import cuina.event.Event;
import cuina.graphics.GraphicContainer;
import cuina.graphics.GraphicSet;
import cuina.graphics.Graphics;
import cuina.graphics.View;
import cuina.plugin.LifeCycle;
import cuina.plugin.LifeCycleAdapter;
import cuina.util.Rectangle;
import cuina.world.CuinaObject;
import cuina.world.CuinaWorld;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Eine vollständige Implementierung der CuinaWorld.
 * @author TheWhiteShadow
 */
public class BaseWorld implements LifeCycle, Serializable, CuinaWorld
{
	private static final long serialVersionUID = 1L;

	/** Prüft den Trigger automatisch, sobald das Objekt erstellt wurde. */
	public static final Event OBJECT_CREATE = Event.getEvent("cuina.object.Create");
	/** Prüft den Trigger jenden Frame automatisch. */
	public static final Event OBJECT_UPDATE = Event.getEvent("cuina.object.Update");
	/** Prüft den Trigger bei passiver Kollision mit Objekt. */
	public static final Event TOUCHED_BY_OBJECT = Event.getEvent("cuina.object.TouchedByObject");
	/** Prüft den Trigger bei aktiver Kollision mit Objekt. */
	public static final Event OBJECT_TOUCH = Event.getEvent("cuina.object.Touch");
	
	private static final String WORLD_GRAPHIC_CONTAINER_KEY = "cuina.world";
	
	private Rectangle bounds = new Rectangle();
	
	private boolean freeze;
	private final ConcurrentHashMap<Integer, CuinaObject> objects =
			new ConcurrentHashMap<Integer, CuinaObject>(20, 0.8f, 4);
	private final String SPIRIT_KEY = "$" + Integer.toHexString(hashCode());

	private int aviableID;
	
	/**
	 * Gibt die Instanz der Spielwelt zurück.
	 * @return Die Instanz der Spielwelt.
	 * @throws ClassCastException Wenn die Spielwelt keine Instanz von BaseWorld ist.
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
		if (isFreezed()) return;
		
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
		return aviableID++;
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

        if (obj.getID() == aviableID) do
    	{
    		aviableID++;
    	}
    	while(!objects.containsKey(aviableID));
    		
		obj.addExtension(SPIRIT_KEY, new Spirit(this, obj));
		obj.testTriggers(OBJECT_CREATE, null, obj);
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
		return bounds.width;
	}

	public void setWidth(int width)
	{
		bounds.width = width;
	}

	@Override
	public int getHeight()
	{
		return bounds.height;
	}

	public void setHeight(int height)
	{
		bounds.height = height;
	}
	
	/**
	 * Gibt eine Referenz auf die Grenzhülle der Welt zurück.
	 * Änderungen an dem Rechteck spiegel sich auf die Grenzhülle der Welt wieder.
	 * @return Referenz auf die Grenzhülle der Welt.
	 */
	public Rectangle getBounds()
	{
		return bounds;
	}
	
	public void follow(int objectID)
	{
		follow(objectID, 0);
	}
	
	public void follow(int objectID, int viewID)
	{
		CuinaObject obj = getObject(objectID);
		if (obj != null)
		{
			View view = Graphics.VIEWS.get(viewID);
			view.border = bounds;
			view.target = obj;
		}
	}
	
	/**
	 * Gibt an, ob das übergebene Objekt ein Mitglied dieser Welt ist.
	 * @param object Das Objekt, welches geprüft werden soll.
	 * @return <code>true</code>, wenn das Objekt dieser Welt angehört, andernfalls <code>false</code>.
	 */
	public boolean isMemberOf(CuinaObject object)
	{
		// Diese Methode sollte schneller gehen, als zu gucken, ob das Objket in der Liste steht.
		return object.getExtension(SPIRIT_KEY) != null;
	}

	/**
	 * Prüft, ob die Position innerhalb der Welt liegt.
	 * 
	 * @param x
	 *            X-Position
	 * @param y
	 *            Y-Position
	 */
	public boolean contains(int x, int y)
	{
		return bounds.contains(x, y);
	}

	/**
	 * Prüft, ob das Rechteck innerhalb der Welt liegt.
	 * 
	 * @param rect
	 *            Kartenbereich
	 */
	public boolean contains(Rectangle rect)
	{
		return bounds.contains(rect);
	}
	
	/**
	 * Der Geist ist eine Erweiterung eines Objekts, der es mit der Welt verbindet.
	 * Nur solange ein Objekt einen Geist hat, ist es mit der Welt verbunden.
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
