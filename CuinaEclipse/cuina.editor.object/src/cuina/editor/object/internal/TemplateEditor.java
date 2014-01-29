package cuina.editor.object.internal;

import cuina.database.ui.AbstractDatabaseEditorPart;
import cuina.editor.ui.WidgetFactory;
import cuina.object.ObjectTemplate;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;

public class TemplateEditor extends AbstractDatabaseEditorPart<ObjectTemplate>
{
	private ObjectTemplate template;
	
	private Text inName;
	private TabFolder extensionFolder;

	@Override
	protected void init(ObjectTemplate template)
	{
		this.template = template;
	}

	@Override
	protected boolean applySave()
	{
		return true;
	}

	@Override
	public void createPartControl(Composite parent)
	{
		parent.setLayout(new GridLayout(2, false));
		this.inName = WidgetFactory.createText(parent, "Name");
		
		this.extensionFolder = new TabFolder(parent, SWT.NONE);
		extensionFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
	}

	@Override
	public void setFocus()
	{
		inName.setFocus();
	}
}
