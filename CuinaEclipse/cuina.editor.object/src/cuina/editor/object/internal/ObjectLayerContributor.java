package cuina.editor.object.internal;

import cuina.editor.map.EditorToolAction;
import cuina.editor.map.ITerrainEditor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.part.EditorActionBarContributor;

public class ObjectLayerContributor extends EditorActionBarContributor
{
	@Override
	public void contributeToToolBar(IToolBarManager manager)
	{
		EditorToolAction selectionAction = new EditorToolAction(
				ObjectLayer.ACTION_SELECTION, ObjectLayer.LAYER_NAME, IAction.AS_RADIO_BUTTON)
		{
			@Override
			public void activate() {}
		};
		selectionAction.setId(ObjectLayer.ACTION_SELECTION);
		selectionAction.setImageDescriptor(Activator.getImageDescriptor("object.png"));
		
		manager.appendToGroup(ITerrainEditor.TOOLBAR_TOOLS, selectionAction);
	}
}