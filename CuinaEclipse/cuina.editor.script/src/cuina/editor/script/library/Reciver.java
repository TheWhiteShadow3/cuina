package cuina.editor.script.library;

import cuina.editor.script.library.TreeLibrary.LibraryTree;
import cuina.editor.script.ruby.ast.ArgNode;
import cuina.editor.script.ruby.ast.AsgNode;
import cuina.editor.script.ruby.ast.DefNode;

import java.util.HashMap;

public class Reciver implements Definition
{
	LibraryTree tree;
	public final String id;
	public final HashMap<String, TreeDefinition> entries = new HashMap<String, TreeDefinition>();
	
	Reciver(String name)
	{
		this.id = name;
	}
	
	@Override
	public String getID()
	{
		return id;
	}

	@Override
	public String getLabel()
	{
		return id == null ? TreeLibrary.DEFAULT_GROUP : id;
	}

	@Override
	public Definition getParent()
	{
		return null;
	}
	
	public void add(TreeDefinition def)
	{
		entries.put(def.getID(), def);
	}
	
	public void addVariable(AsgNode node)
	{
		add(new Variable(this, node));
	}
	
	public void addVariable(ArgNode node, FunctionDefinition def)
	{
		add(new Variable(this, node, def));
	}
	
	public void addVariable(ValueDefinition def)
	{
		add(new Variable(this, def));
	}
	
	public void addFunction(DefNode node)
	{
		add(new Function(this, node));
	}
	
	public void addFunction(FunctionDefinition def)
	{
		add(new Function(this, def));
	}
	
	@Override
	public String getType()
	{
		return "";
	}
	
	@Override
	public String toString()
	{
		return getLabel();
	}
}