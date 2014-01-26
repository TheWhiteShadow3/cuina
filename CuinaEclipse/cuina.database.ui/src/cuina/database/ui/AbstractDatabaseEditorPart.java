package cuina.database.ui;

import cuina.database.DataTable;
import cuina.database.DatabaseInput;
import cuina.database.DatabaseObject;
import cuina.database.DatabasePlugin;
import cuina.editor.core.CuinaProject;
import cuina.editor.core.ObjectUtil;
import cuina.resource.ResourceException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public abstract class AbstractDatabaseEditorPart<E extends DatabaseObject> extends EditorPart
{
	private CuinaProject project;
	private DataTable<E> table;
	private E data;
	private boolean dirty;
	
	@Override
	public void doSave(IProgressMonitor monitor)
	{
		if (!applySave()) return;
		try
		{
			table.update(data.getKey(), data);
			if (table.getDatabase().saveTable(table))
			{
				setDirty(false);
			}
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
			this.table = (DataTable<E>) DBInput.getTable();
			this.data = (E) ObjectUtil.clone(DBInput.getData());
			
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
	protected DataTable<E> getTable()
	{
		return table;
	}
	
	protected IFile getTableFile()
	{
		return DatabasePlugin.getTableFile(table);
	}

	/**
	 * Setzt das Datenbank-Objekt.
	 * @param obj Das Objekt.
	 */
	protected abstract void init(E obj);
	
	protected abstract boolean applySave();
}
