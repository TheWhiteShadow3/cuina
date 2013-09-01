package cuina.script;

import cuina.Game;
import cuina.Logger;
import cuina.database.DataTable;
import cuina.database.Database;
import cuina.event.Trigger;
import cuina.plugin.CuinaPlugin;
import cuina.plugin.PluginManager;
import cuina.util.CuinaClassLoader;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.RubyObject;
import org.jruby.exceptions.RaiseException;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

/**
* Der ScriptExecutor definiert die Schnittstelle zur SkriptEngine und
* stellt Methoden zur Verfügung um Skripte im Spiel zu starten und zu steuern.
* <p>
* Skripte bestehen aus einer Klasse, die anfangs instanziert wird.
* Auf dieser Instanz werden alle weiteren Aufrufe durchgeführt.
* Die Methoden der Skripte können alle einzeln mit Argumenten Aufgerufen werden.
* Um Eine Methode eines anderen Skripts aufzurufen kann man die Instanz über <code>script_&lt;KEY&gt;</code> erhalten.
* </p>
* <p>
* Skripte können ein Interface implementieren und so als vollwertige Implementierung zu diesem
* innerhalb der Engine eingesetzt werden. Das wichtigste Interface bildet das MainScript,
* welches die zentralen Aktionen start/ende, speichern/laden definiert.
* </p>
* <p>
* <b>Achtung!</b> Ein direkter Zugriff auf den OpenGL-Kontext ist aus Skripten heraus nicht möglich.
* Um dieses Manko zu umgehen ist die Image-Klasse so konzipiert,
* dass sie Zeichenoperationen an die <code>Graphics</code>-Klasse weiterleitet.
* Diese puffert die Aufrufe bis zum Ende des Frames um sie dann im GL-Thread durchzuführen.
* </p>
* @author TheWhiteShadow
* @see ScriptTrigger
* @see Trigger
*/
public class ScriptExecuter
{
	public static final String SCRIPT_DB = "Script";

	public static final int STOPPED		= 0;
	public static final int READY 		= 1;
	public static final int STARTED 	= 2;
	public static final int SHUTDOWN 	= 3;
	public static final int FAILD 		= 4;
	
	private static Ruby runtime;
	private final Queue<ScriptCall> scriptQueue = new LinkedList<ScriptCall>();
//	private final ArrayList<ScriptCall> autoCallList = new ArrayList<ScriptCall>();
	/** Liste der Ruby-Instanzen zu den Skripten. */
	private final HashMap<String, RubyObject> instanceCache = new HashMap<String, RubyObject>();
	private static ScriptExecuter instance;
//	private static ScriptCall tempCall;
	private static volatile int state = STOPPED;

	private ScriptExecuter() {}

	public static void init()
	{
		if (instance != null) return;
		instance = new ScriptExecuter();
		
		Logger.log(ScriptExecuter.class, Logger.DEBUG, "ScriptExecuter starting...");

		RubyInstanceConfig config = new RubyInstanceConfig();
		config.setLoader(CuinaClassLoader.getInstance());
		runtime = Ruby.newInstance(config);
		runtime.getLoadService().require("java");
		state = READY;
	}
	
	public static void loadScripts()
	{
		try
		{
			executeMainScripts();
			loadPluginScripts();
			loadDatabaseScripts();
			
			state = STARTED;
			Logger.log(ScriptExecuter.class, Logger.DEBUG, "Script-loading complete.");
		}
		catch (RaiseException e)
		{
			Logger.log(ScriptExecuter.class, Logger.ERROR, e);
			state = FAILD;
			Logger.log(ScriptExecuter.class, Logger.DEBUG, "Script-loading failed.");
		}
	}
	
	private static void executeMainScripts() throws RaiseException
	{
		String initScript = Game.getIni().get("Script", "Init-Script");
		if (initScript != null)
		{
			Logger.log(ScriptExecuter.class, Logger.INFO, "load Init-Script: <" + initScript + ">");
			eval(initScript);
		}
	}
	
	private static void loadPluginScripts() throws RaiseException
	{
//		String dir = PluginManager.getPluginDirectory().getAbsolutePath();
		Collection<CuinaPlugin> plugins = PluginManager.getPluginFiles().values();
		for (CuinaPlugin plugin : plugins)
		{
			String lib = plugin.getScriptLib();
			if (lib == null) continue;
			
			String jar = plugin.getFile().getAbsolutePath();
			try
			{
				runtime.getLoadService().require(jar);
				runtime.getLoadService().require(lib);
			}
			catch (RaiseException e)
			{
				Logger.log(ScriptExecuter.class, Logger.ERROR, e);
			}
		}
	}
	
	private static void loadDatabaseScripts()
	{
		DataTable<Script> table = Database.getDataTable(SCRIPT_DB);
		if (table != null)
		{
			for (Script script : table.values())
			{
				instance.createScriptInstance(script);
			}
		}
	}

	public static Ruby getRuntime()
	{
		return runtime;
	}
	
	public static void shutdown()
	{
		if (runtime != null) runtime.tearDown(false);
		instance = null;
		state = SHUTDOWN;
	}

