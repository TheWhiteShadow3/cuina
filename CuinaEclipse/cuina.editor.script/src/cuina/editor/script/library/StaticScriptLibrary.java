package cuina.editor.script.library;
 
import cuina.editor.core.CuinaProject;
import cuina.editor.script.Scripts;
import cuina.editor.script.internal.ScriptDescriptionParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.xml.sax.SAXException;
 
/**
 * Eine Bibliothek für Funktionen, die für Ruby-Skripte benutzt werden können.
 * Die Einträge werden aus den Plugins gelesen und können im SkriptDialog des Editors angezeigt werden.
 * <p>
 * Zur Einbindung muss innerhalb der Manifest über dem Attribut "<code>Script-Library</code>"
 * eine Rubydatei referenziert werden.
 * Neben der eigentlichen Skriptdatei sollte eine Beschreibung für die Funktionen in XML vorliegen.
 * Hierfür muss ein weiteres Attribut "<code>Script-Description</code>" eine XML referenzieren,
 * die nach dem Schema <i>http://www.cuina.byethost12.com/doc/descriptionSchema</i> aufgebaut ist.
 * </p>
 * @author TheWhiteShadow
 */
public class StaticScriptLibrary implements IScriptLibrary
{
	private static final String EXT_FUNCTIONS   = "cuina.script.functions";
    private static final String EXT_INTERFACES  = "cuina.script.interfaces";
    
//  private final CuinaProject project;
    private final HashMap<String, ScriptType> interfaces = new HashMap<String, ScriptType>();
    private final HashMap<String, ClassDefinition> classDefinitions = new HashMap<String, ClassDefinition>();
    private final ClassDefinition GLOBAL_CLASS = new ClassDefinition(null, null);
    
    public StaticScriptLibrary(CuinaProject project)
    {
        classDefinitions.put(null, GLOBAL_CLASS);
//      this.project = project;
        loadScriptDescriptions();
        loadScriptTypes();
    }
    
    public StaticScriptLibrary(File descriptionFile, ScriptType[] types)
    {
        classDefinitions.put(null, GLOBAL_CLASS);
        addDescription(descriptionFile);
        for(ScriptType c : types)
            interfaces.put(c.name, c);
    }
    
    private void loadScriptDescriptions()
    {
        IConfigurationElement[] elements = Platform.getExtensionRegistry().
                getConfigurationElementsFor(EXT_FUNCTIONS);
 
        for(IConfigurationElement conf : elements)
        {
//          try
//          {
//              Bundle plugin = Platform.getBundle(conf.getContributor().getName());
//              this.dataClass = (Class<E>) plugin.loadClass(conf.getAttribute("class"));
                System.out.println("[ScriptLibrary] load description: " + conf.getAttribute("name"));
                String fileName = conf.getAttribute("file");
                if (fileName != null)
                {
                    addDescription(Scripts.getBundleFile(fileName));
                }
                else
                {
                    for(IConfigurationElement e : conf.getChildren())
                    {
                        if ( "class".equals(e.getName()) )
                        {
                            loadClass(e);
                        }
                        else if ( "function".equals(e.getName()) )
                        {
                            loadFunction(e, GLOBAL_CLASS);
                        }
                        else if ( "attribut".equals(e.getName()) )
                        {
                            loadAttribut(e, GLOBAL_CLASS);
                        }
                    }
                }
//          }
//          catch (Exception ex)
//          {
//              ex.printStackTrace();
//          }
        }
    }
 
    private void loadClass(IConfigurationElement clazzElement)
    {
    	ClassDefinition clazz = classDefinitions.get(clazzElement.getAttribute("id"));
        for (IConfigurationElement e : clazzElement.getChildren())
        {
            if ( "function".equals(e.getName()) )
            {
                loadFunction(e, clazz);
            }
            else if ( "attribut".equals(e.getName()) )
            {
            	loadAttribut(e, clazz);
            }
        }
        classDefinitions.put(clazz.id, clazz);
    }
    
    private void loadFunction(IConfigurationElement functionElement, ClassDefinition clazz)
    {
        FunctionDefinition func = new FunctionDefinition();
        func.id = functionElement.getAttribute("id");
        func.label = functionElement.getAttribute("label");
        func.text = functionElement.getAttribute("text");
        func.help = functionElement.getAttribute("help");
        func.returnType = functionElement.getAttribute("return");
        
        for (IConfigurationElement e : functionElement.getChildren())
        {
            ValueDefinition param = new ValueDefinition();
            param.id = e.getAttribute("id");
            param.label = e.getAttribute("label");
            param.type = e.getAttribute("type");
            param.def = e.getAttribute("default");
            func.params.add(param);
        }
        addFunction(func, clazz);
    }
    
