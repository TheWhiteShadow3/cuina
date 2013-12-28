package cuina.eventx;

import cuina.Context;
import cuina.Game;
import cuina.Logger;
import cuina.database.Database;
import cuina.plugin.PluginManager;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import tws.expression.Config;
import tws.expression.EvaluationException;
import tws.expression.Expression;
import tws.expression.Resolver;

public class Interpreter implements Serializable
{
	private static final long serialVersionUID = -3483878509656721211L;

	public static enum ContextType { INTERNAL, GLOBAL, SESSION, SCENE, STATIC, ARGUMENT }
	
	public static class Result
	{
		/**
		 * Ein Ergebnis-Objekt ohne besondere Anweisung. Führt dazu,
		 * dass der Interpreter mit der nächsten Anweisung weiter macht.
		 */
		public static final Result DEFAULT = new Result();
		/**
		 * Ein Ergebnis-Objekt mit der Anweisung den Interpreter zu beenden.
		 */
		public static final Result STOP = new Result(0, 0, true);
		/**
		 * Ein Ergebnis-Objekt mit der Anweisung den Interpreter einen Frame warten zu lassen.
		 */
		public static final Result WAIT_ONE_FRAME = new Result(1, 0, false);
		
		/** Wartet eine Anzahl von Frames bis zur Ausführung der nächsten Anweisung. */
		public int wait;
		/** Überspringt eine Anzahl von Befehlen des gleichen oder niedrigeren Levels. */
		public int skip;
		/** Stopt den Interpreter. */
		public boolean stop;
		
		public Result() {};
		
		public Result(int wait, int skip, boolean stop)
		{
			this.wait = wait;
			this.skip = skip;
			this.stop = stop;
		}
	}
	
	static class FunctionAccessor
	{
		public String clazz;
		public Method method;
		
		public FunctionAccessor(String clazz, Method method)
		{
			this.clazz = clazz;
			this.method = method;
		}
	}
	
	private static final Map<String, FunctionAccessor> FUNCTIONS = new HashMap<String, FunctionAccessor>();
	private static final Map<String, Object> VARIABLES = new HashMap<String, Object>();
	private static final ThreadLocal<Interpreter> CONTEXT_INSTANCE = new ThreadLocal<Interpreter>();
	
	private final Config EXPRESSION_CONFIG;
	
	private CommandList list;
	private int index;
	private boolean run;
	private int waitCount;
	private Object[] setupArgs;
	
	static
	{
		for (Class clazz : PluginManager.getPluginsClasses())
		{
			findMethods(clazz, clazz.getAnnotations());
		}
	}
	
	public Interpreter()
	{
		EXPRESSION_CONFIG = new Config();
		EXPRESSION_CONFIG.booleanBehavor =
				Config.BooleanBehavor.NON_ZERO_NUMBERS |
				Config.BooleanBehavor.NON_EMPTY_STRINGS;
		EXPRESSION_CONFIG.nullBehavor = Config.NullBehavor.TO_FALSE;
		EXPRESSION_CONFIG.resolver = new EventNameResolver();
	}

	private static void findMethods(Class clazz, Annotation[] annotations)
	{
		for (Method method : clazz.getMethods())
		{
			EventMethod f = method.getAnnotation(EventMethod.class);
			if (f == null) continue;
			
			String alias = f.alias().isEmpty() ? method.getName() : f.alias();
			if (alias.indexOf(':') != -1) throw new IllegalArgumentException("EventMethod.alias");
			
			addFunction(clazz.getName(), alias, method);
		}
	}
	
	private static void addFunction(String clazz, String alias, Method method)
	{
		String functionName = clazz + ':' + alias;
		FUNCTIONS.put(functionName, new FunctionAccessor(clazz, method));
		Logger.log(Interpreter.class, Logger.DEBUG, "Registriere Funktion: " + functionName);
	}
	
	/**
	 * Innerhalb einer vom Interpreter aufgerufenen Methode
	 * gibt diese Funktion den aktuellen Interpreter zurück.
	 * Ansonsten ist der Rückgabewert <code>null</code>.
	 * @return Kontext-Instanz vom Interpreter.
	 */
	public static Interpreter getContextInterpreter()
	{
		return CONTEXT_INSTANCE.get();
	}
	
	public int getArgumentCount()
	{
		return setupArgs.length;
	}
	
	public Object getArgument(int index)
	{
		return setupArgs[index];
	}

	public int getIndex()
	{
		return index;
	}

	public void setIndex(int index)
	{
		this.index = index;
	}

	public CommandList getList()
	{
		return list;
	}
	
	public void setArguments(Object... args)
	{
		this.setupArgs = args;
	}

	public void setup(String eventKey, Object... args)
	{
		setup(Database.<CommandList>get("Event", eventKey), args);
	}
	
	public void setup(CommandList list, Object... args)
	{
		this.list = list;
		this.index = -1;
		this.run = true;
		this.setupArgs = args;
	}
	
	public void update()
	{
		if (!run) return;
		
		waitCount--;
		if (waitCount > 0) return;

		while (run)
		{
			index++;
			if (index >= list.commands.length)
			{
				run = false;
				return;
			}
			
			Command cmd = getCommand();
			if (cmd == null) throw new NullPointerException("Command-list contains null.");

			callMethod(cmd);
			if (!run || waitCount > 0) break;
		}
	}
	
	private Command getCommand()
	{
		if (index >= list.commands.length) return null;
		return list.commands[index];
	}
	
