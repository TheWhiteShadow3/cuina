package cuina.database.ui;

import cuina.database.DataTable;
import cuina.database.DatabaseObject;
import cuina.database.ui.internal.DataLabelProvider;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

public class DatabaseComboViewer<E extends DatabaseObject> extends ComboViewer
{
	/** NULL-Element. */
	private static Object NULL = new String();
	
	public DatabaseComboViewer(Composite parent, int style)
	{
		super(parent, style);
		setContentProvider(new DatabaseListContentProvider());
		setLabelProvider(new DataLabelProvider());
	}
	
	/**
	 * Gibt das ausgewählte Element zurück oder <code>null</code>, wenn keins ausgewählt ist.
	 * @return ausgewählte Element.
	 */
	public E getSelectedElement()
	{
		Object obj = ((IStructuredSelection) getSelection()).getFirstElement();
		if (obj == NULL) return null;
		return (E) obj;
	}
	
	public void setSelectedElement(Object obj)
	{
		if (obj == null) obj = NULL;
		setSelection(new StructuredSelection(obj));
	}
	
	public void setSelection(DataTable<E> table, String key)
	{
		setTable(table);
		E obj = table.get(key);
		if (obj == null)
			super.setSelection(null);
		else
			super.setSelection(new StructuredSelection(obj));
	}
	
	/**
	 * Setzt die Tabelle als Input für den Viewer.
	 * @param table
	 */
	public void setTable(DataTable<E> table)
	{
		super.setInput(table);
	}
	
	public DataTable<E> getTable()
	{
		return (DataTable<E>) getInput();
	}
	
	private static class DatabaseListContentProvider implements IStructuredContentProvider
	{
		@Override
		public void dispose() {}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
			if (newInput == null) return;
			if (!(newInput instanceof DataTable))
				throw new IllegalArgumentException("input must be a DataTable.");
		}

		@Override
		public Object[] getElements(Object inputElement)
		{
			Object[] tableData = ((DataTable) inputElement).values().toArray();
			Object[] comboData = new Object[tableData.length + 1];
			System.arraycopy(tableData, 0, comboData, 1, tableData.length);
			comboData[0] = NULL;
			return comboData;
		}
	}
}
