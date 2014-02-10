package cuina.editor.model.internal.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import cuina.editor.model.internal.ModelExtensionEditor;
import cuina.editor.object.IExtensionContext;
import cuina.editor.object.ObjectPropertyPage;
 
public class ModelPropertyPage extends ObjectPropertyPage implements IExtensionContext
{
	private ModelExtensionEditor editor;
	
	public ModelPropertyPage()
	{
		this.editor = new ModelExtensionEditor();
		editor.init(this);
	}

	@Override
	protected Control createContents(Composite parent)
	{
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        
		editor.createComponents(composite);
        return composite;
	}

	@Override
    public boolean performOk()
    {
		return editor.performOk();
    }

	@Override
	public void fireDataChanged() {}
}
