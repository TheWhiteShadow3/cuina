package cuina.editor.map;
 
import cuina.editor.core.CuinaProject;
import cuina.editor.ui.selection.SelectionManager;
import cuina.editor.ui.selection.SelectionMode;
import cuina.editor.ui.selection.SpanSelectionMode;
import cuina.gl.Image;
import cuina.map.Map;
import cuina.map.Tileset;
import cuina.resource.ResourceException;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.widgets.Listener;
 
public interface ITerrainEditor
{
	public static final SelectionMode CURSOR_SELECTION_MODE = new SpanSelectionMode();

	public static final String TOOLBAR_VIEWOPTIONS	= "editor.map.modegroup";
	public static final String TOOLBAR_TOOLS		= "editor.map.toolgroup";
	
	public static final String ACTION_RASTER 		= "cuina.editor.map.rasterAction";
	public static final String ACTION_CURSOR 		= "cuina.editor.map.cursor";
	
	/**
	 * Gibt die Karte zurück.
	 * Der Karteneditor wird immer mit einer Karte initialisiert.
	 * Der Rückgabewert ist daher niemals <code>null</code>.
	 * @return Die Karte.
	 */
	public Map getMap();

	/**
	 * Gibt das Tileset zur Karte zurück.
	 * Jede Karte besitzt ein Tileset.
	 * Der Rückgabewert ist daher niemals <code>null</code>.
	 * @return Das Tileset der Karte.
	 */
	public Tileset getTileset();
	
	public int getGridSize();

	/**
	 * Gibt ein Rechteck mit der Position und Größe der Karte auf der Zeichenfläche zurück.
	 * @return Grenzen der Zeichenfläche der Karte.
	 */
	public Rectangle getViewBounds();

	/**
	 * Gibt das Canvas mit dem Open-GL Kontext zurück.
	 * @return Das Open-GL Canvas.
	 */
	public GLCanvas getGLCanvas();

	/**
	 * Gibt den SelectionManager für die Zeichenfläche zurück.
	 * @return Den SelectionManager.
	 */
	public SelectionManager getSelectionManager();

	public CuinaProject getProject();

	public Image loadImage(GLCanvas context, String imageName) throws ResourceException;

	public void setActiveLayer(TerrainLayer layer);

	public TerrainLayer getActiveLayer();

	public void fireMapChanged(Object source, int props);

	public void addOperation(IUndoableOperation op);

	public void addListener(int eventType, Listener listener);

	public void removeListener(int eventType, Listener listener);

	public boolean isRasterVisible();

	public void setRasterVisible(boolean showRaster);

	public TerrainLayer getLayerByName(String layerName);

	public String getActiveTool();

	public void setActiveTool(String toolID);
}