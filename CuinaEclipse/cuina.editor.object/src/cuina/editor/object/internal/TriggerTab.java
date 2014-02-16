package cuina.editor.object.internal;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import cuina.editor.event.EventRegistry;
import cuina.editor.event.ITriggerDescriptor;
import cuina.editor.event.ui.ITriggerEditor;
import cuina.editor.object.IExtensionEditorContext;
import cuina.editor.ui.DefaultComboViewer;
import cuina.editor.ui.WidgetFactory;
import cuina.event.Trigger;

public class TriggerTab implements Listener, ISelectionChangedListener
{
	private IExtensionEditorContext context;
	
	private ITriggerDescriptor currentTriggerDescription;
	private Trigger selectedTrigger;
	private Button cmdTrAdd;
	private Button cmdTrDel;
	
	private DefaultComboViewer<ITriggerDescriptor> triggerCombo;
	private Composite triggerPanel;
	private ListViewer triggerList;
	private Composite triggerEditorPanel;
	private ITriggerEditor triggerEditor;
	private boolean update;

	public TriggerTab(IExtensionEditorContext context)
	{
		this.context = context;
	}
	
	private List<Trigger> getTriggers()
	{
		return context.getObjectAdapter().getObject().triggers;
	}
	
	public void createComponents(Composite parent)
	{
		this.triggerList = new ListViewer(parent, SWT.V_SCROLL | SWT.BORDER);
		triggerList.setContentProvider(ArrayContentProvider.getInstance());
		triggerList.setLabelProvider(getTriggerLabelProvider());
		triggerList.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 3));
		triggerList.addSelectionChangedListener(this);
		triggerList.setInput(getTriggers());
		
		this.cmdTrAdd = WidgetFactory.createButton(parent, "Hinzufügen", SWT.PUSH);
		cmdTrAdd.addListener(SWT.Selection, this);
		
		this.cmdTrDel = WidgetFactory.createButton(parent, "Entfernen", SWT.PUSH);
		cmdTrDel.addListener(SWT.Selection, this);
		
		this.triggerPanel = new Composite(parent, SWT.BORDER);
		triggerPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		triggerPanel.setLayout(new GridLayout(2, false));
		
		this.triggerCombo = WidgetFactory.createComboViewer(
				triggerPanel, "Typ", EventRegistry.getTriggerDescriptors(), false);
		triggerCombo.addSelectionChangedListener(this);
		triggerCombo.getControl().setEnabled(false);
	}

	@Override
	public void handleEvent(Event event)
	{
		if (update) return;
		update = true;
		
		if (event.widget == cmdTrAdd)
		{
			addTrigger();
		}
		else if (event.widget == cmdTrDel)
		{
			removeTrigger();
		}
		update = false;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event)
	{
		if (update) return;
		update = true;
		
		if (event.getSource() == triggerCombo)
		{
			ITriggerDescriptor desc = triggerCombo.getSelectedElement();
			Trigger trigger = getSelectedTrigger();
			if (trigger != null && desc != null && desc.getTriggerClass() != trigger.getClass()) trigger = null;
			
			setTriggerEditor(desc, trigger);
			updateCurrentTrigger((trigger != null) ? trigger : triggerEditor.getTrigger());
		}
		else if (event.getSource() == triggerList)
		{
			saveCurrentTrigger();
			Trigger trigger = getSelectedTrigger();
			if (trigger != null)
			{
				setTriggerEditor(EventRegistry.getTriggerDescriptor(trigger), trigger);
				this.selectedTrigger = trigger;
			}
			else
			{
				removeTriggerEditor();
			}
		}
		update = false;
	}
	
	private void removeTriggerEditor()
	{
		if (triggerEditor == null) return;
		
		triggerEditorPanel.dispose();
		triggerPanel.layout(true, true);
		triggerEditor = null;
	}

	private void addTrigger()
	{
		saveCurrentTrigger();
		selectedTrigger = null;
		List<ITriggerDescriptor> descriptors = EventRegistry.getTriggerDescriptors();
		if (descriptors.size() > 0)
		{
			setTriggerEditor(descriptors.get(0), null);
			updateCurrentTrigger(triggerEditor.getTrigger());
		}
		else
		{
			removeTriggerEditor();
		}
	}
	
	private void removeTrigger()
	{
		if (selectedTrigger == null) return;
		
		getTriggers().remove(selectedTrigger);
		context.fireDataChanged();
		selectedTrigger = null;
		triggerCombo.getControl().setEnabled(false);
		triggerList.refresh();
		removeTriggerEditor();
	}
	
	private void setTriggerEditor(ITriggerDescriptor desc, Trigger trigger)
	{
		if (desc == currentTriggerDescription && triggerEditor != null)
		{
			if (trigger != null && trigger == selectedTrigger) return;
			
			triggerEditor.setTrigger(trigger);
			return;
		}
		this.currentTriggerDescription = desc;
		
		removeTriggerEditor();
		if (desc != null)
		{
			triggerCombo.setSelectedElement(desc);
			triggerCombo.getControl().setEnabled(true);
			triggerCombo.getControl().setFocus();
			Class editorClass = desc.getEditorClass();
			if (editorClass != null) try
			{
				triggerEditorPanel = new Composite(triggerPanel, SWT.NONE);
				triggerEditorPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
				
				triggerEditor = (ITriggerEditor) editorClass.newInstance();
				triggerEditor.init(context);
				triggerEditor.createComponents(triggerEditorPanel);
				triggerEditor.setTrigger(trigger);
				triggerPanel.layout(true, true);
			}
			catch(IllegalAccessException | InstantiationException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void updateCurrentTrigger(Trigger trigger)
	{
		Assert.isNotNull(trigger);
		if (selectedTrigger != trigger)
		{
			List<Trigger> triggers = getTriggers();
			int index = triggers.indexOf(selectedTrigger);
			if (index == -1)
				triggers.add(trigger);
			else
				triggers.set(index, trigger);
			
			this.selectedTrigger = trigger;
			triggerList.refresh();
			triggerList.setSelection(new StructuredSelection(trigger));
			context.fireDataChanged();
		}
		else
		{
			triggerList.refresh();
		}
	}
	
	public void saveCurrentTrigger()
	{
		if (triggerEditor == null) return;
		
		updateCurrentTrigger(triggerEditor.getTrigger());
	}
	
	public Trigger getSelectedTrigger()
	{
		return (Trigger) ((IStructuredSelection) triggerList.getSelection()).getFirstElement();
	}
	
	private LabelProvider getTriggerLabelProvider()
	{
		return new LabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				if (element instanceof Trigger)
				{
					Trigger trigger = (Trigger) element;
					ITriggerDescriptor desc = EventRegistry.getTriggerDescriptor(trigger);
					String name = (desc != null) ? desc.getName() : trigger.getClass().getSimpleName();
					
					return name + " (" + (trigger).getEvent().getName() + ')';
				}
				return super.getText(element);
			}
		};
	}
}
