package cuina.editor.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorPart;

public interface EditorContextChangeListener
{
	public void editorContextChange(IEditorPart part, IProject project);
}