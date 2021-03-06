package cuina.editor.map.internal.views;

import cuina.database.DatabaseInput;
import cuina.editor.core.CuinaCore;
import cuina.editor.core.EditorContextChangeListener;
import cuina.editor.map.ITerrainEditor;
import cuina.editor.map.internal.TerrainEditor;
import cuina.editor.map.internal.TilesetEditor;
import cuina.editor.map.internal.TilesetPanel;
import cuina.editor.ui.ClipboardUtil;
import cuina.map.Tileset;
import cuina.resource.ResourceException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

public class TilesetView extends ViewPart implements EditorContextChangeListener, KeyListener
{
	public static final String VIEW_ID = "cuina.editor.map.TilsetView";
	
	private ITerrainEditor editor;
	private Tileset tileset;
	private TilesetPanel panel;
	private Action editAction;
	
	@Override
	public void createPartControl(Composite parent)
	{
		this.panel = new TilesetPanel(parent, 256, 256);
		panel.getGLCanvas().setDragDetect(true);
		ClipboardUtil.addDropListener(panel.getGLCanvas(), IFile.class);
//		addMouseHandling();
		
		IWorkbenchPage page = getViewSite().getPage();
		IEditorPart editor = page.getActiveEditor();
		if (editor instanceof TerrainEditor)
		{
			setEditor((TerrainEditor) editor);
		}
		CuinaCore.getDefault().addEditorContextChangeListener(this);
		getSite().setSelectionProvider(panel);
		panel.getGLCanvas().addKeyListener(this);
		makeActions();
	}

	private void makeActions()
	{
		editAction = new Action("Editieren", IAction.AS_PUSH_BUTTON)
		{
			@Override
			public void run()
			{
				if (editor == null) return;
				
				try
				{
					DatabaseInput input = new DatabaseInput(editor.getProject(), "Tileset", tileset.getKey());
					getSite().getPage().openEditor(input, TilesetEditor.ID);
				}
				catch (PartInitException | ResourceException e)
				{
					throw new RuntimeException(e);
				}
			}
		};
		editAction.setToolTipText("Tileset im Tileseteditor öffnen");
		IActionBars bars = getViewSite().getActionBars();
		bars.getToolBarManager().add(editAction);
	}

	@Override
	public void setFocus()
	{
		panel.getGLCanvas().setFocus();
	}
	
	public void setEditor(ITerrainEditor editor)
	{
		if (this.editor == editor) return;
		
		this.editor = editor;
		refresh();
	}

	private void refresh()
	{
		if (editor == null) return;
		
		editor.refreshResources();
		this.tileset = editor.getTileset();
		panel.setTileset(tileset, editor.getProject());
	}

	@Override
	public void editorContextChange(IEditorPart part, IProject project)
	{
		if (part instanceof TerrainEditor)
			setEditor((TerrainEditor) part);
		else
			setEditor(null);
	}

	@Override
	public void keyPressed(KeyEvent ev)
	{
		if (ev.keyCode == SWT.F5)
		{
			refresh();
			return;
		}
	}

	@Override
	public void keyReleased(KeyEvent ev) {}
}
