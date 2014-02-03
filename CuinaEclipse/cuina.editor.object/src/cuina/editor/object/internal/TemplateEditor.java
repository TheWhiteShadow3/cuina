package cuina.editor.object.internal;

import cuina.database.ui.AbstractDatabaseEditorPart;
import cuina.editor.core.CuinaProject;
import cuina.editor.object.ExtensionEditor;
import cuina.editor.ui.WidgetFactory;
import cuina.object.ObjectData;
import cuina.object.ObjectTemplate;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class TemplateEditor extends AbstractDatabaseEditorPart<ObjectTemplate> implements IExtensionContext
{
	private ObjectTemplate template;
	
	private Text inName;
	private TabFolder extensionFolder;

	private TabItem triggerTab;
	private Button cmdTrAdd;
	private Button cmdTrDel;

	private java.util.List<TabItem> extensionTabs;
	private java.util.List<ExtensionEditor> editors;

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
		
		java.util.List<ExtensionDescriptor> descriptors = Activator.getExtensionDescriptors();
		
		for (int i = 0; i < descriptors.size(); i++)
		{
			ExtensionDescriptor desc = descriptors.get(i);
			
			TabItem tab = new TabItem(extensionFolder, SWT.NONE);
			tab.setText(desc.getName());
			Composite tabPanel = new Composite(extensionFolder, SWT.NONE);
			tab.setControl(tabPanel);
			
			try
			{
				ExtensionEditor editor = (ExtensionEditor) desc.getEditor().newInstance();
				editor.init(this);
				editor.createComponents(tabPanel);
				editor.setData(getObjectData().extensions.get(desc.getName()));
				
				editors.add(editor);
				extensionTabs.add(tab);
			}
			catch (InstantiationException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
		
		this.triggerTab = new TabItem(extensionFolder, SWT.NONE);
		triggerTab.setText("Trigger");
		Composite tabPanel = new Composite(extensionFolder, SWT.NONE);
		tabPanel.setLayout(new GridLayout(3, false));
		triggerTab.setControl(tabPanel);
		
		List list = new List(tabPanel, SWT.V_SCROLL);
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 3));
		
		this.cmdTrAdd = new Button(tabPanel, SWT.PUSH);
		cmdTrAdd.setText("Neu");
		
		this.cmdTrDel = new Button(tabPanel, SWT.PUSH);
		cmdTrDel.setText("Entf");
	}
	
	@Override
	public ObjectData getObjectData()
	{
		return template.sourceObject;
	}

	@Override
	public CuinaProject getCuinaProject()
	{
		return super.getCuinaProject();
	}

	@Override
	public void setFocus()
	{
		inName.setFocus();
	}
}
