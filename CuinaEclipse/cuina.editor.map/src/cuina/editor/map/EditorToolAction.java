package cuina.editor.map;

import org.eclipse.jface.action.Action;
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
		int style = getStyle();
		if (style == AS_CHECK_BOX || style == AS_RADIO_BUTTON)
			execute(isChecked());
		else
			execute(true);
	}

	public void execute(boolean activate)
	{
		setChecked(activate);
		this.editor = (ITerrainEditor) Workbench.getInstance().
				getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (editor == null) return;
		
		this.layer = editor.getLayerByName(layerName);
		if (activate)
		{
			System.out.println("[EditorToolAction] activate " + getId());
			if (layer != null) editor.setActiveLayer(layer);
			activate();
		}
		else
		{
			System.out.println("[EditorToolAction] deactivate " + getId());
			deactivate();
		}
	}

	protected ITerrainEditor getEditor()
	{
		return editor;
	}
	
	protected TerrainLayer getLayer()
	{
		return layer;
	}
	
	protected abstract void activate();
	protected void deactivate() {}
}
