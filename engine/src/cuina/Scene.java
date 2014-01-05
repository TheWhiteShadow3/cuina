package cuina;

import cuina.plugin.LifeCycle;
import cuina.script.ScriptExecuter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Die Szene bildet einen Kontainer für das Spiel, indem eine Spielhandlung ablaufen kann.
 * Jede Szene besitzt die 3 Methoden init, update und dispsoe, welche den Lebensweg der Szene beschreiben.
 * Die Verwaltung der Szene kann und sollte dem {@link FrameTimer} überlassen werden.
 * <p>
 * Während des Spiels kann an beliebieger Stelle eine neue Szene erstellt werden.
 * Am Einfachsten ist es sich eine neue Szene über die Methode {@link Game#newScene(String)}) zu erstellen.
 * Damit ist sicher gestellt, dass die neue Szene auch dem <code>FrameTimer</code> übergeben wird.
 * </p>
 * <p>
 * Szenen erlauben zudem die selektive Benutzung von Plugins,
 * in welchen eine Anzahl von Szenen definiert werden kann,
 * in denen dieses als gültig angesehen wird.
 * <br><b>Beachte:</b> Plugin-Instanzen können auch außerhalb ihrer gültigen Szenen existent und über den
 * entsprechenden Kontext erreichbar sein. Siehe {@link LifeCycle} für weitere Einzelheiten.
 * </p>
 * <p>
 * Der Update-Zyklus einer Szene unterteilt sich in zwei Phasen:
 * <ol>
 * <li>Ausführung der Skripte in der Skript-Queue.</li>
 * <li>Aufruf der Methoden <code>update</code> und <code>postUpdate</code> aller injizierten LifeCycle-Instanzen.</li>
 * </ol>
 * </p>
 * @author TheWhiteShadow
 * @version 1.1
 */
public class Scene
{	/** Schlüsselkonstante für eine Gültigkeit über alle Szenen. */
	public static final String[] ALL_SCENES = new String[] {"_all_"};
	
	private boolean started;
	private final String name;

	private final ArrayList<ObjectContainer> updates = new ArrayList<ObjectContainer>();
	private final ArrayList<ObjectContainer> disposes = new ArrayList<ObjectContainer>();

	boolean needRefresh;

	public Scene(String name)
	{
		this.name = name;
	}
	
	/**
	 * Gibt den Namen der Szene zurück.
	 * 
	 * @return den Namen der Szene.
	 */
	public String getName()
	{
		return name;
	}
	
	void initLifeCycle(ObjectContainer container)
	{
		Logger.log(Scene.class, Logger.DEBUG, "Registriere LifeCycle-Objekt: " + container.getObject());
		
		try
		{
			container.init();
			
			updates.add(container);
			disposes.add(container);
		}
		catch (Exception e)
		{
			Logger.log(Scene.class, Logger.ERROR, e);
		}
	}
	
	public boolean includeThisScene(String[] scenes)
	{
		if (scenes == null) return true;
		
		if (scenes.length == 1 && ALL_SCENES[0].equals(scenes[0])) return true;
		if (this.name == null) return false;
		
		for(String name : scenes)
		{
			if (this.name.equals(name))
			{
				return true;
			}
		}
		return false;
	}
	
	protected final void init()
	{
		Logger.log(Scene.class, Logger.INFO, "start scene: '" + name + "'");
		for(ObjectContainer c : InjectionManager.getLifeCycleObjects(Context.GLOBAL, name))
		{
			initLifeCycle(c);
		}
		if (Game.getSession() != null)
		{
			for(ObjectContainer c : InjectionManager.getLifeCycleObjects(Context.SESSION, name))
			{
				initLifeCycle(c);
			}
		}
		InjectionManager.loadContextObjects(Context.SCENE);
		for(ObjectContainer c : InjectionManager.getLifeCycleObjects(Context.SCENE, name))
		{
			initLifeCycle(c);
		}
		
		// Sortiere Methoden-Listen nach Priorität
		Collections.sort(updates, new ObjectContainer.updateSorter());
		Collections.sort(disposes, new ObjectContainer.disposeSorter());
		
//		CuinaWorld world = Game.getWorld();
//		if (world != null) world.preRendering();
		started = true;
//		if (needRefresh)
//		{
//			for (ObjectContainer m : updates)
//			{
//				m.refresh();
//			}
//			needRefresh = false;
//		}
		Game.getInstance().fireEvent(GameEvent.NEW_SCENE);
	}

	public boolean isStarted()
	{
		return started;
	}

//	protected final void refresh()
//	{
//		for (ObjectContainer m : updates)
//		{
//			m.refresh();
//		}
//	}
	
	protected final void update()
	{
//		checkTriggerSceneUpdate();
//		System.out.println("new Frame: " + FrameTimer.getFrameCount());
		ScriptExecuter.update();
		
		// Update
		for (ObjectContainer m : updates)
		{
			m.update();
		}
		
		// Post-Update
		for (ObjectContainer m : updates)
		{
			m.postUpdate();
		}

//		WeatherEffects weather = (WeatherEffects)getObject("Weather");
//		if (weather != null) weather.update();
	}
	
//	private void checkTriggerSceneUpdate()
//	{
//		CuinaWorld world = Game.getWorld();
//		SaveHashMap<Integer, CuinaObject> objects = world.getObjects();
//		objects.lock();
//		for (Integer key : objects.keySet())
//		{
//			CuinaObject obj = objects.get(key);
//			for (Trigger tr : obj.getTriggers())
//			{
//				tr.testTrigger(obj, TriggerType.SCENE_UPDATE, -1);
//			}
//		}
//		objects.unlock();
//	}

	protected final void dispose()
	{
		for (ObjectContainer m : disposes)
		{
			m.dispose();
		}
		updates.clear();
		disposes.clear();
		started = false;
	}

	@Override
	public String toString()
	{
		return "\"" + name + "\"";
	}
}