	public static int getState()
	{
		return state;
	}

	private void createScriptInstance(Script script) throws RaiseException
	{
		Logger.log(ScriptExecuter.class, Logger.DEBUG, "load Script: " + script.getKey());
		if (script.getCode() == null || script.getCode().isEmpty())
		{
			Logger.log(ScriptExecuter.class, Logger.WARNING, "Script is empty: " + script.getKey());
			return;
		}
		
		StringBuilder builder = new StringBuilder(script.getCode().length() + 32);
		builder.append(script.getCode());
		RubyObject scriptInstance = (RubyObject) eval(builder.toString());
		
//		RubyObject scriptInstance = (RubyObject) scriptEngine.eval(builder.toString());
		if (scriptInstance == null) return;
		
		instanceCache.put(script.getKey(), scriptInstance);
//		runtime.defineVariable(new GlobalVariable(runtime, "OBJ_" + script.getKey(), scriptInstance), runtime.g);
		runtime.defineGlobalConstant("OBJ_" + script.getKey(), scriptInstance);
		// Erstelle eine Variable im Skript-Kontext
//		scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE).put("$script_" + script.getKey(), scriptInstance);
	}

	/**
	 * Führt ein Skript sofort aus.
	 * 
	 * @param name
	 *            Name des Skripts.
	 * @param main
	 *            Main-Methode des Skripts.
	 * @param args
	 *            Object-Array mit den Argumenten.
	 * @return Rückgabewert des Skripts.
	 */
	public static Object executeDirect(String name, String main, Object... args)
	{
		return instance.executeScript(name, main, args);
	}

	/**
	 * Fügt ein Skript der Skript-Queue hinzu um es anschließend auszuführen.
	 * 
	 * @param name Der Name des Skripts.
	 * @param main Der Name der Funktion, die aufgerufen werden soll.
	 * @param args Die Argumente, die dem Skript übergeben werden.
	 */
	public static void execute(String name, String main, Object... args)
	{
		instance.scriptQueue.add(new ScriptCall(name, main, args));
	}

	public static void update()
	{
		if (instance != null) instance._update();
	}
	
	private void _update()
	{
//		for (int i = autoCallList.size() - 1; i >= 0; i--)
//		{
//			AutoCallEntry entry = autoCallList.get(i);
//			if (--entry.waitTime <= 0)
//			{
//				executeScript(entry.call);
//				if (entry.waitTime <= 0) autoCallList.remove(i);
//			}
//		}
		
		while (scriptQueue.peek() != null)
		{
			ScriptCall call = scriptQueue.poll();
			executeScript(call.scriptKey, call.main, call.args);
		}
	}

	public static IRubyObject eval(String code)
	{
		try
		{
			return runtime.executeScript(code, null);
		}
		catch (RaiseException e)
		{
			Logger.log(ScriptExecuter.class, Logger.ERROR, e);
		}
		return null;
	}

	private Object executeScript(String name, String main, Object... args)
	{
		RubyObject scriptInstance = instanceCache.get(name);
		if (scriptInstance == null)
		{
			Logger.log(ScriptExecuter.class, Logger.ERROR,
					"Script-Instance for " + name + " do not exists.");
			return null;
		}

		try
		{
			Logger.log(ScriptExecuter.class, Logger.DEBUG,
					"execute script: " + name + "." + main);
			
//			runtime.defineVariable( new GlobalVariable(runtime, "$trigger", toRubyObject(trigger)), Scope.THREAD);
			
			IRubyObject result;
			if (args != null && args.length > 0)
				result = scriptInstance.callMethod(main, toRubyObject(args));
			else
				result = scriptInstance.callMethod(main);
			
			return JavaEmbedUtils.rubyToJava(result);
		}
		catch (RaiseException e)
		{
			Logger.log(ScriptExecuter.class, Logger.ERROR, e);
			return null;
		}
	}
	
	private IRubyObject toRubyObject(Object javaObject)
	{
		return JavaEmbedUtils.javaToRuby(runtime, javaObject);
	}
	
	private IRubyObject[] toRubyObject(Object[] javaObjects)
	{
		IRubyObject[] rubyObjects = new IRubyObject[javaObjects.length];
		
		for (int i = 0; i < rubyObjects.length; i++)
		{
			rubyObjects[i] = toRubyObject(javaObjects[i]);
		}
		
		return rubyObjects;
	}

//	private Object callFunc(Object instance, String method, Object[] args)
//			throws NoSuchMethodException, ScriptException
//	{
//		return ((Invocable) scriptEngine).invokeMethod(instance, method, args);
//	}
	
	private static class ScriptCall
	{
		public String scriptKey;
		public String main;
		public Object[] args;
		
		public ScriptCall(String scriptKey, String main, Object... args)
		{
			this.scriptKey = scriptKey;
			this.main = main;
			this.args = args;
		}
	}
	

	
//	private static class AutoCallEntry
//	{
//		public int waitTime;
//		public ScriptCall call;
//	}
}
