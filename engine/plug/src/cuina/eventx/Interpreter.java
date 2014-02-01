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
import java.util.Arrays;
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
	
	/**
	 * Ein Result-Objekt kann neben dem eigentlichen Rückgabewert
	 * noch eine Reihe an Informationen für den Interpreter beinhalten.
	 * @author TheWhiteShadow
	 */
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
		/** Der Rückgabewert. Kann als Interpreter-Variable $result abgefragt werden. */
		public Object value;
		
		public Result() {};
		
		public Result(Object value)
		{
			this.value = value;
		}
		
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
	
	static class Setup implements Serializable
	{
		private static final long serialVersionUID = 1L;
		
		public CommandList list;
		public Object[] args;
		public int index;
		public Setup parent;
		
		public Setup(Setup parent, String eventKey, Object[] args)
		{
			this(parent, Database.<CommandList>get("Event", eventKey), args);
		}
		
		public Setup(Setup parent, CommandList list, Object[] args)
		{
			this.parent = parent;
			this.list = list;
			this.args = args;
			this.index = -1;
		}
		
		public Object getArgument(int index)
		{
			if (index < 0 || index >= args.length)
			{
				throw new IndexOutOfBoundsException("Argument '" + index + "' does not exist.");
			}
				
			return args[index];
		}
		
		public int getArgumentCount()
		{
			return args.length;
		}
		
		public boolean isFinished()
		{
			return index >= list.commands.length;
		}
		
		public Command getCommand()
		{
			if (isFinished()) return null;
			return list.commands[index];
		}
	}
	
	private static final String INSTANCE_KEY = "Interpreter";
	
	private static final Map<String, FunctionAccessor> FUNCTIONS = new HashMap<String, FunctionAccessor>();
	private static final ThreadLocal<Interpreter> CONTEXT_INSTANCE = new ThreadLocal<Interpreter>();
	
	private final Map<String, Object> variables;
	private final Config expressionConfig;
	
	private Setup setup;
	private boolean run;
	private int waitCount;
	private int switchValue;
	private Object resultValue;
	
	static
	{
		for (Class clazz : PluginManager.getPluginsClasses())
		{
			findMethods(clazz, clazz.getAnnotations());
		}
	}
	
	/**
	 * Gibt den globalen Interpreter zurück.
	 * @return der globale Interpreter oder <code>null</code>, wenn nicht gesetzt.
	 */
	public static Interpreter getGlobalInterpreter()
	{
		return Game.getContext(Context.GLOBAL).get(INSTANCE_KEY);
	}

	/**
	 * Setzt den globalen Interpreter.
	 * @param instance neuer globaler Interpreter.
	 */
	public static void setGlobalInterpreter(Interpreter instance)
	{
		Game.getContext(Context.GLOBAL).set(INSTANCE_KEY, instance);
	}
	
	/**
	 * Erzeugt einen neuen Interpreter.
	 * Wenn es bisher keinen globalen Interpreter gibt,
	 * wird die neue Instanz als globaler Interpreter gesetzt.
	 */
	public Interpreter()
	{
		this.variables = new HashMap<String, Object>();
		
		expressionConfig = new Config();
		expressionConfig.booleanBehavor =
				Config.BooleanBehavor.NON_ZERO_NUMBERS |
				Config.BooleanBehavor.NON_EMPTY_STRINGS;
		expressionConfig.nullBehavor = Config.NullBehavor.TO_FALSE;
		expressionConfig.resolver = new ResolverImpl();
		
		
		if (getGlobalInterpreter() == null)
			setGlobalInterpreter(this);
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
		return setup.getArgumentCount();
	}
	
	public Object getArgument(int index)
	{
		return setup.getArgument(index);
	}

	public int getIndex()
	{
		return setup.index;
	}

	public void setIndex(int index)
	{
		this.setup.index = index;
	}

	public CommandList getList()
	{
		return setup != null ? setup.list : null;
	}

	public void setup(String eventKey, Object... args)
	{
		setup(Database.<CommandList>get("Event", eventKey), args);
	}
	
	public void setup(CommandList list, Object... args)
	{
		this.setup = new Setup(null, list, args);
		this.run = true;
	}
	
	public void update()
	{
		if (!run) return;
		
		if (waitCount > 0)
		{
			waitCount--;
			return;
		}

		CONTEXT_INSTANCE.set(this);
		while (true)
		{
			setup.index++;
			if (isFinished())
			{
				if (setup.parent != null)
				{
					setup = setup.parent;
					continue;
				}
				
				run = false;
				break;
			}
				
			Command cmd = setup.getCommand();
			callCommand(cmd);
			
			if (!run || waitCount > 0) break;
		}
		CONTEXT_INSTANCE.set(null);
	}
	
	private void callCommand(Command cmd)
	{
		Logger.log(Interpreter.class, Logger.DEBUG, "Execute: " + cmd);
		
		try
		{
			if (cmd == null) throw new InterpreterException(setup, cmd, "Command is null.");
			
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
					case ARGUMENT: instance = setup.getArgument(Integer.parseInt(contextName)); break;
				}
				if (instance == null) throw new InterpreterException(setup, cmd, 
							"Target-Instance '" + contextName + "' in Context '" + context + "' do not exists.");
	
				String functionName = instance.getClass().getName() + ':' + name;
				fa =  FUNCTIONS.get(functionName);
			}
		
			if (fa == null)
				throw new InterpreterException(setup, cmd, "Function for '" + cmd.name + "' was not found.");
			
			Object obj = invokeMethode(cmd, fa.method, instance, resolveArguments(cmd.args));
			
			if (obj instanceof Result)
			{
				Result result = (Result) obj;

				run = !result.stop;
				waitCount = result.wait;
				this.resultValue = result.value;
				if (result.skip != 0) skipCommands(result.skip);
			}
			else this.resultValue = obj;
		}
		catch (InterpreterException e)
		{
			Logger.log(Interpreter.class, Logger.ERROR, e);
			run = false;
		}
		catch (Exception e)
		{
			Logger.log(Interpreter.class, Logger.ERROR, new InterpreterException(setup, cmd, e.getMessage()));
			run = false;
		}
	}
	
	private Object[] resolveArguments(Object[] args)
	{
		return resolveArguments(args, 0);
	}
	
	private Object[] resolveArguments(Object[] args, int start)
	{
		Object[] result = new Object[args.length - start];
		System.arraycopy(args, start, result, 0, result.length);
		for (int i = 0; i < result.length; i++)
		{
			Object arg = result[i];
			if (arg instanceof Argument)
				result[i] = setup.getArgument( ((Argument) arg).index );
			else if (arg instanceof Variable)
				result[i] = getVariable(((Variable) arg).name);
		}
		return result;
	}
	
	private Object invokeMethode(Command cmd, Method method, Object obj, Object[] args) throws InterpreterException
	{
		try
		{
			return method.invoke(obj, args);
		}
		catch(IllegalArgumentException e)
		{
			throw new InterpreterException(setup, cmd, getTypeExceptionMessage(e, method, args));
		}
		catch(InvocationTargetException e)
		{
			throw new InterpreterException(setup, cmd, e.getCause());
		}
		catch(Exception e)
		{
			throw new InterpreterException(setup, cmd, e);
		}
	}
	
	private void skipCommands(int count)
	{
		Command[] commands = setup.list.commands;
		int level = commands[setup.index].indent;
		int sign = (count >= 0) ? +1 : -1;
		while(count != 0)
		{
			if (commands[setup.index+sign].indent <= level)
			{
				setup.index += sign;
				count -= sign;
			}
		}
	}
	
	public void setSwitchValue(int value)
	{
		this.switchValue = value;
	}
	
	public void skipBlocks(int count)
	{
		while(count > 0)
		{
			skipBlock();
			count--;
		}
	}
	
	public void skipBlock()
	{
		Command[] commands = setup.list.commands;
		int level = commands[setup.index].indent;
		while(commands[setup.index+1].indent > level)
		{
			setup.index++;
		}
	}
	
	private void handleSpecialCommands(Command cmd)
	{
		switch(cmd.name)
		{
			case "if":
			case "while":	handleCondition((String) cmd.args[0]); break;
			case "set":		setVariable((String) cmd.args[0], cmd.args[1]); break;
			case "get":		getVariable((String) cmd.args[0]); break;
			case "wait":	waitCount = (int) cmd.args[0]; break;
			case "skip":	skipCommands((int) cmd.args[0]); break;
			case "case":	handleCase((int) cmd.args[0]); break;
			case "switch":	switchValue = (int) cmd.args[0]; break;
			case "goto":	setIndex((int) cmd.args[0] - 1); break;
			case "stop":	run = false; break;
			case "call":	callEvent((String) cmd.args[0], cmd.args); break;
			default: throw new NullPointerException("Internal function '" + cmd.name + "' is not defined.");
		}
	}
	
	private void callEvent(String eventKey, Object[] args)
	{
		this.setup = new Setup(setup, eventKey, resolveArguments(args, 1));
	}

	private Object getInternalVariable(String name)
	{
		try
		{
			if (name.startsWith("$argument"))
			{
				if ("$argument.length".equals(name)) return setup.getArgumentCount();
				int index = Integer.parseInt(name.substring(9));
				if (setup.getArgumentCount() > index)
					return setup.getArgument(index);
			}
			else switch(name)
			{
				case "$index": return setup.index;
				case "$result": return resultValue;
			}
		}
		catch(Exception e) {}
		return null;
	}
	
	private Object getVariable(String name)
	{
		Object result = null;
		if (name.charAt(0) == '$')
			result = getInternalVariable(name);
		else
			result = variables.get(name);
		
		if (result == null)
			Logger.log(Interpreter.class, Logger.WARNING, "Variable '" + name + "' does not exists.");
		
		return result;
	}

	private void setVariable(String name, Object value)
	{
		if (name.charAt(0) == '$') throw new IllegalArgumentException("Variable-Name must not start with $");
		variables.put(name, value);
	}

	private void handleCondition(String expression)
	{
		if (!new Expression(expression, expressionConfig).resolve().asBoolean()) skipBlock();
	}
	
	private void handleCase(int value)
	{
		if (switchValue != value) skipBlock();
	}
	
	public boolean isRunning()
	{
		return run;
	}
	
	public boolean isFinished()
	{
		return setup != null && setup.isFinished();
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
		if (setup != null) run = true;
	}
	
	private String getTypeExceptionMessage(Exception e, Method method, Object[] args)
			throws InterpreterException
	{
		StringBuilder builder = new StringBuilder(e.getMessage());
		builder.append('\n');
		
		builder.append("\tExpected Types: ");
		Class<?>[] paramTypes = method.getParameterTypes();
		String[] types = new String[paramTypes.length];
		for (int i = 0; i < types.length; i++)
			types[i] = paramTypes[i].getName();
		
		builder.append(Arrays.toString(types)).append('\n');
		builder.append("\tCalled Types:   ");
		types = new String[args.length];
		for (int i = 0; i < types.length; i++)
			types[i] = args[i].getClass().getName();
		
		builder.append(Arrays.toString(types)).append('\n');
		
		return builder.toString();
	}
	
	private class ResolverImpl implements Resolver
	{
		@Override
		public Object resolve(String name, tws.expression.Argument[] args) throws EvaluationException
		{
			return getVariable(name);
		}
	}
}
