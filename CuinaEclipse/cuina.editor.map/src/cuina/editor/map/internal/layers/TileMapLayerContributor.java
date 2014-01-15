package cuina.editor.map.internal.layers;

import cuina.editor.map.EditorToolAction;
import cuina.editor.map.IManagedContributor;
import cuina.editor.map.ITerrainEditor;
import cuina.editor.map.internal.Activator;
import cuina.editor.map.internal.EditorActionManager;
import cuina.map.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.EditorActionBarContributor;

public class TileMapLayerContributor extends EditorActionBarContributor implements IManagedContributor
{
	private TileMapLayerAction pencilAction;
	private TileMapLayerAction rectAction;
	private TileMapLayerAction elliAction;
	private TileMapLayerAction fillAction;
	private EditorActionManager actionManager;
	
//	private static final String STATE_TOOL = "cuina.editor.map.tilemap.Tool";
//	private static final String STATE_RASTER = "cuina.editor.map.tilemap.Raster";
//	private static final String STATE_DIM = "cuina.editor.map.tilemap.Dim";
//	private static final String STATE_FADE = "cuina.editor.map.tilemap.Fade";

//	@Override
//	public void setActiveEditor(IEditorPart targetEditor)
//	{
////		EditorState state = ((ITerrainEditor) targetEditor).getEditorState();
////		int tool = (Integer) state.getValue("cuina.editor.map.tilemap.tool");
////		switch(tool)
////		{
////			case TileMapLayer.DRAWMODE_NONE: 
////		}
//		
//		
//		//TODO: Ich brauche Eine Möglichekt den Anzeige-Zustand von einem Editor auf einen anderen zu übertragen.
//		
//	}

	@Override
	public void setActionManager(EditorActionManager actionManager)
	{
		this.actionManager = actionManager;
	}
	
	@Override
	public void contributeToToolBar(IToolBarManager manager)
	{
		pencilAction = new TileMapLayerAction(TileMapLayer.ACTION_PENCIL, IAction.AS_RADIO_BUTTON)
		{
			@Override
			public void activate()
			{
				getLayer().setTool(TileMapLayer.DRAWMODE_PENCIL);
			}
		};
		pencilAction.setText("Stift");
		pencilAction.setToolTipText("Aktiviert den Stift Modus");
		pencilAction.setImageDescriptor(Activator.getImageDescriptor("pencil.png"));
		
		rectAction = new TileMapLayerAction(TileMapLayer.ACTION_RECTANGLE, IAction.AS_RADIO_BUTTON)
		{
			@Override
			public void activate()
			{
				getLayer().setTool(TileMapLayer.DRAWMODE_RECTANGLE);
			}
		};
		rectAction.setText("Rechteck");
		rectAction.setToolTipText("Aktiviert den Rechteck Zeichenmodus.");
		rectAction.setImageDescriptor(Activator.getImageDescriptor("rectangle.png"));
		
		elliAction = new TileMapLayerAction(TileMapLayer.ACTION_ELLIPSE, IAction.AS_RADIO_BUTTON)
		{
			@Override
			public void activate()
			{
				getLayer().setTool(TileMapLayer.DRAWMODE_ELLISPE);
			}
		};
		elliAction.setText("Ellipse");
		elliAction.setToolTipText("Aktiviert den Ellipsen Zeichenmodus.");
		elliAction.setImageDescriptor(Activator.getImageDescriptor("ellipse.png"));
		
		fillAction = new TileMapLayerAction(TileMapLayer.ACTION_FILLER, IAction.AS_RADIO_BUTTON)
		{
			@Override
			public void activate()
			{
				getLayer().setTool(TileMapLayer.DRAWMODE_FILLER);
			}
		};
		fillAction.setText("Filler");
		fillAction.setToolTipText("Aktiviert den Ausfüll Zeichenmodus.");
		fillAction.setImageDescriptor(Activator.getImageDescriptor("filler.png"));

		actionManager.addAction(pencilAction);
		actionManager.addAction(rectAction);
		actionManager.addAction(elliAction);
		actionManager.addAction(fillAction);
		
		manager.appendToGroup(ITerrainEditor.TOOLBAR_TOOLS, pencilAction);
		manager.appendToGroup(ITerrainEditor.TOOLBAR_TOOLS, rectAction);
		manager.appendToGroup(ITerrainEditor.TOOLBAR_TOOLS, elliAction);
		manager.appendToGroup(ITerrainEditor.TOOLBAR_TOOLS, fillAction);
		
		Action layerAction = new LayerDropDownAction();
		manager.appendToGroup(ITerrainEditor.TOOLBAR_VIEWOPTIONS, layerAction);
	}
	
	// Ebenenauswahl-menü
	private class LayerDropDownAction extends TileMapLayerAction implements IMenuCreator
	{
		private Menu layerMenu;
		
		public LayerDropDownAction()
		{
			super(null, IAction.AS_DROP_DOWN_MENU);
			setText("Ebene");
			setImageDescriptor(Activator.getImageDescriptor("layer.png"));
			setMenuCreator(this);
			setEnabled(true);
		}
		
		@Override
		public Menu getMenu(Menu parent)
		{
			return null;
		}
		
		@Override
		public Menu getMenu(Control parent)
		{
			layerMenu = new Menu(parent);
			
			for(int i = 0; i < Map.LAYERS; i++)
			{
				new ActionContributionItem(new LayerAction(i)).fill(layerMenu, -1);
			}
			return layerMenu;
		}
		
		@Override
		public void dispose()
		{
			if (layerMenu != null) layerMenu.dispose();
		}

		@Override
		public void activate()
		{
			getLayer().setDimFade(false);
		}

		@Override
		public void deactivate()
		{
			getLayer().setDimFade(true);
		}
	}
	
	private class LayerAction extends TileMapLayerAction
	{
		private final int layer;

		public LayerAction(int layer)
		{
			super(null, IAction.AS_RADIO_BUTTON);
			setText("Ebene" + (layer + 1));
			this.layer = layer;
			setChecked(layer == 0);//this.layer == TileMapLayer.this.currentLayer);
		}
		
		@Override
		public void activate()
		{
			getLayer().setCurrentLayer(layer);
			getLayer().setDimFade(true);
		}
	}
	
	private static abstract class TileMapLayerAction extends EditorToolAction
	{
		public TileMapLayerAction(String id, int type)
		{
			super(id, TileMapLayer.LAYER_NAME, type);
		}

		@Override
		public TileMapLayer getLayer()
		{
			return (TileMapLayer) super.getLayer();
		}
	}
}