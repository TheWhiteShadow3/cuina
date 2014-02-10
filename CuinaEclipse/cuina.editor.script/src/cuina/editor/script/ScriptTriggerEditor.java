package cuina.editor.script;

import cuina.editor.core.CuinaProject;
import cuina.editor.event.ITriggerEditor;
import cuina.event.Trigger;
import cuina.script.ScriptTrigger;

import org.eclipse.swt.widgets.Composite;

public class ScriptTriggerEditor implements ITriggerEditor
{
	private ScriptTrigger trigger;
	
	@Override
	public Trigger getTrigger()
	{
		return trigger;
	}

	@Override
	public void setTrigger(Trigger trigger)
	{
		this.trigger = (ScriptTrigger) trigger;
	}

	@Override
	public void init(CuinaProject project)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void createComponents(Composite parent)
	{
		// TODO Auto-generated method stub

	}

}
