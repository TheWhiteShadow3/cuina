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
import org.eclipse.ui.IEditorSite;
 
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

	/**
	 * Gibt das Cuina-Projekt zurück.
	 * @return Das Cuina-Projekt.
	 */
	public CuinaProject getProject();

	/**
	 * Ladet ein GL-Image aus dem Projekt im Editor-Kontext.
	 * @param imageName Ressourcen-Name der Datei.
	 * @return Das GL-Image.
	 * @throws ResourceException
	 */
	public Image loadImage(String imageName) throws ResourceException;

	/**
	 * Setzt die aktive Ebene.
	 * @param layer Die neue aktive Ebene.
	 */
	public void setActiveLayer(TerrainLayer layer);

	/**
	 * Gibt die aktive Ebene zurück.
	 * @return Die aktive Ebene.
	 */
	public TerrainLayer getActiveLayer();

	/**
	 * Meldet, dass sich die Karte verändert hat.
	 * @param source
	 * @param props
	 * @see MapEvent
	 */
	public void fireMapChanged(Object source, int props);

	public void addOperation(IUndoableOperation op);

	public void addListener(int eventType, Listener listener);

	public void removeListener(int eventType, Listener listener);

	/*
	 * XXX: Was macht diese Methode hier eigentlich?
	 * Solle die nicht im TilemapLayer, wo das Raster auch gezeichnet wird?
	 * @return
	 */
	public boolean isRasterVisible();

	public void setRasterVisible(boolean showRaster);

	public TerrainLayer getLayerByName(String layerName);

	public String getActiveTool();

	public void setActiveTool(String toolID);
	
    /**
     * Gibt die Seite für den Editor zurück.
     * Der Wert kann <code>null</code> sein, wenn der Editor noch nicht inizialisiert ist.
     * @return Die Editor Seite oder <code>null</code>, wenn der Editor noch nicht inizialisiert ist.
     */
	public IEditorSite getEditorSite();
}