package cuina.database.ui;

import cuina.database.DataTable;
import cuina.database.DatabaseObject;
import cuina.editor.core.CuinaProject;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;

/**
 * Handlet Daten-Change Events.
 * @author TheWhiteShadow
 */
public interface IDatabaseEditor
{
	public static final String ID = "cuina.editor.editors.DatabaseEditor";
	
	public void addDataChangeListener(TreeListener l);
	public void removeDataChangeListener(TreeListener l);
	public CuinaProject getProject();
	public DataTable getTable();
	public IEditorInput getEditorInput();
	public IEditorSite getEditorSite();
	public void fireDataChanged(Object source, DatabaseObject obj);
}
