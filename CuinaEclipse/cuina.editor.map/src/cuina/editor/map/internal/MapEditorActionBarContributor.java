package cuina.editor.map.internal;

import cuina.editor.map.EditorToolAction;
import cuina.editor.map.IManagedContributor;
import cuina.editor.map.ITerrainEditor;
import cuina.editor.map.internal.Activator.LayerDefinition;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.EditorActionBarContributor;

public class MapEditorActionBarContributor extends EditorActionBarContributor
{
//	private EditorState editorState = new EditorState();
	private Map<String, IEditorActionBarContributor> contributors = new HashMap<String, IEditorActionBarContributor>();
	private TerrainEditor editor;
	private EditorActionManager actionManager;
	
	public MapEditorActionBarContributor()
	{
		this.actionManager = new EditorActionManager();
	}
	
	public IEditorActionBarContributor getLayerActionBarContributor(String layerName)
	{
		return contributors.get(layerName);
	}

	@Override
	public void init(IActionBars bars, IWorkbenchPage page)
	{
		super.init(bars, page);
		
		for (LayerDefinition def : Activator.getLayerDefinitions().values())
		{
			Class<? extends IEditorActionBarContributor> clazz = def.getActionBarContributorClass();
			if (clazz != null) try
			{
				IEditorActionBarContributor c = clazz.newInstance();
				if (c instanceof IManagedContributor)
					((IManagedContributor) c).setActionManager(actionManager);
				c.init(bars, page);
				contributors.put(def.getName(), c);
			}
			catch (InstantiationException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void contributeToToolBar(IToolBarManager manager)
    {
		actionManager.toolbarManager = manager;
    	manager.add(new Separator(ITerrainEditor.TOOLBAR_VIEWOPTIONS));
		manager.add(new Separator(ITerrainEditor.TOOLBAR_TOOLS));
		
//		IAction undoAction = new UndoActionHandler(getSite(), MapOperation.MapContext.INSTANCE);
//		IAction redoAction = new RedoActionHandler(getSite(), MapOperation.MapContext.INSTANCE);
//		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undoAction);
//		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), redoAction);
		
		EditorToolAction rasterAction = new EditorToolAction(ITerrainEditor.ACTION_RASTER, null, IAction.AS_CHECK_BOX)
		{
			@Override
			public void activate()
			{
				((TerrainEditor) getEditor()).showRaster = isChecked();
			}
		};
		rasterAction.setText("Raster");
		rasterAction.setToolTipText("Stellt das Karten-Raster an/aus.");
		rasterAction.setImageDescriptor(Activator.getImageDescriptor("raster.png"));
		
		EditorToolAction cursorMode = new EditorToolAction(TerrainEditor.ACTION_CURSOR, null, IAction.AS_RADIO_BUTTON) 
		{
			@Override
			public void activate()
			{
				((TerrainEditor) getEditor()).initSelectionMode();
			}
		};
		cursorMode.setText("Cursor");
		cursorMode.setToolTipText("Aktiviert den Auswahl-Modus.");
		cursorMode.setImageDescriptor(Activator.getImageDescriptor("cursor.png"));
		cursorMode.setChecked(true);
		
		actionManager.addAction(cursorMode);
		
		manager.appendToGroup(ITerrainEditor.TOOLBAR_VIEWOPTIONS, rasterAction);
		manager.appendToGroup(ITerrainEditor.TOOLBAR_TOOLS, cursorMode);
    }
	
	@Override
	public void setActiveEditor(IEditorPart targetEditor)
	{
		if (!(targetEditor instanceof TerrainEditor))
		{
			this.editor = null;
			return;
		}
		if (this.editor == targetEditor) return;
		
		this.editor = (TerrainEditor) targetEditor;
//		editor.setEditorState(editorState);
		for(IEditorActionBarContributor c : contributors.values())
			c.setActiveEditor(editor);
		
		actionManager.update();
	}

	public EditorActionManager getActionManager()
	{
		return actionManager;
	}
}