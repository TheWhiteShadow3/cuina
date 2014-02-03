package cuina.object;

import cuina.Logger;
import cuina.ObjectContainer;
import cuina.database.Database;
import cuina.event.Event;
import cuina.event.Trigger;
import cuina.plugin.LifeCycle;
import cuina.plugin.Upgradeable;
import cuina.util.Vector;
import cuina.world.CuinaObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class BaseObject implements Serializable, Upgradeable, CuinaObject
{
	private static final long serialVersionUID = -5435193188062906672L;

	public static final int PERSISTENT_OFFSET = 1_000_000;
	
	private int id = -1;
	private String name;
	private Vector pos = new Vector(0, 0, 0);
	private final ArrayList<Trigger> triggers = new ArrayList<Trigger>();
	private final HashMap<String, Object> extensions = new HashMap<String, Object>(8);
	private final ArrayList<ObjectContainer> updateOrder = new ArrayList<ObjectContainer>(4);
	
	public BaseObject() {}
	
	public BaseObject(ObjectData data)
	{
		init(data);
	}

	/**
	 * Initialisiert das Objekt.
	 * @param data Datenobjekt.
	 */
	public void init(ObjectData data)
	{
		this.id = data.id;
		this.pos = new Vector(data.x, data.y, data.z);
		if (data.templateKey != null && !data.templateKey.isEmpty())
			initSourceObjectData(Database.<ObjectTemplate>get("Template", data.templateKey).sourceObject);
		else
			initSourceObjectData(data);
	}
		
	private void initSourceObjectData(ObjectData data)
	{
		this.name = data.name;
		for (String key : data.extensions.keySet())
		{
			Object obj = data.extensions.get(key);
			if (obj instanceof Instantiable) try
			{
				addExtension(key, ((Instantiable) obj).createInstance(this));
			}
			catch (Exception e)
			{
				Logger.log(BaseObject.class, Logger.ERROR, e);
			}
		}
		triggers.addAll(data.triggers);
	}
	
	/**
	 * Setzt die ID des Objekts.
	 * Das darf nur geschehen, solange das Objekt keiner Welt angehört.
	 * @param id
	 */
	public void setID(int id)
	{
		this.id = id;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public int getID()
	{
		return id;
	}

	public Vector getPosition()
	{
		return pos;
	}
	
	public void setPosition(float x, float y, float z)
	{
		pos.x = x;
		pos.y = y;
		pos.z = z;
	}
	
	public void setPosition(BaseObject obj)
	{
		pos.set(obj.pos);
	}

	@Override
	public float getX()
	{
		return pos.x;
	}

	@Override
	public void setX(float x)
	{
		pos.x = x;
	}

	@Override
	public float getY()
	{
		return pos.y;
	}

	@Override
	public void setY(float y)
	{
		pos.y = y;
	}

	@Override
	public float getZ()
	{
		return (int) pos.z;
	}

	@Override
	public void setZ(float z)
	{
		pos.z = z;
	}

	@Override
	public void addTrigger(Trigger trigger)
	{
		triggers.add(trigger);
	}

	@Override
	public boolean removeTrigger(Trigger trigger)
	{
		return triggers.remove(trigger);
	}

	@Override
	public List<Trigger> getTriggers()
	{
		return triggers;
	}
	
	@Override
	public void testTriggers(Event event, Object eventArg, Object... callArgs)
	{
		for(Trigger trigger : triggers)
		{
			if (trigger.isActive() && trigger.test(event, eventArg))
			{
				trigger.run(callArgs);
			}
		}
	}

	@Override
	public Object getExtension(String key)
	{
		return extensions.get(key);
	}

	@Override
	public void addExtension(String key, Object instance)
	{
		Object old = extensions.get(key);
		if (old != null && old instanceof LifeCycle)
		{
			((LifeCycle) old).dispose();
		}
		
		extensions.put(key, instance);
		
		if (instance instanceof LifeCycle)
		{
			ObjectContainer container = new ObjectContainer(instance, key, null, -1);
			int pri = container.getUpdatePriority();
			int i = updateOrder.size();
			while (--i >= 0 && updateOrder.get(i).getUpdatePriority() < pri);
			updateOrder.add(i + 1, container);
			container.init();
		}
	}

	@Override
	public Set<String> getExtensionKeys()
	{
		return extensions.keySet();
	}
	
	public void removeExtension(String key)
	{
		Object obj = extensions.remove(key);
		if (obj instanceof LifeCycle)
		{
			((LifeCycle) obj).dispose();
		}
	}

	@Override
	public void update()
	{
		for (ObjectContainer ext : updateOrder)
		{
			ext.update();
		}
		testTriggers(BaseWorld.OBJECT_UPDATE, null, this);
	}

	@Override
	public void postUpdate()
	{
		for (ObjectContainer ext : updateOrder)
		{
			ext.postUpdate();
		}
	}
	
	@Override
	public void dispose()
	{
		for (ObjectContainer ext : updateOrder)
		{
			ext.dispose();
		}
	}

	@Override
	public boolean exists()
	{
		BaseWorld world = BaseWorld.getInstance();
		if (world == null) return false;
		
		return world.getObject(id) != null;
	}
	
	@Override
	public boolean isPersistent()
	{
		return id >= PERSISTENT_OFFSET;
	}

	@Override
	public String toString()
	{
		return "Object(" + id + ") '" + name + '\'';
	}
}
