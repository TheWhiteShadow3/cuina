package cuina.database.ui;

import cuina.database.DatabaseObject;

import org.eclipse.swt.widgets.Composite;

@Deprecated
public interface Toolbox<E extends DatabaseObject>
{
	public void createToolboxControl(Composite parent, DataEditorPage<E> dataEditorPage);
}
