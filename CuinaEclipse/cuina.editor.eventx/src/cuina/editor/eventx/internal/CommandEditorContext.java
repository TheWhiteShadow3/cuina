package cuina.editor.eventx.internal;

import org.eclipse.swt.widgets.Shell;

import cuina.editor.core.CuinaProject;

public interface CommandEditorContext
{
	public Shell getShell();
	
	/**
	 * Gibt das Cuina-Projekt zurück, für das der Editor agiert.
	 * @return Das Cuina-Projekt.
	 */
	public CuinaProject getCuinaProject();

	/**
	 * Gibt die Command-Library zurück.
	 * @return Die Command-Library.
	 */
	public CommandLibrary getCommandLibrary();
}