    private void loadAttribut(IConfigurationElement e, ClassDefinition clazz)
    {
        ValueDefinition field = new ValueDefinition();
        field.id = e.getAttribute("id");
        field.label = e.getAttribute("label");
        field.type = e.getAttribute("type");
        field.def = e.getAttribute("default");
        addField(field, clazz);
    }
 
    private void loadScriptTypes()
    {
        IConfigurationElement[] elements = Platform.getExtensionRegistry().
                getConfigurationElementsFor(EXT_INTERFACES);
 
        for(IConfigurationElement conf : elements)
        {
            try
            {
				Bundle plugin = Platform.getBundle(conf.getContributor().getName());
				Class clazz = plugin.loadClass(conf.getAttribute("interface"));
				
				String name = conf.getAttribute("name");
				System.out.println("[ScriptLibrary] load interface: " + name);
				
				String module = conf.getAttribute("module");
				
				ScriptType type = new ScriptType(name, clazz, module);
				interfaces.put(clazz.getName(), type);
            }
            catch (ClassNotFoundException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Fügt der Library eine Datei mit Funktionsbeschreibungen hinzu.
     * @param file
     */
    public void addDescription(File file)
    {
        try
        {
            InputStream stream = new FileInputStream(file);
            addObjectDefinition(ScriptDescriptionParser.parse(stream));
            stream.close();
        }
        catch (IOException | SAXException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Fügt der Library eine Datei mit Funktionsbeschreibungen hinzu.
     * @param url
     */
    public void addDescription(URL url)
    {
        try
        {
            InputStream stream = url.openStream();
            addObjectDefinition(ScriptDescriptionParser.parse(stream));
            stream.close();
        }
        catch (IOException | SAXException e)
        {
            e.printStackTrace();
        }
    }
    
    private void addObjectDefinition(ArrayList<ClassDefinition> newDefinitions)
    {
        for (ClassDefinition newDef : newDefinitions)
        {
        	ClassDefinition ownDef = getClassDefinition(newDef.id);
        	if (ownDef == null)
        	{
        		classDefinitions.put(newDef.id, newDef);
        	}
        	else
        	{
	            for (FunctionDefinition func : newDef.methods.values())
	            {
	            	if (ownDef.methods.containsKey(func.id))
	            	{
	            		System.err.println("[ScriptLibrary] Funktions-Definition schon vorhanden: " + newDef.id + "." + func.id);
	            		continue;
	            	}
	                addFunction(func, ownDef);
	            }
	            
	            for (ValueDefinition field : newDef.fields.values())
	            {
	            	if (ownDef.fields.containsKey(field.id))
	              	{
	            		System.err.println("[ScriptLibrary] Field-Definition schon vorhanden: " + newDef.id + "." + field.id);
	            		continue;
	            	}
	                addField(field, ownDef);
	            }
        	}
        }
    }
 
    private void addFunction(FunctionDefinition func, ClassDefinition clazz)
    {
        if (clazz == null) clazz = GLOBAL_CLASS;
        
        clazz.add(func);
    }
    
    private void addField(ValueDefinition field, ClassDefinition clazz)
    {
        if (clazz == null) clazz = GLOBAL_CLASS;
        
        clazz.add(field);
    }
    
    @Override
	public HashMap<String, ClassDefinition> getClassDefinitions()
    {
        return classDefinitions;
    }
    
    @Override
	public ClassDefinition getClassDefinition(String classID)
    {
        return classDefinitions.get(classID);
    }
 
	public ScriptType[] getScriptTypes()
	{
		return interfaces.values().toArray(new ScriptType[interfaces.size()]);
	}
	
	public ScriptType findScriptType(String clazzName)
	{
		return interfaces.get(clazzName);
	}
    
    @Override
	public FunctionDefinition getFunction(String classID, String functionID)
    {
        ClassDefinition clazz = getClassDefinition(classID);
        if (clazz == null) return null;
        
        return clazz.methods.get(functionID);
    }
    
    @Override
	public ArrayList<FunctionDefinition> getFunctions(String returnType)
    {
        ArrayList<FunctionDefinition> resultList = new ArrayList<FunctionDefinition>();
        
        for(ClassDefinition clazz : classDefinitions.values())
        {
            if (clazz.id == null) continue;
            
            for(FunctionDefinition func : clazz.methods.values())
            {
                if (returnType == null || func.returnType != null && func.returnType.equals(returnType))
                {
                    resultList.add(func); 
                }
            }
        }
        
        return resultList;
    }
    
	@Override
	public ValueDefinition getClassVariable(String classID, String id)
	{
        ClassDefinition clazz = getClassDefinition(classID);
        if (clazz == null) return null;
        
        return clazz.fields.get(id);
	}
    
	public static class ScriptType
	{
		public String name;
		public Class clazz;
		public String module;
		
		public ScriptType(String name, Class clazz, String module)
		{
			this.name = name;
			this.clazz = clazz;
			this.module = module;
		}
		
		public String getFullPath()
		{
			return module + "::" + clazz.getSimpleName();
		}
	}
}