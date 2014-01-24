/*
 * Cuina Engine
 * Copyright (C) 2011 - 2012 by Cuina Team (http://www.cuina.byethost12.com/)
 * see license.txt for more info
 */

package cuina.map;

import cuina.Context;
import cuina.Game;
import cuina.Logger;
import cuina.database.Database;
import cuina.event.Event;
import cuina.eventx.EventMethod;
import cuina.eventx.Interpreter;
import cuina.graphics.Panorama;
import cuina.movement.CollisionSystem;
import cuina.object.BaseObject;
import cuina.object.BaseWorld;
import cuina.object.ObjectData;
import cuina.plugin.ForSession;
import cuina.plugin.Plugin;
import cuina.util.Rectangle;
import cuina.util.SaveHashMap;
import cuina.world.CuinaObject;
import cuina.world.CuinaWorld;

import java.io.File;
import java.util.HashMap;

@ForSession(name = CuinaWorld.INSTANCE_KEY, scenes = { "Map" })
public class GameMap extends BaseWorld implements Plugin
{
	private static final long serialVersionUID = -2742145729445081906L;

	public static final Event MAP_START = Event.getEvent("cuina.map.Start");
	public static final Event MAP_UPDATE = Event.getEvent("cuina.map.Update");
	public static final Event MAP_EXIT = Event.getEvent("cuina.map.Exit");
	/** Prüft den Trigger automatisch, sobald das Objekt erstellt wurde. */
	public static final Event OBJECT_CREATE = Event.getEvent("cuina.map.object.Create");
	/** Prüft den Trigger jenden Frame automatisch. */
	public static final Event OBJECT_UPDATE = Event.getEvent("cuina.map.object.Update");
	/** Prüft den Trigger bei passiver Kollision mit Objekt. */
	public static final Event TOUCHED_BY_OBJECT = Event.getEvent("cuina.map.object.TouchedByObject");
	/** Prüft den Trigger bei aktiver Kollision mit Objekt. */
	public static final Event OBJECT_TOUCH = Event.getEvent("cuina.map.object.Touch");
	/** Prüft den Trigger bei betreten einer Karten-Region. */
	public static final Event ENTERS_AREA = Event.getEvent("cuina.map.object.EntersArea");

	private Map map;
	private SaveHashMap<Integer, CuinaObject> areas = new SaveHashMap<Integer, CuinaObject>();
	private Panorama[] panoramas = new Panorama[10];

	private Tileset tileset;
	private TileMap tilemap;
	private short[][] collisionMap;
	private CollisionSystem cs;
	private boolean visible = true;

	private Interpreter mapInterpreter;

	@Override
	public void init()
	{
		super.init();
		Game.setWorld(this);
	}

	public void load(String key)
	{
		if(map != null)
		{
			// DebugPanel.dispose();
			// if (cs != null) cs.destroy();
			// if (tilemap != null) tilemap.dispose();
			// CuinaObject obj;
			// Iterator<Integer> itr = getObjects().keySet().iterator();
			// while(itr.hasNext())
			// {
			// Integer id = itr.next();
			// // ignoriere Persistend-Objekte
			// if (id > 100000) continue;
			// obj = getObject(id);
			// if (obj.getModel() != null) obj.getModel().dispose();
			// itr.remove();
			// }
			// areas.clear();
			map = null;
		}
		File file = new File(Game.getRootPath() + File.separator + "maps", key + ".cxmz");
		if(!file.exists())
			file = new File(Game.getRootPath() + File.separator + "maps", key + ".cxm");
		this.map = (Map) Database.loadData(file);
		initMap();
	}

