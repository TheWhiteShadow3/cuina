package cuina.editor.eventx.internal;

import org.eclipse.swt.widgets.Shell;

import cuina.editor.core.CuinaProject;

public interface CommandEditorContext
{
	public Shell getShell();
	public CuinaProject getCuinaProject();
}
