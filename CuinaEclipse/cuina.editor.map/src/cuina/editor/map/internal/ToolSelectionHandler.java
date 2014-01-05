package cuina.editor.map.internal;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RadioState;

public class ToolSelectionHandler extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        if(HandlerUtil.matchesRadioState(event)) return null;
        
        IEditorPart editor = HandlerUtil.getActiveEditorChecked(event);
        if (!(editor instanceof TerrainEditor))
            throw new ExecutionException("Incorrect editor for command " + event.getCommand().getId());
        
        String toolID = event.getParameter(RadioState.PARAMETER_ID);
        System.out.println("[SelectionModeHandler] Change State: " + toolID);
 
        TerrainEditor terrainEditor = (TerrainEditor) editor;
        terrainEditor.activateTool(toolID);
        
        HandlerUtil.updateRadioState(event.getCommand(), toolID);
        return null;
    }
}
