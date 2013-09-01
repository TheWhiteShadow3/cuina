package cuina;

import cuina.plugin.ForGlobal;
import cuina.plugin.ForScene;
import cuina.plugin.ForSession;
import cuina.plugin.LifeCycle;
import cuina.plugin.Priority;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Kontainer für ein Objekt, welches in einen Kontext injiziert werden soll.
 * @author TheWhiteShadow
 */
public class ObjectContainer implements Serializable
{
	private static final long serialVersionUID = -448361024141222680L;
	
	private Class<?> clazz;
	private Object object;
	private int context;
	private String name = "";
	private String[] scenes = Scene.ALL_SCENES;
//	private final List<FieldInjector> injects = new ArrayList<FieldInjector>();
	
	private int initPriority = 0;
	private int updatePriority = 0;
	private int disposePriority = 0;
	
	public ObjectContainer(Class<?> clazz, Annotation an)
	{
		if (clazz == null) throw new NullPointerException("clazz is null");
		
		this.clazz = clazz;
		analyzeContextAnnotation(an);
		analyzePriorities();
//		analyzeInjectAnnotations();
	}
	
	public ObjectContainer(Object obj, String name, String[] scenes, int contextType)
	{
		if (obj == null) throw new NullPointerException("object is null");
		if (name == null) throw new NullPointerException("name is null");
		
		this.clazz = obj.getClass();
		this.object = obj;
		this.name = name;
		this.scenes = scenes;
		this.context = contextType;
		analyzePriorities();
//		analyzeInjectAnnotations();
	}
	
//	public ObjectContainer(Object object, int context, String name)
//	{
//		this.object = object;
//		this.context = context;
//		this.name = name;
//	}
//	
//	public ObjectContainer(Object object, int context, String name, String[] scenes)
//	{
//		this.object = object;
//		this.context = context;
//		this.name = name;
//		this.scenes = scenes;
//	}
	
	public Object newObject() throws InstantiationException, IllegalAccessException
	{
		this.object = clazz.newInstance();
		return object;
	}

//	public List<FieldInjector> getInjects()
//	{
//		return injects;
//	}

	public Object getObject()
	{
		if (object == null) try
		{
			return newObject();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			Logger.log(ObjectContainer.class, Logger.ERROR, e);
		}
		return object;
	}
	
	public int getContext()
	{
		return context;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String[] getScenes()
	{
		return scenes;
	}

	private void analyzeContextAnnotation(Annotation an)
	{
		if (an == null) return;
		
		if (an instanceof ForGlobal)
		{
			context = Context.GLOBAL;
			name = ((ForGlobal) an).name();
			scenes = Scene.ALL_SCENES;
		}
		else if (an instanceof ForSession)
		{
			context = Context.SESSION;
			name = ((ForSession) an).name();
			scenes = ((ForSession) an).scenes();
		}
		else if (an instanceof ForScene)
		{
			context = Context.SCENE;
			name = ((ForScene) an).name();
			scenes = ((ForScene) an).scenes();
		}
	}
	
//	private void analyzeInjectAnnotations()
//	{
//		for (Field f : clazz.getDeclaredFields())
//		{
//			Inject inject = f.getAnnotation(Inject.class);
//			if (inject != null)
//			{
//				if (inject.context() == context && inject.name() == name)
//					Logger.log(ObjectContainer.class, Logger.WARNING, "Self-Injection detected!");
//				
//				injects.add(new FieldInjector(f, inject));
//			}
//		}
//	}
	
	private void analyzePriorities()
	{
		Priority priority = clazz.getAnnotation(Priority.class);
		if (priority == null) return;
		
		initPriority = priority.initPriority();
		updatePriority = priority.updatePriority();
		disposePriority = priority.disposePriority();
	}
	
	public int getInitPriority()
	{
		return initPriority;
	}

	public int getUpdatePriority()
	{
		return updatePriority;
	}

	public int getDisposePriority()
	{
		return disposePriority;
	}
	
	private LifeCycle getLifeCycleInstance()
	{
		if (!(object instanceof LifeCycle))
			throw new UnsupportedOperationException("Object is not an Instance of LifeCycle.");
		return (LifeCycle) object;
	}

	public void init()
	{
		getLifeCycleInstance().init();
	}
	
	public void update()
	{
		getLifeCycleInstance().update();
	}
	
	public void postUpdate()
	{
		getLifeCycleInstance().postUpdate();
	}
	
	public void dispose()
	{
		getLifeCycleInstance().dispose();
	}
	
	public static class updateSorter implements Comparator<ObjectContainer>
	{
		@Override
		public int compare(ObjectContainer o1, ObjectContainer o2)
		{
			return o2.updatePriority - o1.updatePriority;
		}
	}
	
	public static class disposeSorter implements Comparator<ObjectContainer>
	{
		@Override
		public int compare(ObjectContainer o1, ObjectContainer o2)
		{
			return o2.disposePriority - o1.disposePriority;
		}
	}

	@Override
	public String toString()
	{
		return "ObjectContainer [object=" + object + "; priorities=(" + initPriority +
				", " + updatePriority + ", " + disposePriority + "); scenes=" +
				Arrays.toString(getScenes()) + "]";
	}
	
//	public static class FieldInjector
//	{
//		public Field field;
//		public int contextType;
//		public String name;
//		
//		public FieldInjector(Field field, Inject inject)
//		{
//			this.field = field;
//			this.contextType = inject.context();
//			this.name = inject.name();
//		}
//	}
}
