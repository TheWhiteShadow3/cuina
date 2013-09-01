package cuina.editor.script.internal;

import cuina.editor.script.internal.RubyNodeConverter.CommandPage;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * ContentProvider für den ScriptEditor.
 * Akzeptiert ein Input-Objekt vom Typ CommandPage.
 * @author TheWhiteShadow
 */
public class ScriptContentProvider implements IStructuredContentProvider
{
	private static final Object[] EMPTY = new Object[0];
	private RubyNodeConverter converter;
	
	public ScriptContentProvider(RubyNodeConverter converter)
	{
		this.converter = converter;
	}
	
	@Override
	public void dispose() {}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{}

	@Override
	public Object[] getElements(Object input)
	{
		if (input instanceof CommandPage)
		{
			CommandPage page = (CommandPage) input;
			// aktualisiere die Zeilen der Seite
			converter.createLines(page);
			return page.lines.toArray();
		}
		return EMPTY;
	}
}
