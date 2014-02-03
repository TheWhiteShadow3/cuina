package cuina.database.ui.properties;

import cuina.database.DataTable;
import cuina.database.DatabaseObject;
import cuina.database.ui.DatabaseComboViewer;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class DataReferencePropertyDescriptor<T extends DatabaseObject> extends PropertyDescriptor
{
	private DataTable<T> table;

	public DataReferencePropertyDescriptor(Object id, DataTable<T> table, String displayName)
	{
		super(id, displayName);
		this.table = table;
	}
	
	@Override
	public CellEditor createPropertyEditor(Composite parent)
	{
		return new DataReferenceCellEditor(parent, table);
	}
	
	private static class DataReferenceCellEditor<T extends DatabaseObject> extends CellEditor
	{
		private DatabaseComboViewer<T> viewer;
		private DataTable<T> table;
		
		public DataReferenceCellEditor(Composite parent, DataTable<T> table)
		{
			super();
			this.table = table;
			create(parent);
		}
		
		@Override
		protected Control createControl(Composite parent)
		{
			this.viewer = new DatabaseComboViewer<T>(parent, SWT.NONE);
			viewer.setTable(table);
			return viewer.getControl();
		}

		@Override
		protected T doGetValue()
		{
			return viewer.getSelectedElement();
		}

		@Override
		protected void doSetFocus()
		{
			viewer.getControl().setFocus();
		}

		@Override
		protected void doSetValue(Object value)
		{
			viewer.setSelectedElement(value);
		}
	}
}
