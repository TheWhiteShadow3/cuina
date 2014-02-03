package cuina.editor.event;

import cuina.editor.core.CuinaProject;
import cuina.event.Trigger;

import org.eclipse.swt.widgets.Composite;

public interface ITriggerEditor
{
	public Trigger getTrigger();
	public void setTrigger(Trigger value);
	public void init(CuinaProject project);
	public void createComponents(Composite parent);
}
