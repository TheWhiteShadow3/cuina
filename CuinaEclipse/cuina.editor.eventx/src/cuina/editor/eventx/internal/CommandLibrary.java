package cuina.editor.eventx.internal;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

import cuina.database.DatabaseObjectReference;
import cuina.editor.core.CuinaProject;
import cuina.editor.core.engine.EngineReference;
import cuina.editor.eventx.internal.editors.ArrayEditor;
import cuina.editor.eventx.internal.editors.BooleanEditor;
import cuina.editor.eventx.internal.editors.FloatEditor;
import cuina.editor.eventx.internal.editors.IDEditor;
import cuina.editor.eventx.internal.editors.IntegerEditor;
import cuina.editor.eventx.internal.editors.StringEditor;
import cuina.editor.eventx.internal.editors.TypeEditor;
import cuina.editor.eventx.internal.editors.UndefinedTypeEditor;
import cuina.eventx.Command;

public class CommandLibrary
{
	public static final String GLOBAL_CONTEXT	= "GLOABL";
	public static final String SESSION_CONTEXt	= "SESSION";
	public static final String SCENE_CONTEXt 	= "SCENE";
	public static final String STATIC_CONTEXT	= "STATIC";
	public static final String INTERNAL_CONTEXT = "INTERNAL";
	
	private static final String EDITOR_EXTENSION = "cuina.eventx.types";
	private static final String FUNCTION_EXENSION = "cuina.eventx.functions";
	private static final String CATEGORY_EXENSION = "cuina.eventx.categories";
	private static final String CONTEXT_EXENSION = "cuina.eventx.contexts";
	
	private static final String INTERNAL_ID = "cuina.eventx.Internal";
	
	private static final Map<String, Class<TypeEditor>> typeEditors = new HashMap<String, Class<TypeEditor>>();
	
	
	static
	{
        IConfigurationElement[] elements = Platform.getExtensionRegistry().
        		getConfigurationElementsFor(EDITOR_EXTENSION);
 
        for (IConfigurationElement conf : elements)
        {
        	try
			{
				String className = conf.getAttribute("class");
				if (typeEditors.containsKey(className)) continue;
				
				Bundle plugin = Platform.getBundle(conf.getContributor().getName());
				Class<TypeEditor> editorClass = (Class<TypeEditor>) plugin.loadClass(conf.getAttribute("editor"));
				
				typeEditors.put(className, editorClass);
			}
			catch (ClassNotFoundException | InvalidRegistryObjectException e)
			{
				e.printStackTrace();
			}
        }
	}
	
//	private final Map<String, Class> contextTypes = new HashMap<String, Class>();
//	private final Map<Class, String> contextTargets = new HashMap<Class, String>();
	private final Map<String, Category> categories = new HashMap<String, Category>();
	private final Map<String, ContextTarget> contexts = new HashMap<String, ContextTarget>();
	private final Map<String, FunctionEntry> functions = new HashMap<String, FunctionEntry>();
	private Category defaultCategory;
//	private CuinaProject project;
	private ClassLoader engineClassloader;
	
	public CommandLibrary(CuinaProject project)
	{
//		this.project = project;
		
		EngineReference ref = project.getService(EngineReference.class);
		this.engineClassloader = ref.getClassLoader();
		
//		try
//		{
//			getCategory("Szene").addFunktion(new FunctionEntry(target, clazz, name, argTypes, argNames))
//			setContextType(toTarget(GLOBAL_CONTEXT, "Scene"), "Szene", engineClassloader.loadClass("cuina.Scene"));
//			setContextType(toTarget(SESSION_CONTEXt, "World"), "Welt", engineClassloader.loadClass("cuina.map.GameMap"));
//			setContextType(toTarget(SESSION_CONTEXt, "Party"), "Gruppe", engineClassloader.loadClass("cuina.rpg.actor.ActorGroup"));
//			setContextType(toTarget(SESSION_CONTEXt, "Player"), "Spieler", engineClassloader.loadClass("cuina.object.BaseObject"));
//			setContextType(toTarget(SESSION_CONTEXt, "Message"), "Nachichten", engineClassloader.loadClass("cuina.message.MessageBox"));
//			setContextType(toTarget(SESSION_CONTEXt, "MessageHistory"), "Historie", engineClassloader.loadClass("cuina.message.MessageHistory"));
//			
//			setContextType(SESSION_CONTEXt, "BattleController", engineClassloader.loadClass("cuina.ks.Battle"));
//		}
//		catch (ClassNotFoundException e)
//		{
//			e.printStackTrace();
//		}
		
//		loadInternalFunctions();
		loadCommandDefinitions();
	}

