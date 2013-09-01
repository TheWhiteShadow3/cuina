package cuina;
 
import cuina.plugin.ForGlobal;
import cuina.plugin.ForScene;
import cuina.plugin.ForSession;
import cuina.plugin.LifeCycle;
import cuina.plugin.PluginManager;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Injektiert Objekte in verschiedene Kontexte und aktiviert LifeCycle-Instanzen.
 * Plugins werden automatisch in die einzelnen Kontexte injektiert, wenn sie entsprechend gekennzeichnet sind.
 * <p>
 * Die Kontexte unterscheiden sich jeweils über den Zeitraum ihrer Gültigkeit.
 * <i>Gültigkeit bedeutet hier die Zugreifbarkeit der Objekte über den Kontext,
 * sowie der Aktivität der LifeCycle-Instanzen.</i>
 * </p>
 * <dl>
 * 	<dt><b>Globaler Kontext:</b></dt>
 * 	<dd>Objekte, die vom Start der Engine bis zum Beenden von dieser gültig sind.
 * Plugin-Klassen, die die Annotation {@link ForGlobal} besitzen,
 * werden automatisch in diesen Kontext injektiert.</dd>
 * 
 * 	<dt><b>Session Kontext:</b></dt>
 * 	<dd>Objekte in diesem Kontext sind erst beim anlegen einer Session gültig und zwar bis diese beendet,
 * 	oder eine andere Session wiederhergestellt wird. Dort abgelegte Objekte müssen serialisierbar sein.</dd>
 * 
 * 	<dt><b>Szenen Kontext:</b></dt>
 * 	<dd>Hier beläuft sich die Gültigkeit nur innerhalb einer Szene.
 * Die Szenen für die Gültigkeit bestehen soll, können über die Annotation {@link ForScene} spezifiziert werden.</dd>
 * </dl>
 * @author TheWhiteShadow
 */
public class InjectionManager
{	
	/* Szenen-Objekte, dessen Instanz wieder verwendet werden soll. */
	static HashMap<String, Object> persistentObjects = new HashMap<String, Object>();
	
	static Map<String, ObjectContainer> globalClasses = new HashMap<String, ObjectContainer>();
	static Map<String, ObjectContainer> sessionClasses = new HashMap<String, ObjectContainer>();
	static Map<String, ObjectContainer> sceneClasses = new HashMap<String, ObjectContainer>();

	private InjectionManager() {}

	static void loadPluginClasses()
	{
		Annotation an;
		for (Class<?> clazz : PluginManager.getPluginsClasses())
		{
			an = clazz.getAnnotation(ForGlobal.class);
			if (an != null)
			{
				addContextObject(Context.GLOBAL, new ObjectContainer(clazz, an));
			}
			an = clazz.getAnnotation(ForSession.class);
			if (an != null)
			{
				addContextObject(Context.SESSION, new ObjectContainer(clazz, an));
			}
			an = clazz.getAnnotation(ForScene.class);
			if (an != null)
			{
				addContextObject(Context.SCENE, new ObjectContainer(clazz, an));
			}
		}
	}
	
	public static void addContextObject(int contextType, ObjectContainer container)
	{
		getContainerMap(contextType).put(container.getName(), container);
	}
	
	/**
	 * Instanziert alle Objekte, die während des angegebenen Kontextes gültig sind.
	 */
	public static void loadContextObjects(int contextType)
	{
		Context context = Game.getContext(contextType);
		context.clear();
		for(ObjectContainer c : getContainerMap(contextType).values())
		{
			inject(c, context);
		}
	}
	
//	private static void updateInjectionValues()
//	{
//		for(ObjectContainer c : getContainerMap(Context.GLOBAL).values())
//		{
//			for(FieldInjector fi : c.getInjects())
//			{
//				injectField(c.getObject(), fi);
//			}
//		}
//		
//		for(ObjectContainer c : getContainerMap(Context.SESSION).values())
//		{
//			for(FieldInjector fi : c.getInjects())
//			{
//				injectField(c.getObject(), fi);
//			}
//		}
//		
//		for(ObjectContainer c : getContainerMap(Context.SCENE).values())
//		{
//			for(FieldInjector fi : c.getInjects())
//			{
//				injectField(c.getObject(), fi);
//			}
//		}
//	}
	
//	private static void injectField(Object instance, FieldInjector fi)
//	{
//		ObjectContainer container = getContainerMap(fi.contextType).get(fi.name);
//		if (container == null)
//		{
//			Logger.log(InjectionManager.class, Logger.ERROR, "Can not inject " + fi.name);
//		}
//		
//		Field field = fi.field;
//		boolean wasAccassible = field.isAccessible();
//		field.setAccessible(true);
//		try
//		{
//			field.set(instance, container.getObject());
//		}
//		catch (IllegalArgumentException | IllegalAccessException e)
//		{
//			Logger.log(InjectionManager.class, Logger.ERROR, e);
//		}
//		finally
//		{
//			if (!wasAccassible)
//				field.setAccessible(false);
//		}
//	}
	
	public static void injectObject(Object obj, String name)
	{
		injectObject(obj, name, new String[] {Game.getScene().getName()}, Context.SCENE);
	}

	public static void injectObject(Object obj, String name, String[] scenes, int contextType)
	{
		if (name == null) throw new NullPointerException("name is null");
		if (obj == null) throw new NullPointerException("obj is null");
//		if (!SceneContext.exists()) throw new IllegalStateException("Scene-Context do not exists.");
		
		ObjectContainer container = new ObjectContainer(obj, name, scenes, contextType);
		addContextObject(contextType, container);
		
		inject(container, Game.getContext(contextType));
	}	
	
	private static void inject(ObjectContainer container, Context context)
	{
		Object obj = container.getObject();
		if (obj == null) return;
			
		String name = container.getName();
		if (name.length() > 0)
		{
			if (context.get(name) != null)
			{
				dublicateObject(name);
				return;
			}
			context.set(name, obj);
		}
		
		if (obj instanceof LifeCycle)
		{
			Scene scene = Game.getScene();
			if (scene != null && scene.isStarted() && includeScene(scene.getName(), container.getScenes()))
			{
				scene.initLifeCycle(container);
			}
		}
	}

    private static boolean includeScene(String sceneName, String[] scenes)
    {
    	if (sceneName == null || scenes == null) return false;
        if (scenes.length == 1 && Scene.ALL_SCENES[0].equals(scenes[0])) return true;
        
        for(String name : scenes)
        {
            if (sceneName.equals(name)) return true;
		}
		return false;
	}
	
	static Map<String, ObjectContainer> getContainerMap(int contextType)
	{
		switch(contextType)
		{
			case Context.GLOBAL: return globalClasses;
			case Context.SESSION: return sessionClasses;
			case Context.SCENE:return sceneClasses;
		}
		throw new IllegalArgumentException("invalid context-type " + contextType);
	}
	
	static List<ObjectContainer> getLifeCycleObjects(int contextType, String sceneName)
	{
		Map<String, ObjectContainer> map = getContainerMap(contextType);
		List<ObjectContainer> list = new ArrayList<ObjectContainer>(map.size());
		for(ObjectContainer c : map.values())
		{
			if (sceneName != null && !includeScene(sceneName, c.getScenes()) ) continue;
			if (c.getObject() instanceof LifeCycle) list.add(c);
		}
		return list;
	}
	
	private static void dublicateObject(String name)
	{
		Logger.log(InjectionManager.class, Logger.WARNING,
				"Object-Key '" + name + "' already exists in the denoted context.");
	}
}