package cuina.editor.script.internal.dialog;

import cuina.database.DatabasePlugin;
import cuina.editor.core.CuinaProject;
import cuina.editor.script.Scripts;
import cuina.editor.script.internal.ScriptPosition;
import cuina.editor.script.internal.ScriptSelection;
import cuina.editor.script.internal.ScriptUtil;
import cuina.editor.script.internal.ruby.ParseException;
import cuina.editor.script.internal.ruby.RubyParser;
import cuina.editor.script.internal.ruby.RubySource;
import cuina.editor.script.internal.ruby.TreeEditor;
import cuina.editor.script.library.StaticScriptLibrary;
import cuina.editor.script.library.StaticScriptLibrary.ScriptType;
import cuina.editor.script.library.TreeLibrary;
import cuina.editor.script.ruby.ast.ClassNode;
import cuina.editor.script.ruby.ast.DefNode;
import cuina.editor.script.ruby.ast.Node;
import cuina.script.MainScript;

import java.io.File;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.widgets.Shell;

public class DialogTester implements ScriptDialogContext
{
	private StaticScriptLibrary library;
	private TreeEditor treeEditor;
	private ScriptSelection selection;
	private TreeLibrary treeLibrary;
	
	private DialogTester(String statement) throws ParseException
	{
		ScriptType[] types = new ScriptType[] {new ScriptType("GameScript", MainScript.class, "Cuina")};
		this.library = new StaticScriptLibrary(new File("func-desc.xml"), types);
		this.treeLibrary = new TreeLibrary(library);
		createScript(statement);
		initSelection();
		openDialog();
	}

	private void createScript(String statement) throws ParseException
	{
		String script =
				"class Script_Test\n" +
					"def main(sandra, susi)\n" +
						statement + "\n" +
					"end\n" +
				"end";
		
		RubyParser parser = new RubyParser(RubyParser.MODE_DEFAULT);
		parser.parse(new RubySource(script, null));
		this.treeEditor = parser.getTreeEditor();
		this.treeLibrary.setRoot(treeEditor.getRoot());
	}
	
	private void initSelection()
	{
		ClassNode node = ScriptUtil.getScriptClass(treeEditor.getRoot());
		DefNode mainNode = (DefNode) node.getChild(0);
		this.selection = new ScriptSelection(null, mainNode, 0);
	}
	
	private void openDialog()
	{
		ScriptPosition pos = selection.getPosition();
		CommandDialog dialog = new CommandDialog(this, pos.getNode(), "Test-Dialog");
		dialog.open();
		System.out.println(dialog.getNode());
	}
	
	public static void main(String[] args)
	{
		new DatabasePlugin(); // Dummy
		
		final IPreferenceStore store = new PreferenceStore()
		{
			
		};
		
		new Scripts()
		{
			@Override
			public IPreferenceStore getPreferenceStore()
			{
				return store;
			}
		};
		
		String statement = "sandra.kiss(susi)";
		try
		{
			new DialogTester(statement);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void valueChanged(Object source, Node node)
	{
		System.out.println("valueChanged: " + node);
	}

	@Override
	public TreeEditor getTreeEditor()
	{
		return treeEditor;
	}

	@Override
	public TreeLibrary getTreeLibrary()
	{
		return treeLibrary;
	}

	@Override
	public ScriptSelection getSelection()
	{
		return selection;
	}

	@Override
	public CuinaProject getCuinaProject()
	{
		return null;
	}

	@Override
	public Shell getShell()
	{
		return null;
	}

	@Override
	public Node getNode()
	{
		return selection.getPosition().getNode();
	}
	
	@Override
	public ScriptPosition getPosition()
	{
		return getSelection().getPosition();
	}
}