	private void loadCommandDefinitions()
	{
        IConfigurationElement[] elements;
        elements = Platform.getExtensionRegistry().getConfigurationElementsFor(CATEGORY_EXENSION);
        
        for (IConfigurationElement conf : elements) try
        {
        	String id = conf.getAttribute("id");
        	String label = conf.getAttribute("label");
        	String imageName = conf.getAttribute("icon");
    		Bundle plugin = Platform.getBundle(conf.getContributor().getName());
    		Image image = null;
    		if (imageName != null) try
    		{
    			image = new Image(Display.getDefault(), FileLocator.resolve(plugin.getEntry(imageName)).getPath());
    		}
    		catch(Exception e) { e.printStackTrace(); }
        	
        	Category category = new Category(id, label, image);
        	categories.put(id, category);
        }
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
        
        elements = Platform.getExtensionRegistry().getConfigurationElementsFor(CONTEXT_EXENSION);
        
        for (IConfigurationElement conf : elements) try
		{
        	String id = conf.getAttribute("id");
        	if (contexts.containsKey(id)) throw new IllegalArgumentException("Context '" + id + "' already exists.");
        	
        	String type = conf.getAttribute("type");
        	if (type.equals(INTERNAL_CONTEXT) && !EventPlugin.PLUGIN_ID.equals(conf.getNamespaceIdentifier()))
        		throw new IllegalArgumentException("The context type INTERNAL can only used in cuina.eventx-Plugin.");
        	
        	String name = conf.getAttribute("name");

        	ContextTarget target = new ContextTarget(type, name, conf.getAttribute("class"));
        	contexts.put(id, target);
		}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
        
        elements = Platform.getExtensionRegistry().getConfigurationElementsFor(FUNCTION_EXENSION);
        
        for (IConfigurationElement conf : elements) try
		{
        	addFunction(conf);
		}
		catch (InvalidRegistryObjectException e)
		{
			e.printStackTrace();
		}
	}
	
	private void addFunction(IConfigurationElement conf) throws InvalidRegistryObjectException
	{
    	Category category = categories.get(conf.getAttribute("categoryID"));
    	if (category == null) category = defaultCategory;
    	
    	String contextID = conf.getAttribute("contextID");
    	if (INTERNAL_ID.equals(contextID) && !EventPlugin.PLUGIN_ID.equals(conf.getNamespaceIdentifier()))
    		throw new IllegalArgumentException("The internal context can only used in cuina.eventx-Plugin.");
    	
    	ContextTarget context = contexts.get(contextID);
    	if (context == null) throw new NullPointerException("Context '" + contextID + "' is not defined.");
    	
		String name = conf.getAttribute("name");
		if (name.isEmpty()) throw new IllegalArgumentException();
		
		String label = conf.getAttribute("label");
		if (label == null) label = name;
		
		String description = conf.getAttribute("description");

		IConfigurationElement[] childs = conf.getChildren("argument");
		String[] argTypes = new String[childs.length];
		String[] argNames = new String[childs.length];
		for (int i = 0; i < childs.length; i++)
		{
			argTypes[i] = childs[i].getAttribute("type");
			argNames[i] = childs[i].getAttribute("label");
		}
		addFunction(new FunctionEntry(category, context.getTarget(),
				context.className, name, label, description, argTypes, argNames));
	}
	
