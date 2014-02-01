package cuina.editor.map.internal;

import cuina.database.Database;
import cuina.database.DatabaseInput;
import cuina.editor.core.CuinaCore;
import cuina.editor.core.CuinaProject;
import cuina.editor.map.ITerrainEditor;
import cuina.editor.map.MapChangeListener;
import cuina.editor.map.MapEvent;
import cuina.editor.map.TerrainLayer;
import cuina.editor.map.util.MapOperation;
import cuina.editor.ui.selection.Selection;
import cuina.editor.ui.selection.SelectionEvent;
import cuina.editor.ui.selection.SelectionListener;
import cuina.editor.ui.selection.SelectionManager;
import cuina.editor.ui.selection.SpanSelectionMode;
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
import java.util.List;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistoryListener;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
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
import org.eclipse.ui.dialogs.SaveAsDialog;
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

	private IFile file;
	private Map map;
	private CuinaProject project;
	private Tileset tileset;
	private final ArrayList<MapChangeListener> listeners = new ArrayList<MapChangeListener>();
	private IOperationHistory operationHistory;
//	private boolean exclusiveLayer;
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
		try
		{
			SerializationManager.save(map, file);
			setDirty(false);
		}
		catch(ResourceException e)
		{
			showError("Datei konnte nicht gespeichert werden.", e);
		}
	}

	@Override
	public void doSaveAs()
	{
		SaveAsDialog dialog = new SaveAsDialog(getEditorSite().getShell());
		dialog.setOriginalFile(file);
		dialog.setTitle("Speichern unter");
		int result = dialog.open();
		if (result == Dialog.OK) try
		{
			SerializationManager.save(map, ResourcesPlugin.getWorkspace().getRoot().getFile(dialog.getResult()));
			setDirty(false);
		}
		catch(ResourceException e)
		{
			showError("Datei konnte nicht gespeichert werden.", e);
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		LWJGL.init(null);
		setSite(site);
		setInput(input);

		readInput(input);

		if(map == null)
			throw new PartInitException("Map not found!");
		if(tileset == null)
			throw new PartInitException("Tileset '" + map.tilesetKey + "' not found!");
	}
	
	private void showError(String message, Exception e)
	{
		ErrorDialog.openError(getEditorSite().getShell(), "Fehler!", message,
				new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage()));
	}

	private void readInput(IEditorInput input) throws PartInitException
	{
		try
		{
			if (input instanceof DatabaseInput)
				file = getMapFile((DatabaseInput) input);
			else
				file = (IFile) input.getAdapter(IFile.class);

			if (file == null)
				throw new PartInitException("Input must adapt an IFile.");

			project = (CuinaProject) input.getAdapter(CuinaProject.class);
			if (project == null)
				project = CuinaCore.getCuinaProject(file.getProject());

			this.map = (Map) SerializationManager.load(file, Map.class.getClassLoader());
			loadTileset();
		}
		catch(ResourceException e)
		{
			throw new PartInitException("Read editor input faild!", e);
		}
		setPartName(input.getName());
	}
	
	private void loadTileset() throws ResourceException
	{
		Database db = project.getService(Database.class);
		this.tileset = db.<Tileset> loadTable("Tileset").get(map.tilesetKey);
	}

	private IFile getMapFile(DatabaseInput dbInput) throws ResourceException
	{
		CuinaProject project = (CuinaProject) dbInput.getAdapter(CuinaProject.class);
		IFolder folder = project.getProject().getFolder(
				project.getIni().get(Activator.PLUGIN_ID, Activator.MAPS_DIRECTORY_ID, "maps"));

		return SerializationManager.resolve(folder, dbInput.getKey(), "cxm");
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
		return true;
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
	public int getGridSize()
	{
		//XXX: Momentan noch Hard codiert.
		return getTileset().getTileSize() / 2;
	}
	
	@Override
	public void setActiveTool(String toolID)
	{
		MapEditorActionBarContributor c = (MapEditorActionBarContributor) getEditorSite().getActionBarContributor();
		c.setActiveTool(toolID);
	}
	
	@Override
	public String getActiveTool()
	{
		MapEditorActionBarContributor c = (MapEditorActionBarContributor) getEditorSite().getActionBarContributor();
		return c.getActiveTool();
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
		if(props != 0)
			setDirty(true);
		if((props & MapEvent.PROP_SIZE) != 0)
			updateMapSize();

		MapEvent event = new MapEvent(source, map, props);
		for(MapChangeListener l : listeners)
		{
			l.mapChanged(event);
		}

		panel.redraw();
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

		fillActionBars(getEditorSite().getActionBars());
		hookContextMenu();
	}

	private void addSelectionHandling()
	{
		panel.getSelectionManager().addSelectionListener(new SelectionListener()
		{
			@Override
			public void startSelection(SelectionEvent event)
			{
				if(getActiveLayer() != null) return;

				if(event.mouseEvent.button == 1)
				{
					System.out.println("[TerrainEditor] change Selection-Mode");
					event.manager.setSelectionMode(CURSOR_SELECTION_MODE, false);
				} else
				{
					event.manager.clearSeletionMode();
					event.doIt = false;
				}
			}

			@Override
			public void updateSelection(SelectionEvent event)
			{
				if ((event.mouseEvent.stateMask & SWT.MOD1) != 0)
				{
					((SpanSelectionMode) CURSOR_SELECTION_MODE).setGridSize(getGridSize());
				}
				else
				{
					((SpanSelectionMode) CURSOR_SELECTION_MODE).setGridSize(1);
				}
//				if(exclusiveLayer) return;
				// List<ViewLayer> layers = panel.getLayers();
				//
				// for (int i = layers.size() - 1; i >= 0; i--)
				// {
				// if ( ((TerrainLayer) layers.get(i)).updateSelection(event) )
				// break;
				// }
			}

			@Override
			public void endSelection(SelectionEvent event)
			{
				if(event.manager.getSelectionMode() != ITerrainEditor.CURSOR_SELECTION_MODE) return;

				System.out.println("[TerrainEditor] change Selection-Mode");
				event.manager.setSelectionMode(null, false);

				Selection s = event.manager.getSelection();
				List<TerrainLayer> layers = panel.getLayers();

				if(s.getWidth() == 1 && s.getHeight() == 1)
				{
					Point p = new Point(s.getX(), s.getY());
					for(int i = layers.size() - 1; i >= 0; i--)
					{
						if(layers.get(i).selectionPerformed(p)) return;
					}
				}
				else
				{
					Rectangle r = s.getBounds();
					for(int i = layers.size() - 1; i >= 0; i--)
					{
						if(layers.get(i).selectionPerformed(r)) return;
					}
				}

				event.manager.clearSelections();
				event.doIt = false;
			}
		});
	}

	public void fillActionBars(IActionBars actionBars)
	{
//		toolbarManager.add(new Separator(ITerrainEditor.TOOLBAR_VIEWOPTIONS));
//		toolbarManager.add(new Separator(ITerrainEditor.TOOLBAR_TOOLS));

		IAction undoAction = new UndoActionHandler(getSite(), MapOperation.MapContext.INSTANCE);
		IAction redoAction = new RedoActionHandler(getSite(), MapOperation.MapContext.INSTANCE);
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undoAction);
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), redoAction);
	}

	void initSelectionMode()
	{
		SelectionManager handler = getSelectionManager();
		handler.setDisableOutside(false);
		handler.setSelectionMode(CURSOR_SELECTION_MODE, false);
		setActiveLayer(null);
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
		return new Rectangle(
				panel.getMargin(),
				panel.getMargin(),
				map.width * tileset.getTileSize(),
				map.height * tileset.getTileSize());
	}

	@Override
	public SelectionManager getSelectionManager()
	{
		return panel.getSelectionManager();
	}

	@Override
	public CuinaProject getProject()
	{
		return project;
	}

	@Override
	public Image loadImage(String imageName) throws ResourceException
	{
		Resource res = project.getService(ResourceProvider.class).getResource(ResourceManager.KEY_GRAPHICS, imageName);

		try
		{
			return new Image(panel.getGLCanvas(), res.getPath().toString());
		}
		catch(LWJGLException e)
		{
			showError("Datei '" + imageName + "' konnte nicht geladen werden.", e);
		}
		return null;
	}

	@Override
	public void setActiveLayer(TerrainLayer layer)
	{
		if (getSite().getPage().getActiveEditor() != this)
		{
			System.out.println("[TerrainEditor] frage Fokus an.");
			getSite().getPage().activate(this);
		}
		
		TerrainLayer oldLayer = panel.getActiveLayer();
		if (oldLayer == layer) return;
		
		if (oldLayer != null) oldLayer.deactivated();
		
		panel.setActiveLayer(layer);
		if (layer != null) layer.activated();
	}

	@Override
	public TerrainLayer getActiveLayer()
	{
		return panel.getActiveLayer();
	}

	@Override
	public TerrainLayer getLayerByName(String layerName)
	{
		if (layerName == null) return null;
		for (TerrainLayer layer : panel.getLayers())
		{
			if (layerName.equals(layer.getName())) return layer;
		}
		
		return null;
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
			panel.redraw();
		}
	}

	@Override
	public void menuDetected(MenuDetectEvent ev)
	{
		menuPoint = panel.toControl(ev.x, ev.y);
	}

	@Override
	public void keyPressed(KeyEvent ev)
	{
		if (ev.keyCode == SWT.F5)
		{
			try
			{
				loadTileset();
				panel.refreshLayers();
			}
			catch (ResourceException e)
			{
				e.printStackTrace();
			}
			return;
		}
		
		TerrainLayer layer = getActiveLayer();
		if (layer == null) return;

		layer.keyActionPerformed(ev);
	}

	@Override
	public void keyReleased(KeyEvent ev)
	{
	}
}