	private void callMethod(Command cmd)
	{
		int seperator = cmd.target.lastIndexOf(':');
		ContextType context;
		String contextName = null;
		if (seperator != -1)
		{
			context = ContextType.valueOf(cmd.target.substring(0, seperator));
			contextName = cmd.target.substring(seperator+1);
		}
		else
			context = ContextType.valueOf(cmd.target);
		
		if (context == ContextType.INTERNAL)
		{
			handleSpecialCommands(cmd);
			return;
		}
		
		FunctionAccessor fa;
		Object instance = null;
		if (context == ContextType.STATIC)
		{
			fa = FUNCTIONS.get(contextName + ':' + cmd.name);
		}
		else
		{
			String name = cmd.name;
			switch(context)
			{
				case GLOBAL: instance = Game.getContext(Context.GLOBAL).get(contextName); break;
				case SESSION: instance = Game.getContext(Context.SESSION).get(contextName); break;
				case SCENE: instance = Game.getContext(Context.SCENE).get(contextName); break;
				case ARGUMENT: instance = setupArgs[Integer.parseInt(contextName)]; break;
			}
			if (instance == null) throw new NullPointerException(
						"Target-Instance '" + contextName + "' in Context '" + context + "' do not exists.");

			String functionName = instance.getClass().getName() + ':' + name;
			fa =  FUNCTIONS.get(functionName);
		}
		if (fa == null)
			throw new NullPointerException("Funktion für '" + cmd.name + "' wurde nicht gefunden.");
		
		try
		{
			CONTEXT_INSTANCE.set(this);
			Object obj = invokeMethode(fa.method, instance, cmd.args);
			if (obj instanceof Result)
			{
				Result result = (Result) obj;

				run = !result.stop;
				waitCount = result.wait;
				if (result.skip != 0) skipCommands(result.skip);
			}
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			Logger.log(Interpreter.class, Logger.ERROR, e);
		}
		finally
		{
			CONTEXT_INSTANCE.set(null);
		}
	}
	
	private Object[] resolveArguments(Object[] args)
	{
		Object[] result = new Object[args.length];
		for(int i = 0; i < args.length; i++)
		{
			Object arg = args[i];
			if (arg instanceof Argument)
				result[i] = setupArgs[((Argument) arg).index];
			else
				result[i] = args[i];
		}
		return result;
	}
	
//	private Object[] convertToArray(Method method, Object args)
//	{
//		// Sonderfall, wenn das Argument ein Array ist.
//		if (args.getClass().isArray())
//		{
//			// Überprüfe, ob als Argument kein ein Array verlangt wird.
//			Class[] params = method.getParameterTypes();
//			if ( !(params.length == 1 && params[0].isArray()) )
//			{
//				// Konvertiere Die Argumente in ein Objekt-Array.
//				int length = Array.getLength(args);
//				Object[] array = new Object[length];
//				for(int i = 0; i < length; i++)
//				{
//					array[i] = Array.get(args, i);
//				}
//				return array;
//			}
//		}
//		return new Object[] {args};
//	}
	
	private Object invokeMethode(Method method, Object obj, Object[] args)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return method.invoke(obj, resolveArguments(args));
	}
	
	private void skipCommands(int count)
	{
		Command[] commands = list.commands;
		int level = commands[index].indent;
		int sign = (count >= 0) ? +1 : -1;
		while(count != 0)
		{
			if (commands[index+sign].indent <= level)
			{
				index += sign;
				count -= sign;
			}
		}
	}
	
	private void skipBlock()
	{
		Command[] commands = list.commands;
		int level = commands[index].indent;
		while(commands[index+1].indent > level)
		{
			index++;
		}
	}
	
	private void handleSpecialCommands(Command cmd)
	{
		switch(cmd.name)
		{
			case "set":		setVariable((String) cmd.args[0], cmd.args[1]); break;
			case "get":		getVariable((String) cmd.args[0]); break;
			case "wait":	waitCount = (int) cmd.args[0]; break;
			case "skip":	skipCommands((int) cmd.args[0]); break;
			case "if":		handleCondition((String) cmd.args[0]); break;
			case "while":	handleCondition((String) cmd.args[0]); break;
			case "goto":	setIndex((int) cmd.args[0] - 1); break;
			case "stop":	run = false; break;
			default: throw new NullPointerException("Interne Funktion '" + cmd.name + "' ist nicht definiert.");
		}
	}
	
	private void getVariable(String name)
	{
		VARIABLES.get(name);
	}

	private void setVariable(String name, Object value)
	{
		if (name.charAt(0) == '$') throw new IllegalArgumentException("Variable-Name must not start with $");
		VARIABLES.put(name, value);
	}

	private void handleCondition(String expression)
	{
		if (!new Expression(expression, EXPRESSION_CONFIG).resolve().asBoolean())
		{
			skipBlock();
		}
	}
	
	public boolean isRunning()
	{
		return run;
	}

	public void setWaitCount(int frames)
	{
		this.waitCount = frames;
	}
	
	public void stop()
	{
		run = false;
	}
	
	public void start()
	{
		if (list != null) run = true;
	}
	
	private class EventNameResolver implements Resolver
	{
		@Override
		public Object resolve(String name, tws.expression.Argument[] args) throws EvaluationException
		{
			if (name.charAt(0) == '$')
			{
				switch(name)
				{
					case "$index": return index;
					case "$argument.length": return setupArgs.length;
					case "$argument": return setupArgs[(int) args[0].asLong()];
				}
			}
			return VARIABLES.get(name);
		}
	}
}
