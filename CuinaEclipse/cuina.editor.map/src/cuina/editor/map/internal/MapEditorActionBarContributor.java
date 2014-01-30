package cuina.editor.map.internal;

import cuina.editor.map.EditorToolAction;
import cuina.editor.map.ITerrainEditor;
import cuina.editor.map.internal.Activator.LayerDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.EditorActionBarContributor;

public class MapEditorActionBarContributor extends EditorActionBarContributor implements IPropertyChangeListener
{
	private Map<String, IEditorActionBarContributor> contributors = new HashMap<String, IEditorActionBarContributor>();
	private TerrainEditor editor;
	private final Map<String, SaveActionContributionItem> actions = new HashMap<String, SaveActionContributionItem>();
	public String activeTool;
	
	public MapEditorActionBarContributor() {}
	
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
				c.init(bars, page);
				contributors.put(def.getName(), c);
			}
			catch (InstantiationException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		ToolBarManager toolBarManager = (ToolBarManager) bars.getToolBarManager();
		IContributionItem[] items = toolBarManager.getItems();
		for (int i = 0; i < items.length; i++)
		{
			if (items[i] instanceof ActionContributionItem)
			{
				ActionContributionItem item = (ActionContributionItem) items[i];
				IAction action = item.getAction();
				if (action instanceof EditorToolAction)
				{
					toolBarManager.remove(items[i]);
					SaveActionContributionItem ci = new SaveActionContributionItem(action);
					toolBarManager.insert(i, ci);
					actions.put(action.getId(), ci);
					action.addPropertyChangeListener(this);
				}
			}
		}
		setActiveTool(TerrainEditor.ACTION_CURSOR);
	}

	@Override
	public void contributeToToolBar(IToolBarManager manager)
    {
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
		
		manager.appendToGroup(ITerrainEditor.TOOLBAR_VIEWOPTIONS, rasterAction);
		manager.appendToGroup(ITerrainEditor.TOOLBAR_TOOLS,  cursorMode);
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
		for(IEditorActionBarContributor c : contributors.values())
			c.setActiveEditor(editor);
	}

	public String getActiveTool()
	{
		return activeTool;
	}
	
	public void setActiveTool(String toolID)
	{
		if (Objects.equals(activeTool, toolID)) return;
		
		if (activeTool != null)
		{
			((EditorToolAction) actions.get(activeTool).getAction()).execute(false);
		}
		if (toolID != null)
		{
			SaveActionContributionItem ci = actions.get(toolID);
			if (ci == null) return;
			
			((EditorToolAction) ci.getAction()).execute(true);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		if (IAction.CHECKED.equals(event.getProperty()))
		{
			IAction action = (IAction) event.getSource();
			if (action.getStyle() != IAction.AS_RADIO_BUTTON) return;
			
			if (event.getNewValue() == Boolean.TRUE)
				this.activeTool = action.getId();
			else
				this.activeTool = null;
		}
	}
}