	public void initMap()
	{
		tileset = Database.get("Tileset", map.tilesetKey);

		setWidth(map.width * getTileSize());
		setHeight(map.height * getTileSize());

		tilemap = new TileMap(tileset.getTilesetName(), tileset.getAutotiles(), getTileSize());
		tilemap.setPriorities(tileset.getPriorities());
		tilemap.setData(map.data);

		panoramas[0] = new Panorama(tileset.getBackgroundName());
		panoramas[0].setSpeedX(tileset.getBackSpeedX());
		panoramas[0].setSpeedY(tileset.getBackSpeedX());

		// lightMap = new MapSprite();
		// try
		// {
		// light1 = new LightSprite(208, 208);
		// light2 = new LightSprite(368, 208);
		// }
		// catch (LoadingException e)
		// {
		// e.printStackTrace();
		// }

		int id;
		collisionMap = new short[map.width][map.height];
		for(int x = 0; x < map.width; x++)
		{
			for(int y = 0; y < map.height; y++)
			{
				short passable = 0;
				for(int z = 2; z >= 0; z--)
				{
					id = map.data[x][y][z];
					if(id > 0 && id < Tileset.AUTOTILES_OFFSET)
					{
						if(tileset.getPassages()[id] != 0)
						{
							if(tileset.getPriorities()[id] == 0)
							{
								passable = tileset.getPassages()[id];
								break;
							}
						} else
						{ // TODO: Implementiere Priorität und Passage für
							// Autotiles
							if(tileset.getPriorities()[id] == 0)
								break;
						}
					}
				}
				collisionMap[x][y] = passable;
			}
		}
		cs = CollisionSystem.newInstance(this, getWidth() * getHeight() / 80000);
		// Objekte laden
		// HashMap<Integer, cuina.data.MapObject> objects = map.objects;
		// MapObject obj;
		for(Integer key : map.objects.keySet())
		{
			ObjectData src = (ObjectData) map.objects.get(key);
			addObject(new BaseObject(src));
			/*
			 * XXX Kleiner Workaround um eine Persistent-ID zu erzeugen.
			 * NOTE: Wegen Änderungen an der Objekt-Datenklasse nicht mehr
			 * gültig
			 * Muss später im Editor gemacht werden.
			 */
			// if (src. != null && src.motor.motorType == 3)
			// {
			// src.id += 100000;
			// }
		}
		mapInterpreter = new Interpreter();
		Game.getContext(Context.SESSION).set("Interpreter", mapInterpreter);
		// mapInterpreter.setup(Database.<CommandList>get("Event", "test"));
		// EventExecuter.init();
		// EventExecuter.runEvent(Database.<Event>get("Event",
		// "test").getCode());
		// for(Integer key : map.areas.keySet())
		// {
		// obj = new MapObject(map.areas.get(key));
		// addArea(obj);
		// }

		// WeatherEffects weather = new WeatherEffects(-100, -100, 840, 680);
		// Game.getScene().setObject("Weather", weather);
		// weather.initRain(0.5F);
		Logger.log(GameMap.class, Logger.INFO, "Map erstellt mit " + getObjectCount() + " Objekten");

		if(Game.isDebug())
		{
			DebugPanel.initDebugPanel();
		}
	}

	public static GameMap getInstance()
	{
		return Game.getContext(Context.SESSION).get(CuinaWorld.INSTANCE_KEY);
	}

	public CollisionSystem getCollisionSystem()
	{
		return cs;
	}

	/**
	 * @param id ID des Panoramas 0-9.
	 * @return Das Panorama oder null.
	 */
	public Panorama getPanorama(int id)
	{
		return panoramas[id];
	}

	/**
	 * Setzt das Panorama für dem angegebenen Index.
	 * @param id ID des Panoramas 0-9.
	 * @param panorama das zu setzende Panoramam oder null.

	 */
	public void setPanorama(int id, Panorama panorama)
	{
		this.panoramas[id] = panorama;
	}

	public String getKey()
	{
		return map.getKey();
	}

	public Map getMap()
	{
		return map;
	}

	public TileMap getTilemap()
	{
		return tilemap;
	}

	public int getTileSize()
	{
		if(tileset == null)
			return 0;
		return tileset.getTileSize();
	}

	// /**
	// * Prüft, ob das Rechteck auf der Karte gültig ist.
	// * Wird auch in <code>isPassable</code> geprüft, daher ist ein
	// * direkter Aufruf meißt unnötig.
	// * @param rect Kartenbereich in Pixel
	// * @see #isPassable
	// */
	// public boolean isValid(int x, int y, Rectangle rect)
	// {
	// return (x + rect.x >= 0 &&
	// y + rect.y >= 0 &&
	// x + rect.x + rect.width <= get &&
	// y + rect.y + rect.height <= height);
	// }

