package cuina.editor.map;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.internal.Workbench;

public abstract class EditorToolAction extends Action
{
	private String layerName;
	private ITerrainEditor editor;
	private TerrainLayer layer;
	
	public EditorToolAction(String id, String layerName, int style)
	{
		super(null, style);
		setId(id);
		this.layerName = layerName;
	}
	
	@Override
	public void run()
	{
		execute(!isChecked());
	}
	
	public void execute(boolean activate)
	{
		this.editor = (ITerrainEditor) Workbench.getInstance().
				getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (editor == null) return;
		
		this.layer = editor.getLayerByName(layerName);
		if (activate)
		{
			System.out.println("activate " + getId());
			if (layer != null) editor.setActiveLayer(layer);
			activate();
		}
		else
		{
			System.out.println("deactivate " + getId());
			deactivate();
		}
		if (getListeners().length == 0) System.out.println("Kein Schwein interessiert der Button.");
		setChecked(activate);
	}
	
	public ITerrainEditor getEditor()
	{
		return editor;
	}
	
	public TerrainLayer getLayer()
	{
		return layer;
	}
	
	public abstract void activate();
	public void deactivate() {}
}
