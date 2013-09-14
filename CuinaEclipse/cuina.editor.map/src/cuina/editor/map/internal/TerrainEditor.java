package cuina.editor.map.internal;

import cuina.database.Database;
import cuina.editor.core.CuinaCore;
import cuina.editor.core.CuinaProject;
import cuina.editor.map.EditorToolAction;
import cuina.editor.map.ITerrainEditor;
import cuina.editor.map.MapChangeListener;
import cuina.editor.map.MapEvent;
import cuina.editor.map.TerrainLayer;
import cuina.editor.map.internal.layers.TerrainPanel;
import cuina.editor.map.util.MapOperation;
import cuina.editor.ui.ViewLayer;
import cuina.editor.ui.selection.Selection;
import cuina.editor.ui.selection.SelectionEvent;
import cuina.editor.ui.selection.SelectionListener;
import cuina.editor.ui.selection.SelectionManager;
import cuina.gl.Image;
import cuina.gl.LWJGL;
import cuina.map.Map;
import cuina.map.Tileset;
import cuina.resource.ResourceException;
import cuina.resource.ResourceManager;
import cuina.resource.ResourceManager.Resource;
import cuina.resource.ResourceProvider;
import cuina.resource.SerializationManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.EditorPart;
import org.lwjgl.LWJGLException;