	// /**
	// * Prüft ob der angegebene Bereich passierbar ist.
	// * @param rect Kartenbereich in Pixel
	// */
	// public boolean isPassable(int x, int y, Rectangle rect)
	// {
	// if (!isValid(x, y, rect)) return false;
	//
	// int x1 = (x + rect.x) / getTileSize();
	// int y1 = (y + rect.y) / getTileSize();
	// int x2 = (x + rect.x + rect.width - 1) / getTileSize();
	// int y2 = (y + rect.y + rect.height - 1) / getTileSize();
	//
	// for(int xx = x1; xx <= x2; xx++)
	// {
	// for(int yy = y1; yy <= y2; yy++)
	// {
	// // System.out.println("Teste: " + xx + ", " + yy);
	// if (collisionMap[xx][yy] != 0) return false;
	// }
	// }
	// return true;
	// }

	/**
	 * Prüft ob der angegebene Bereich passierbar ist.
	 * 
	 * @param rect
	 *            Kartenbereich in Pixel
	 */
	public boolean isPassable(Rectangle rect)
	{
		if (!isValid(rect)) return false;

		int x1 = (rect.x) / getTileSize();
		int y1 = (rect.y) / getTileSize();
		int x2 = (rect.x + rect.width - 1) / getTileSize();
		int y2 = (rect.y + rect.height - 1) / getTileSize();

		for (int xx = x1; xx <= x2; xx++)
		{
			for (int yy = y1; yy <= y2; yy++)
			{
				// System.out.println("Teste: " + xx + ", " + yy);
				short bits = collisionMap[xx][yy];
				if(bits == 0) continue;
				if(bits == -1) return false;

				int cs = getTileSize() / 4;
				Rectangle cell = new Rectangle(cs, cs);
				for(int i = 0; i < 16; i++)
				{
					if((bits & (1 << i)) != 0)
					{
						cell.x = xx * getTileSize() + (i % 4) * cs;
						cell.y = yy * getTileSize() + (i / 4) * cs;
						if(rect.intersects(cell)) return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Prüft ob das Feld passierbar ist.
	 * 
	 * @param x
	 *            X-Position in Felder
	 * @param y
	 *            Y-Position in Felder
	 */
	public short isTilePassable(int x, int y)
	{
		if (!isValid(x * getTileSize(), y * getTileSize())) return -1;

		return collisionMap[x][y];
	}

	/**
	 * Prüft ob die Position passierbar ist.
	 * 
	 * @param x
	 *            X-Position in Pixel
	 * @param y
	 *            Y-Position in Pixel
	 */
	public boolean isPassable(int x, int y)
	{
		if(!isValid(x, y)) return false;

		return collisionMap[x / getTileSize()][y / getTileSize()] == 0;
	}
	
	@Override
	@EventMethod
	public void follow(int objectID, int viewID)
	{
		super.follow(objectID, viewID);
	}

	public CuinaObject getArea(int key)
	{
		return areas.get(key);
	}

	public HashMap<Integer, CuinaObject> getAreas()
	{
		return areas;
	}
	
	@Override
	public boolean addObject(CuinaObject obj)
	{
		if (super.addObject(obj))
		{
			cs.updatePosition(obj);
			obj.testTriggers(OBJECT_CREATE, obj.getID(), obj);
			return true;
		}
		return false;
	}

	@Override
	public void removeObject(CuinaObject obj)
	{
		super.removeObject(obj);
		cs.removeObject(obj);
	}

	@Override
	public void update()
	{
		if((map == null) || isFreezed()) return;

		mapInterpreter.update();
		// update = true;
		if(!mapInterpreter.isRunning())
		{
			super.update();
			for (Integer key : getObjectIDs())
			{
				CuinaObject obj = getObject(key);
				obj.setZ(obj.getY());
			}
			
			areas.lock();
			for(Integer key : areas.keySet())
			{
				areas.get(key).update();
			}
			areas.unlock();
		}
	}

	@Override
	public void postUpdate()
	{
		if(map == null) return;

		updateView();
		DebugPanel.update();
	}

	public void updateView()
	{
		if(map == null) return;

		tilemap.update();
		super.postUpdate();
	}

	public void setVisible(boolean value)
	{
		visible = value;
		tilemap.setVisible(value);
	}

	public boolean isVisible()
	{
		return visible;
	}

	@Override
	public void dispose()
	{
		DebugPanel.dispose();
		if(cs != null)
			cs.destroy();
		if(tilemap != null)
			tilemap.dispose();
		if(map != null)
		{
			super.dispose();
			areas.clear();
			map = null;
		}
	}

	public Interpreter getInterpreter()
	{
		return mapInterpreter;
	}
}
