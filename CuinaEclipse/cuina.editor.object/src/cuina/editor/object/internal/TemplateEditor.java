package cuina.editor.object.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import cuina.database.ui.AbstractDatabaseEditorPart;
import cuina.editor.core.CuinaProject;
import cuina.editor.event.ITriggerDescriptor;
import cuina.editor.object.ExtensionEditor;
import cuina.editor.object.IExtensionEditorContext;
import cuina.editor.object.ObjectAdapter;
import cuina.editor.ui.DefaultComboViewer;
import cuina.editor.ui.WidgetFactory;
import cuina.event.Trigger;
import cuina.object.ObjectTemplate;

public class TemplateEditor extends AbstractDatabaseEditorPart<ObjectTemplate> implements IExtensionEditorContext
{
	private ObjectTemplate template;
	private ObjectAdapter adapter;
	
	private Text inName;
	private TabFolder extensionFolder;

	private TabItem triggerTabItem;
	private TriggerTab triggerTab;

	private final ArrayList<EditorTab> extensionTabs = new ArrayList<EditorTab>();
	private Map<String, java.util.List<ExtensionDescriptor>> descriptors;
	DefaultComboViewer<ITriggerDescriptor> triggerCombo;

	@Override
	protected void init(ObjectTemplate template)
	{
		this.template = template;
		this.adapter = new ObjectAdapter(getCuinaProject(), template);
	}

	@Override
	protected boolean applySave()
	{
		clearExtensions();
		
		for(EditorTab tab : extensionTabs)
		{
			if (!tab.applySave()) return false;
		}
		triggerTab.saveCurrentTrigger();
		
		return true;
	}

	@Override
	public void createPartControl(Composite parent)
	{
		parent.setLayout(new GridLayout(2, false));
		this.inName = WidgetFactory.createText(parent, "Name");
		inName.setText(template.getName());
		
		this.extensionFolder = new TabFolder(parent, SWT.NONE);
		extensionFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		this.descriptors = Activator.getExtensionDescriptors();
		
		for (String id : descriptors.keySet())
		{
			EditorTab tab = new EditorTab(extensionFolder, id);

			extensionTabs.add(tab);
			Object ext = getExtension(id);
			if (ext != null)
			{
				tab.setEditor(ext.getClass());
			}
		}
		
		this.triggerTabItem = new TabItem(extensionFolder, SWT.NONE);
		triggerTabItem.setText("Trigger");
		Composite tabPanel = new Composite(extensionFolder, SWT.NONE);
		tabPanel.setLayout(new GridLayout(4, false));
		triggerTabItem.setControl(tabPanel);
		
		createTriggerTab(tabPanel);
	}
	
	private void createTriggerTab(Composite parent)
	{
		this.triggerTab = new TriggerTab(this);
		triggerTab.createComponents(parent);
	}

	private void clearExtensions()
	{
		template.sourceObject.extensions.clear();
	}

	public Object getExtension(String key)
	{
		Map<String, Object> extensions = template.sourceObject.extensions;
		if (extensions == null) return null;
		
		return extensions.get(key);
	}
	
	public Object setExtension(String key, Object value)
	{
		Map<String, Object> extensions = template.sourceObject.extensions;
		if (extensions == null)
		{
			extensions = new HashMap<String, Object>();
			template.sourceObject.extensions = extensions;
		}
		
		return extensions.put(key, value);
	}

	public List<Trigger> getTriggers()
	{
		return template.sourceObject.triggers;
	}
	
	@Override
	public CuinaProject getCuinaProject()
	{
		return super.getCuinaProject();
	}
	
	@Override
	public ObjectAdapter getObjectAdapter()
	{
		return adapter;
	}
	
	@Override
	public void fireDataChanged()
	{
		setDirty(true);
	}
	
	@Override
	public void setErrorMessage(String message)
	{
		
	}
	
	@Override
	public void setFocus()
	{
		inName.setFocus();
	}
	
	private class EditorTab implements Listener
	{
//		private String id;
		private Composite tabPanel;
		private java.util.List<ExtensionDescriptor> list;
		private DefaultComboViewer<ExtensionDescriptor> editorViewer;
		
		private ExtensionEditor editor;
		private Composite editorPanel;
		
		public EditorTab(TabFolder folder, String id)
		{
//			this.id = id;
			this.list = descriptors.get(id);
			
			TabItem tab = new TabItem(folder, SWT.NONE);
			tab.setText(id);
			this.tabPanel = new Composite(folder, SWT.NONE);
			tabPanel.setLayout(new GridLayout(2, false));
			tab.setControl(tabPanel);
			
			this.editorViewer = WidgetFactory.createComboViewer(tabPanel, "Typ", list, true);
			editorViewer.getControl().addListener(SWT.Selection, this);
		}
		
//		public String getID()
//		{
//			return id;
//		}
		
		public boolean applySave()
		{
			if (editor == null) return true;
			
			return editor.performOk();
		}
		
		private void setEditor(Class dataClass)
		{
			for (ExtensionDescriptor desc : list)
			{
				if (desc.getDataClassName().equals(dataClass.getName()))
				{
					setEditor(desc);
					editorViewer.setSelectedElement(desc);
				}
			}
		}
		
		private void setEditor(ExtensionDescriptor descriptor)
		{
			if (editor != null)
			{
				editor.dispose();
				editor = null;
				editorPanel.dispose();
			}
			
			if (descriptor != null)
			{
				this.editorPanel = new Composite(tabPanel, SWT.BORDER);
				editorPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
				try
				{
					this.editor = (ExtensionEditor) descriptor.getEditor().newInstance();
					editor.init(TemplateEditor.this);
					editor.createComponents(editorPanel);
				}
				catch (InstantiationException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
			tabPanel.layout(true, true);
		}

		@Override
		public void handleEvent(Event event)
		{
			setEditor(editorViewer.getSelectedElement());
		}
	}
}
