package cuina.editor.object.internal.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import cuina.editor.object.ObjectPropertyPage;
import cuina.editor.object.internal.MaskExtensionEditor;

public class MaskPropertyPage extends ObjectPropertyPage
{
	private MaskExtensionEditor editor;
	
	public MaskPropertyPage()
	{
		this.editor = new MaskExtensionEditor();
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
}
