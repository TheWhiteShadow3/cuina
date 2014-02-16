package cuina.editor.event.ui;

import org.eclipse.swt.widgets.Composite;

import cuina.editor.core.IEditorContext;
import cuina.event.Trigger;

public interface ITriggerEditor
{
	public Trigger getTrigger();
	public void setTrigger(Trigger value);
	public void init(IEditorContext context);
	public void createComponents(Composite parent);
}
