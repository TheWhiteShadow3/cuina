package cuina.editor.script.internal;

import cuina.editor.core.CuinaProject;
import cuina.editor.script.internal.ruby.ParseException;
import cuina.editor.script.internal.ruby.RubyParser;
import cuina.editor.script.internal.ruby.RubySource;
import cuina.editor.script.internal.ruby.TreeEditor;
import cuina.editor.script.ruby.ast.RootNode;
import cuina.script.Script;

import java.util.HashMap;

import org.eclipse.core.runtime.Assert;

public class ScriptCache
{
	private final HashMap<String, TreeEditor> cache = new HashMap<String, TreeEditor>();
	private final CuinaProject project;
	
	public ScriptCache(CuinaProject project)
	{
		this.project = project;
	}

	public CuinaProject getProject()
	{
		return project;
	}

	public TreeEditor getTreeEditor(Script script)
	{
		TreeEditor treeEditor = cache.get(script.getKey());
		if (treeEditor == null)
		{
			treeEditor = createTreeEditor(script);
			cache.put(script.getKey(), treeEditor);
		}
		return treeEditor;
	}
	
	private TreeEditor createTreeEditor(Script script)
	{
		RubyParser parser = new RubyParser(RubyParser.MODE_DEFAULT);
		try
		{
			RubySource source = new RubySource(script.getCode(), script.getKey());
			RootNode root = parser.parse(source);
			Assert.isNotNull(root);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return parser.getTreeEditor();
	}
	
	public void clear()
	{
		cache.clear();
	}
}
