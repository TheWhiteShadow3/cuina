package cuina.database.ui;

import cuina.database.DatabaseObject;

import org.eclipse.swt.widgets.Composite;

public interface DataEditorPage<E extends DatabaseObject>
{
	public void setValue(E obj);
	public void setChildValue(Object obj);
	public E getValue();

	public void createEditorPage(Composite parent, IDatabaseEditor context);
}