public class TerrainEditor extends EditorPart implements
		ITerrainEditor, IOperationHistoryListener, MenuDetectListener, KeyListener
{
	public static final String TOOL_COMMAND_ID = "cuina.editor.map.tool";
	
	private TerrainPanel panel;
	private boolean dirty;
	
	private Map map;
	private CuinaProject project;
	private Tileset tileset;
	private IToolBarManager toolbarManager;
	private final HashMap<String, EditorToolAction> tools = new HashMap<String, EditorToolAction>();
	private final ArrayList<MapChangeListener> listeners = new ArrayList<MapChangeListener>();
	private IOperationHistory operationHistory;
	private EditorToolAction cursorMode;
	private boolean exclusiveLayer;
	protected boolean showRaster;

	private Point menuPoint;
	
	@Override
	public void addOperation(IUndoableOperation op)
	{
		operationHistory.add(op);
	}
	
	@Override
	public void doSave(IProgressMonitor monitor)
	{
		System.out.println("[TerrainEditor] doSave");
	}

	@Override
	public void doSaveAs()
	{
		doSave(null);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		LWJGL.init(null);
		setSite(site);
		setInput(input);
		
		readInput(input);
		
		if (map == null) throw new PartInitException("Map not found!");
		if (tileset == null) throw new PartInitException("Tileset '" + map.tilesetKey + "' not found!");
	}

	private void readInput(IEditorInput input) throws PartInitException
	{
		IFile file = (IFile) input.getAdapter(IFile.class);
		if (file == null) throw new PartInitException("input must adapt an IFile.");
		try
		{
			project = (CuinaProject) input.getAdapter(CuinaProject.class);
			if (project == null) project = CuinaCore.getCuinaProject(file.getProject());
			
			map = (Map) input.getAdapter(Map.class);
			if (map == null) map = (Map) SerializationManager.load(file, Map.class.getClassLoader());
			
			Database db = project.getService(Database.class);
			tileset = db.<Tileset> loadTable("Tileset").get(map.tilesetKey);
		}
		catch (ResourceException e)
		{
			throw new PartInitException("read Editor Input faild!", e);
		}
		setPartName(input.getName());
	}

	@Override
	public boolean isDirty()
	{
		return dirty;
	}
	
	public void setDirty(boolean value)
	{
		dirty = value;
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}
	
	@Override
	public boolean isRasterVisible()
	{
		return showRaster;
	}
	
	@Override
	public void setRasterVisible(boolean showRaster)
	{
		this.showRaster = showRaster;
	}

	@Override
	public void addListener(int eventType, Listener listener)
	{
		panel.getGLCanvas().addListener(eventType, listener);
	}
	
	@Override
	public void removeListener(int eventType, Listener listener)
	{
		panel.getGLCanvas().removeListener(eventType, listener);
	}

	public boolean addMapChangeListener(MapChangeListener l)
	{
		return listeners.add(l);
	}

	public boolean removeMapChangeListener(MapChangeListener l)
	{
		return listeners.remove(l);
	}

	@Override
	public void fireMapChanged(Object source, int props)
	{
		if (props != 0) setDirty(true);
		if ((props & MapEvent.PROP_SIZE) != 0) updateMapSize();
		
		MapEvent event = new MapEvent(source, map, props);
		for (MapChangeListener l : listeners)
		{
			l.mapChanged(event);
		}
		
		panel.refresh();
	}
	
	@Override
	public void createPartControl(Composite parent)
	{
		panel = new TerrainPanel(parent, map.width * tileset.getTileSize(), map.height * tileset.getTileSize());
		initSelectionMode();
		addSelectionHandling();
		
		panel.installLayers(this);
		panel.setMargin(tileset.getTileSize());
		panel.getGLCanvas().addMenuDetectListener(this);
		panel.getGLCanvas().addKeyListener(this);
		
		// setze Operation-Handler
		IWorkbench workbench = PlatformUI.getWorkbench();
		operationHistory = workbench.getOperationSupport().getOperationHistory();
		operationHistory.addOperationHistoryListener(this);

		// setze Commands
		this.toolbarManager = getEditorSite().getActionBars().getToolBarManager();
		fillActionBars(getEditorSite().getActionBars());
		hookContextMenu();
	}
	
	private void addSelectionHandling()
	{
		panel.getSelectionHandler().addSelectionListener(new SelectionListener()
		{
//			@Override
//			public void selectionModeChanged(Object source, SelectionMode oldMode, SelectionMode newMode)
//			{
//				if (oldMode == CURSOR_SELECTION_MODE)
//				{
//					Selection s = panel.getSelectionHandler().getSelection();
//					List<ViewLayer> layers = panel.getLayers();
//
//					if (s.getWidth() == 1 && s.getHeight() == 1)
//					{
//						Point p = new Point(s.getX(), s.getY());
//						for (int i = layers.size() - 1; i >= 0; i--)
//						{
//							if ( ((TerrainLayer) layers.get(i)).selectionPerformed(p) ) break;
//						}
//					}
//					else
//					{
//						Rectangle r = s.getBounds();
//						for (int i = layers.size() - 1; i >= 0; i--)
//						{
//							if ( ((TerrainLayer) layers.get(i)).selectionPerformed(r) ) break;
//						}
//					}
//				}
//			}

			@Override
			public void startSelection(SelectionEvent event)
			{
				if (getActiveLayer() != null) return;
				
				if (event.mouseEvent.button == 1)
				{
					System.out.println("[TerrainEditor] change Selection-Mode");
					event.manager.setSelectionMode(CURSOR_SELECTION_MODE, false);
				}
				else
				{
					event.manager.clearSeletionMode();
					event.doIt = false;
				}
			}

			@Override
			public void updateSelection(SelectionEvent event)
			{
				if (exclusiveLayer) return;
//				List<ViewLayer> layers = panel.getLayers();
//				
//				for (int i = layers.size() - 1; i >= 0; i--)
//				{
//					if ( ((TerrainLayer) layers.get(i)).updateSelection(event) ) break;
//				}
			}

			@Override
			public void endSelection(SelectionEvent event)
			{
				if (event.manager.getSelectionMode() != ITerrainEditor.CURSOR_SELECTION_MODE) return;
				
				System.out.println("[TerrainEditor] change Selection-Mode");
				event.manager.setSelectionMode(null, false);
				
				Selection s = event.manager.getSelection();
				List<TerrainLayer> layers = panel.getLayers();

				if (s.getWidth() == 1 && s.getHeight() == 1)
				{
					Point p = new Point(s.getX(), s.getY());
					for (int i = layers.size() - 1; i >= 0; i--)
					{
						if ( layers.get(i).selectionPerformed(p) ) return;
					}
				}
				else
				{
					Rectangle r = s.getBounds();
					for (int i = layers.size() - 1; i >= 0; i--)
					{
						if ( layers.get(i).selectionPerformed(r) ) return;
					}
				}
				
				event.manager.clearSelections();
				event.doIt = false;
			}
		});
	}

	public void fillActionBars(IActionBars actionBars)
	{
		toolbarManager.add(new Separator(ITerrainEditor.TOOLBAR_VIEWOPTIONS));
		toolbarManager.add(new Separator(ITerrainEditor.TOOLBAR_TOOLS));
		
		IAction undoAction = new UndoActionHandler(getSite(), MapOperation.MapContext.INSTANCE);
		IAction redoAction = new RedoActionHandler(getSite(), MapOperation.MapContext.INSTANCE);
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undoAction);
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), redoAction);
		
		Action rasterAction = new Action("Raster", IAction.AS_CHECK_BOX)
		{
			@Override
			public void run()
			{
				showRaster = isChecked();
			}
		};
		rasterAction.setId(ACTION_RASTER);
		rasterAction.setText("Raster");
		rasterAction.setToolTipText("Stellt das Karten-Raster an/aus.");
		rasterAction.setImageDescriptor(Activator.getImageDescriptor("raster.png"));
		
		toolbarManager.appendToGroup(TOOLBAR_VIEWOPTIONS, rasterAction);
		
		cursorMode = new EditorToolAction(this, null)
		{
			@Override
			public void activate()
			{
				System.out.println("[TerrainEditor] Set Cursor Mode");
				initSelectionMode();
			}
		};
		cursorMode.setId(ACTION_CURSOR);
		cursorMode.setText("Cursor");
		cursorMode.setToolTipText("Aktiviert den Auswahl-Modus.");
		cursorMode.setImageDescriptor(Activator.getImageDescriptor("cursor.png"));
		cursorMode.setChecked(true);
		
		addEditorTool(cursorMode);

