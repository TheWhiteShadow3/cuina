package cuina.editor.event;

import cuina.database.NamedItem;
import cuina.editor.event.ui.ITriggerEditor;
import cuina.event.Trigger;

import org.eclipse.swt.graphics.Image;

public interface ITriggerDescriptor extends NamedItem
{
	public abstract Image getImage();

	public abstract String getDescription();

	public abstract Class<? extends ITriggerEditor> getEditorClass();

	public abstract Class<? extends Trigger> getTriggerClass();
}
