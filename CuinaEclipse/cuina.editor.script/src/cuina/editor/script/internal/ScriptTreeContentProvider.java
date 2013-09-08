package cuina.editor.script.internal;

import cuina.database.DataTable;
import cuina.editor.core.CuinaProject;
import cuina.editor.script.Scripts;
import cuina.editor.script.internal.RubyNodeConverter.CommandPage;
import cuina.editor.script.library.StaticScriptLibrary;
import cuina.script.Script;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * ContentProvider für eine Baumansicht der Skripte im Viewer.
 * Input ist per Definition die DataTable.
 * @author TheWhiteShadow
 */
public class ScriptTreeContentProvider implements ITreeContentProvider
{
	private static final Object[] EMPTY = new Object[0];
	private DataTable<Script> table;
	private CuinaProject project;
	
	@Override
	public void dispose() {}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		this.table = (DataTable<Script>) newInput;
		this.project = table.getDatabase().getProject();
	}

	@Override
	public Object[] getElements(Object element)
	{
		return getChildren(element);
	}

	@Override
	public Object[] getChildren(Object element)
	{
		if (element instanceof Script)
		{
			Script script = (Script) element;
			RubyNodeConverter converter = new RubyNodeConverter(Scripts.getScriptCache(project).
					getTreeEditor(script));
			converter.setScriptType(project.getService(StaticScriptLibrary.class).
					findScriptType(script.getInterfaceClass()));
			
			converter.createPages();
			return converter.getPageList().toArray();
		}
		
		if (element instanceof CommandPage)
		{
			CommandPage page = (CommandPage) element;
			// aktualisiere die Zeilen der Seite
			page.createLines();
			return page.lines.toArray();
		}
		return EMPTY;
	}

	@Override
	public Object getParent(Object element)
	{
		return null;
	}

	@Override
	public boolean hasChildren(Object element)
	{
		if (element instanceof Script)
		{
			String code = ((Script) element).getCode();
			return (code != null && code.length() > 0);
		}
		if (element instanceof CommandPage)
			return false; // Zeige die Code-Zeilen im Viewer nicht an.
		return false;
	}
}
