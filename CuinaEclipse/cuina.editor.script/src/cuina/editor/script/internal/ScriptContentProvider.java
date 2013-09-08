package cuina.editor.script.internal;

import cuina.editor.script.internal.RubyNodeConverter.CommandPage;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * ContentProvider für den ScriptEditor.
 * Akzeptiert nur ein Input-Objekt vom Typ CommandPage.
 * @author TheWhiteShadow
 */
public class ScriptContentProvider implements IStructuredContentProvider
{
	private static final Object[] EMPTY = new Object[0];
	
	@Override
	public void dispose() {}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{}

	@Override
	public Object[] getElements(Object element)
	{
		if (element instanceof CommandPage)
		{
			CommandPage page = (CommandPage) element;
			// aktualisiere die Zeilen der Seite
			page.createLines();
			return page.lines.toArray();
		}
		return EMPTY;
	}
}