	public Class getClass(String className) throws ClassNotFoundException
	{
		switch(className)
		{
			case "boolean": return boolean.class;
			case "byte": return byte.class;
			case "char": return char.class;
			case "short": return short.class;
			case "int": return int.class;
			case "float": return float.class;
			case "long": return long.class;
			case "double": return double.class;
			case "void": return void.class;
			case "string":
			case "text": return String.class;
		}
		if (className.endsWith("[]"))
		{
			Class elementClass = getClass(className.substring(0, className.length()-2));
			return Array.newInstance(elementClass, 0).getClass();
		}
		if (className.startsWith("id:"))
		{
			return DatabaseObjectReference.class;
		}
		if (engineClassloader == null) return null;
		
		return engineClassloader.loadClass(className);
	}
	
	public Map<String, Category> getCategories()
	{
		return Collections.unmodifiableMap(categories);
	}
	
	public static TypeEditor newTypeEditor(String className)
	{
		try
		{
			Class<TypeEditor> editorClass = typeEditors.get(className);
			if (editorClass != null)
				return editorClass.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return createDefaultEditor(className);
	}
	
	
	private static TypeEditor<?> createDefaultEditor(String className)
	{
		switch(className)
		{
			case "boolean": return new BooleanEditor();
			case "string": 
			case "text": return new StringEditor();
			case "int": return new IntegerEditor(Integer.MAX_VALUE);
			case "short": return new IntegerEditor(Short.MAX_VALUE);
			case "byte": return new IntegerEditor(Byte.MAX_VALUE);
			case "float": return new FloatEditor();
		}
		if (className.equals("java.lang.String")) return new StringEditor();
		if (className.endsWith("[]")) return new ArrayEditor();
		if (className.startsWith("id:")) return new IDEditor(className.substring(3));
		
		return new UndefinedTypeEditor();
	}
	
//	public String getContextName(TypeEntry type)
//	{
//		return contextTargets.get(type.clazz);
//	}
	
//	public TypeEntry getContextype(String context, String name)
//	{
//		String address = toAddress(context, name);
//		return types.get(contextTypes.get(address));
//	}
	
	public Command createCommand(FunctionEntry function) throws ClassNotFoundException
	{
		Object[] args = new Object[function.argTypes.length];
		for (int i = 0; i < function.argTypes.length; i++)
		{
			String type = function.argTypes[i];
			if (type != null && type.endsWith("[]"))
			{
				args[i] = Array.newInstance(getClass(type), 0);
			}
		}
		return new Command(function.target, function.name, 0, args);
	}
	
	public FunctionEntry getFunction(Command cmd)
	{
		String key = cmd.target + '.' + cmd.name;
		return functions.get(key);
	}
	
//	private void addInternalFunction(String name, String paramTypes, String paramNames)
//	{
//		Category category = categories.get(null);
//		addFunction(new FunctionEntry(
//				category, INTERNAL_CONTEXT, null, name, name, new String[] {paramTypes}, new String[] {paramNames}));
//	}
//	
//	private void addInternalFunction(String name)
//	{
//		Category category = categories.get(null);
//		addFunction(new FunctionEntry(category, INTERNAL_CONTEXT, null, name, name, new String[0], new String[0]));
//	}
	
	private void addFunction(FunctionEntry function)
	{
		functions.put(function.target + '.' + function.name, function);
		function.category.addFunktion(function);
	}
	
//	private void loadInternalFunctions()
//	{
//		addInternalFunction("wait", "int", "Frames");
//		addInternalFunction("skip", "int", "Zeilen");
//		addInternalFunction("if", "string", "Bedingung");
//		addInternalFunction("while", "string", "Bedingung");
//		addInternalFunction("goto", "int", "Zeile");
//		addInternalFunction("stop");
//	}
}
