package cuina.editor.movement.internal.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import cuina.editor.movement.internal.MotorExtensionEditor;
import cuina.editor.object.IExtensionContext;
import cuina.editor.object.ObjectPropertyPage;

public class MotorPropertyPage extends ObjectPropertyPage implements IExtensionContext
{
	private MotorExtensionEditor editor;
	
	public MotorPropertyPage()
	{
		this.editor = new MotorExtensionEditor();
		editor.init(this);
	}
	
	@Override
	protected Control createContents(Composite parent)
	{
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        
		editor.createComponents(composite);
        return parent;
	}

	@Override
    public boolean performOk()
    {
		return editor.performOk();
    }

	@Override
	public void fireDataChanged() {}
}
