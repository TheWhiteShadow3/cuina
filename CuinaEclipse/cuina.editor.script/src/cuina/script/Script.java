package cuina.script;

import cuina.database.DatabaseObject;
import cuina.database.ui.TreeItem;
import cuina.database.ui.tree.TreeRoot;
import cuina.editor.core.CuinaProject;
import cuina.editor.script.Scripts;
import cuina.editor.script.internal.RubyNodeConverter;
import cuina.editor.script.library.StaticScriptLibrary;

public class Script implements DatabaseObject, TreeItem
{
	private static final long serialVersionUID = -6467215823639970856L;
	
	private String key;
	private String name;
	private String interfaceClass;
	private String code;
	
	@Override
	public String getKey()
	{
		return key;
	}
	
	@Override
	public void setKey(String key)
	{
		this.key = key;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getCode()
	{
		return code;
	}
	
	public void setCode(String code)
	{
		this.code = code;
	}

	public String getInterfaceClass()
	{
		return interfaceClass;
	}

	public void setInterfaceClass(String clazz)
	{
		this.interfaceClass = clazz;
	}

	@Override
	public boolean hasChildren()
	{
		return (code == null || code.isEmpty());
	}
	
	@Override
	public Object[] getChildren(TreeRoot root)
	{
		if (code == null || code.isEmpty()) return new Object[0];
		
		CuinaProject project = root.getTable().getDatabase().getProject();
		RubyNodeConverter converter = new RubyNodeConverter(Scripts.getScriptCache(project).getTreeEditor(this));
		converter.setScriptType(project.getService(StaticScriptLibrary.class).findScriptType(getInterfaceClass()));
		converter.createPages();
		return converter.getPageList().toArray();
	}
}
