package cuina.editor.script.internal;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import cuina.database.DataTable;
import cuina.database.Database;
import cuina.database.ui.DatabaseComboViewer;
import cuina.editor.core.IEditorContext;
import cuina.editor.event.EventRegistry;
import cuina.editor.event.IEventDescriptor;
import cuina.editor.event.ui.ITriggerEditor;
import cuina.editor.script.Scripts;
import cuina.editor.script.internal.ruby.TreeEditor;
import cuina.editor.script.library.IScriptLibrary;
import cuina.editor.script.ruby.ast.DefNode;
import cuina.editor.ui.DefaultComboViewer;
import cuina.editor.ui.WidgetFactory;
import cuina.event.Event;
import cuina.event.Trigger;
import cuina.resource.ResourceException;
import cuina.script.Script;
import cuina.script.ScriptTrigger;

public class ScriptTriggerEditor implements ITriggerEditor
{
	private IEditorContext context;
	private ScriptTrigger trigger;
	
	private DefaultComboViewer<IEventDescriptor> eventViewer;
	private Text inArg;
	private DatabaseComboViewer<Script> keyViewer;
	private DefaultComboViewer<DefNode> mainMethodCombo;
	private boolean update;
	
	@Override
	public Trigger getTrigger()
	{
		trigger.setEvent(eventViewer.getSelectedElement().getEvent());
		trigger.setArgument(parseArgument(inArg.getText()));
		Script script = keyViewer.getSelectedElement();
		trigger.setScript((script != null) ? script.getKey() : null);
		DefNode node = mainMethodCombo.getSelectedElement();
		trigger.setMain((node != null) ? node.getName() : null);
		
		return trigger;
	}

	@Override
	public void setTrigger(Trigger trigger)
	{
		update = true;
		if (trigger == null) trigger = new ScriptTrigger(Event.NEVER, null, null);
		this.trigger = (ScriptTrigger) trigger;
		
		eventViewer.setSelectedElement( EventRegistry.getEventDescriptor(this.trigger.getEvent()) );
		
		Object arg = this.trigger.getArgument();
		inArg.setText(arg != null ? arg.toString() : "");
		keyViewer.setSelection(this.trigger.getScript());
		update = false;
	}

	@Override
	public void init(IEditorContext context)
	{
		this.context = context;
	}

	@Override
	public void createComponents(Composite parent)
	{
		EditorHandler handler = new EditorHandler();
		
		parent.setLayout(new GridLayout(2, false));
		this.eventViewer = WidgetFactory.createComboViewer(
				parent, "Ereignis", EventRegistry.getEventDescriptors(), false);
		eventViewer.getControl().addListener(SWT.Selection, handler);
		
		this.inArg = WidgetFactory.createText(parent, "Argument");
		inArg.addListener(SWT.Modify, handler);
		try
		{
			DataTable<Script> table = context.getCuinaProject().getService(Database.class).loadTable("Script");
			this.keyViewer = WidgetFactory.createDatabaseComboViewer(parent, "Skript", table);
			keyViewer.getControl().addListener(SWT.Selection, handler);
			
			this.mainMethodCombo = WidgetFactory.createComboViewer(parent, "Main", Collections.EMPTY_LIST, false);
			mainMethodCombo.setLabelProvider(
					new ScriptLabelProvider(context.getCuinaProject().getService(IScriptLibrary.class)));
			mainMethodCombo.getControl().addListener(SWT.Selection, handler);
		}
		catch (ResourceException e)
		{
			e.printStackTrace();
		}
	}

	private Object parseArgument(String text)
	{
		if (text.isEmpty()) return null;
		if ("true".equalsIgnoreCase(text)) return Boolean.TRUE;
		if ("false".equalsIgnoreCase(text)) return Boolean.FALSE;
		
		if (Character.isDigit(text.charAt(0))) try
		{
			if (text.indexOf('.') != -1)
				return Float.valueOf(text);
			else
				return Integer.valueOf(text);
		}
		catch(NumberFormatException e) { /* Keine Zahl */ }
		
		return text;
	}
	
	private void updateMainMethodCombo()
	{
		Script script = keyViewer.getSelectedElement();
		if (script != null)
		{
			TreeEditor treeEditor = Scripts.getScriptCache(context.getCuinaProject()).getTreeEditor(script);
			List<DefNode> nodes = ScriptUtil.getClassMethods(treeEditor.getRoot());
			mainMethodCombo.setList(nodes, false);
			
			if (trigger.getMain() != null)
			{
				for(DefNode node : nodes)
				{
					if (node.getName().equals(trigger.getMain()))
					{
						mainMethodCombo.setSelectedElement(node);
						break;
					}
				}
			}
		}
		else
		{
			mainMethodCombo.setList(Collections.EMPTY_LIST, false);
		}
	}
	
	private class EditorHandler implements Listener
	{
		@Override
		public void handleEvent(org.eclipse.swt.widgets.Event event)
		{
			if (event.widget == keyViewer.getControl())
				updateMainMethodCombo();
			
			if(!update) context.fireDataChanged();
		}
	}
}
