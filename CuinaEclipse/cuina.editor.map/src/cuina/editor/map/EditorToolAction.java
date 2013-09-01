package cuina.editor.map;

import org.eclipse.jface.action.Action;

public abstract class EditorToolAction extends Action
{
	private TerrainLayer layer;
	private ITerrainEditor editor;
//	private boolean selected;
	
	public EditorToolAction(ITerrainEditor editor, TerrainLayer layer)
	{
		super(null, AS_RADIO_BUTTON);
		this.editor = editor;
		this.layer = layer;
	}
	
	public TerrainLayer getLayer()
	{
		return layer;
	}

	/**
	 * Überprüft den Zustand des Tools und delegiert den Aufruf weiter nach
	 * {@link #activate()} bzw. {@link #deactivate()}.
	 * Wenn das Tool aktiviert wird, bekommt die assoziierte Ebene den Fokus, falls vorhanden.
	 */
	@Override
	public void run()
	{
//		setSelected(!selected);
		if (isChecked())
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
	}

//	public boolean isSelected()
//	{
//		return selected;
//	}
//
//	public void setSelected(boolean selected)
//	{
//		this.selected = selected;
//		super.setChecked(selected);
//	}

	public abstract void activate();
	public void deactivate() {}
}
