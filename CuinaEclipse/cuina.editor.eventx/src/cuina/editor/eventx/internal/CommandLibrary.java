package cuina.editor.eventx.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import cuina.editor.core.CuinaProject;
import cuina.editor.core.engine.EngineReference;
import cuina.editor.eventx.internal.editors.TypeEditor;
import cuina.eventx.Command;

public class CommandLibrary
{
	public static final String GLOBAL_CONTEXT	= "GLOABL";
	public static final String SESSION_CONTEXt	= "SESSION";
	public static final String SCENE_CONTEXt 	= "SCENE";
	public static final String STATIC_CONTEXT = "STATIC";
	public static final String INTERNAL_CONTEXT = "INTERNAL";
	
	private static final String EDITOR_EXTENSION = "cuina.eventx.types";
	private static final String FUNCTION_EXENSION = "cuina.eventx.functions";
	private static final String CATEGORY_EXENSION = "cuina.eventx.categories";
	private static final String CONTEXT_EXENSION = "cuina.eventx.contexts";
	
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
		defaultCategory = new Category("Default");
		categories.put(null, defaultCategory);
		
		loadInternalFunctions();
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
        	Category category = new Category(label);
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
        	String type = conf.getAttribute("type");
        	String name = conf.getAttribute("name");
        	
        	Class clazz = engineClassloader.loadClass(conf.getAttribute("class"));
        	ContextTarget target = new ContextTarget(type, name, clazz);
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
		catch (ClassNotFoundException | InvalidRegistryObjectException e)
		{
			e.printStackTrace();
		}
	}
	
	private void addFunction(IConfigurationElement conf) throws ClassNotFoundException, InvalidRegistryObjectException
	{
    	Category category = categories.get(conf.getAttribute("categoryID"));
    	if (category == null) category = defaultCategory;
    	
    	ContextTarget context = contexts.get(conf.getAttribute("contextID"));
    	if (context == null) throw new NullPointerException("Context is not defined.");
    	
		String name = conf.getAttribute("name");
		if (name.isEmpty()) throw new IllegalArgumentException();
		
		String label = conf.getAttribute("label");
		if (label == null) label = name;

		IConfigurationElement[] childs = conf.getChildren("argument");
		Class[] argTypes = new Class[childs.length];
		String[] argNames = new String[childs.length];
		for (int i = 0; i < childs.length; i++)
		{
			argTypes[i] = getClass(childs[i].getAttribute("type"));
			argNames[i] = childs[i].getAttribute("label");
		}
		boolean isStatic = Boolean.parseBoolean(conf.getAttribute("static"));
		category.addFunktion(new FunctionEntry(
				context.getTarget(isStatic), context.clazz, name, label, argTypes, argNames));
	}
	
	private Class getClass(String className) throws ClassNotFoundException
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
		}
		return engineClassloader.loadClass(className);
	}
	
	public Map<String, Category> getCategories()
	{
		return Collections.unmodifiableMap(categories);
	}
	
	public <T> TypeEditor<T> newTypeEditor(Class<T> clazz)
	{
		try
		{
			Class<TypeEditor> editorClass = typeEditors.get(clazz.getName());
			if (editorClass != null)
				return editorClass.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			e.printStackTrace();
			
		}
		return null;
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
	
	public Command createCommand(FunctionEntry function)
	{
		return new Command(function.target, function.name, 0, new Object[function.argTypes.length]);
	}
	
	public Category findCategory(FunctionEntry function)
	{
		if (function == null) throw new NullPointerException();
		
		for (Category category : categories.values())
		{
			for (FunctionEntry f : category.getFunctions())
			{
				if (f == function) return category;
			}
		}
		return null;
	}
	
	public FunctionEntry getFunction(Command cmd)
	{
		String key = cmd.target + '.' + cmd.name;
		return functions.get(key);
	}
	
	private void addInternalFunction(String name, Class<?> paramTypes, String paramNames)
	{
		Category category = categories.get(null);
		category.addFunktion(new FunctionEntry(
				null, null, name, name, new Class[] {paramTypes}, new String[] {paramNames}));
	}
	
	private void addInternalFunction(String name)
	{
		Category category = categories.get(null);
		category.addFunktion(new FunctionEntry(null, null, name, name, new Class[0], new String[0]));
	}
	
	private void loadInternalFunctions()
	{
		addInternalFunction("wait", int.class, "Frames");
		addInternalFunction("skip", int.class, "Zeilen");
		addInternalFunction("if", String.class, "Bedingung");
		addInternalFunction("while", String.class, "Bedingung");
		addInternalFunction("goto", int.class, "Zeile");
		addInternalFunction("stop");
	}
}