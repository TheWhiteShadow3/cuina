package cuina.editor.gui.internal;

import cuina.database.ui.DataEditorPage;
import cuina.database.ui.Toolbox;
import cuina.editor.gui.internal.provider.WidgetLibaryContentProvider;
import cuina.editor.gui.internal.provider.WidgetLibraryLabelProvider;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class WidgetToolbox implements Toolbox
{
	private DataEditorPage editorPage;
	private TableViewer viewer;
	
	@Override
	public void createToolboxControl(Composite parent, DataEditorPage dataEditorPage)
	{
		this.editorPage = dataEditorPage;
		this.viewer = new TableViewer(parent, SWT.FULL_SELECTION);
		viewer.setContentProvider(new WidgetLibaryContentProvider());
		viewer.setLabelProvider(new WidgetLibraryLabelProvider());
		viewer.setInput(new WidgetLibrary());
	}
}
