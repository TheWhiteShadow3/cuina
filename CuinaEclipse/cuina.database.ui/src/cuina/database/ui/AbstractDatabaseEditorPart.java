package cuina.database.ui;

import cuina.database.DataTable;
import cuina.database.DatabaseInput;
import cuina.database.DatabaseObject;
import cuina.editor.core.CuinaProject;
import cuina.resource.ResourceException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public abstract class AbstractDatabaseEditorPart extends EditorPart
{
	private CuinaProject project;
	private DataTable table;
	private DatabaseObject data;
	private boolean dirty;
	
	@Override
	public void doSave(IProgressMonitor monitor)
	{
		if (!applySave()) return;
		try
		{
			table.getDatabase().saveTable(table);
			dirty = false;
		}
		catch (ResourceException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	@Override
	public void doSaveAs()
	{
		doSave(null);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		if (!(input instanceof DatabaseInput))
			throw new PartInitException("Input must be an instance of DatabaseInput.");
		
		DatabaseInput DBInput = (DatabaseInput) input;
		try
		{
			this.project = DBInput.getCuinaProject();
			this.table = DBInput.getTable();
			this.data = DBInput.getData();
			
			setSite(site);
			setInput(input);
			
			setPartName(table.getName() + " - " + data.getName());
			init(data);
		}
		catch (Exception e)
		{
			throw new PartInitException(e.getMessage(), e);
		}
	}
	
	@Override
	public boolean isDirty()
	{
		return dirty;
	}
	
	protected void setDirty(boolean value)
	{
		dirty = value;
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}
	
	protected CuinaProject getCuinaProject()
	{
		return project;
	}
	
	/**
	 * Gibt die Tabelle zurück.
	 * @return die Tabelle.
	 */
	protected DataTable getTable()
	{
		return table;
	}

	/**
	 * Setzt das Datenbank-Objekt.
	 * @param obj Das Objekt.
	 */
	protected abstract void init(DatabaseObject obj);
	
	protected abstract boolean applySave();
}