//		manager.appendToGroup(TOOLBAR_TOOLS, cursorMode);

		for (ViewLayer layer : panel.getLayers())
		{
			try
			{
				layer.fillActionBars(actionBars);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void initSelectionMode()
	{
		SelectionManager handler = getSelectionManager();
		handler.setDisableOutside(false);
		handler.setSelectionMode(CURSOR_SELECTION_MODE, false);
		handler.clearSelections();
	}

	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			@Override
			public void menuAboutToShow(IMenuManager manager)
			{
				panel.showContextMenu(manager, menuPoint);
			}
		});
		Menu menu = menuMgr.createContextMenu(panel.getGLCanvas());
		panel.getGLCanvas().setMenu(menu);
	}

	private void updateMapSize()
	{
		Rectangle bounds = getViewBounds();
		panel.setViewSize(bounds.width, bounds.height);
	}

	@Override
	public void setFocus()
	{
		panel.getGLCanvas().setFocus();
	}

	@Override
	public Map getMap()
	{
		return map;
	}

	@Override
	public Tileset getTileset()
	{
		return tileset;
	}

	@Override
	public Rectangle getViewBounds()
	{
		return new Rectangle(panel.getMargin(), panel.getMargin(),
				map.width * tileset.getTileSize(), map.height * tileset.getTileSize());
	}

	@Override
	public SelectionManager getSelectionManager()
	{
		return panel.getSelectionHandler();
	}

	@Override
	public CuinaProject getProject()
	{
		return project;
	}

	@Override
	public Image loadImage(GLCanvas context, String imageName) throws ResourceException
	{
		Resource res = project.getService(ResourceProvider.class).
		getResource(ResourceManager.KEY_GRAPHICS, imageName);
		
		try
		{
			return new Image(context, res.getPath().toString());
		}
		catch (LWJGLException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void setActiveLayer(TerrainLayer layer)
	{
		panel.setActiveLayer(layer);
	}

	@Override
	public TerrainLayer getActiveLayer()
	{
		return panel.getActiveLayer();
	}

	@Override
	public GLCanvas getGLCanvas()
	{
		return panel.getGLCanvas();
	}

	@Override
	public void historyNotification(OperationHistoryEvent event)
	{
		if (event.getOperation() instanceof MapOperation)
		{
			System.out.println("Undo-History Event: " + event);
			panel.refresh();
		}
	}
	
	@Override
	public void addEditorTool(EditorToolAction tool)
	{
		/* XXX
		 * CommandContributionItem Übergibt den Parameter nicht korrekt.
		 * Daher alternativ wieder mit Actions.
		 */
		
//        CommandContributionItemParameter cmdParameters = new CommandContributionItemParameter(
//        		getEditorSite(), tool.getID(), TOOL_COMMAND_ID, CommandContributionItem.STYLE_RADIO);
//        cmdParameters.icon = tool.getAction().getImageDescriptor();
//        cmdParameters.label = tool.getAction().getText();
//        cmdParameters.tooltip = tool.getAction().getToolTipText();
//        cmdParameters.parameters = new HashMap();
//        cmdParameters.parameters.put("org.eclipse.ui.commands.radioStateParameter", tool.getID());
//        CommandContributionItem item = new CommandContributionItem(cmdParameters);
        
		toolbarManager.appendToGroup(TOOLBAR_TOOLS, new ActionContributionItem(tool));
		tools.put(tool.getId(), tool);
	}
	
	@Override
	public EditorToolAction getEditorTool(String id)
	{
		return tools.get(id);
	}
	
	@Override
	public void activateTool(String id)
	{
		EditorToolAction tool = tools.get(id);
		if (tool == null) throw new NullPointerException("Tool " + id + " does not exists.");

		if (tool.isChecked()) return;

		for (EditorToolAction t : tools.values())
		{
			if (t.isChecked())
			{
				t.setChecked(false);
				t.run();
			}
		}

		tool.setChecked(true);
		tool.run();
		toolbarManager.update(true);
	}

	@Override
	public void menuDetected(MenuDetectEvent e)
	{
		menuPoint = panel.toControl(e.x, e.y);
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		TerrainLayer layer = getActiveLayer();
		if (layer == null) return;
		
		layer.keyActionPerformed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {}
}
