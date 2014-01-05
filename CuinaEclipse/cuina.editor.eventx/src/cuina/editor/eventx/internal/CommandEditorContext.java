package cuina.editor.eventx.internal;

import org.eclipse.swt.widgets.Shell;

import cuina.editor.core.CuinaProject;
import cuina.eventx.Command;

public interface CommandEditorContext
{
	public Shell getShell();
	public CuinaProject getCuinaProject();
	
	/**
	 * Fügt ein neuen Befehl vor der Auswahl ein.
	 * Wenn kein Element ausgewält ist, wird der Befehl ans Ende der Liste angefügt.
	 * @param cmd Der Befehl.
	 */
	public void addCommand(Command cmd);
}