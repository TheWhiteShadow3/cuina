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
				ObjectLayer.OBJECT_ACTION, ObjectLayer.LAYER_NAME, IAction.AS_RADIO_BUTTON)
		{
			@Override
			public void activate() { /* Wir wollen nur die Ebene aktivieren. */ }
		};
		selectionAction.setId(ObjectLayer.OBJECT_ACTION);
		selectionAction.setImageDescriptor(Activator.getImageDescriptor("object.png"));
		
		manager.appendToGroup(ITerrainEditor.TOOLBAR_TOOLS, selectionAction);
	}
}