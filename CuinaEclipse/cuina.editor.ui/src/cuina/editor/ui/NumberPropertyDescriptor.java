package cuina.editor.ui;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class NumberPropertyDescriptor extends PropertyDescriptor
{
	public NumberPropertyDescriptor(Object id, String displayName)
	{
		super(id, displayName);
	}

	@Override
	public CellEditor createPropertyEditor(Composite parent)
	{
        CellEditor editor = new TextCellEditor(parent);
        if (getValidator() != null)
        {
			editor.setValidator(getValidator());
		}
        return editor;
	}

	@Override
	protected ICellEditorValidator getValidator()
	{
		return new NumberValidator();
	}
}
