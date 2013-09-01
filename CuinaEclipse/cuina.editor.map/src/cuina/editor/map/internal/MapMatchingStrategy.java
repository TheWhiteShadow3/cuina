package cuina.editor.map.internal;

import cuina.database.DatabasePlugin;
import cuina.editor.core.CuinaPlugin;
import cuina.editor.core.CuinaProject;
import cuina.resource.ResourceProvider;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;

public class MapMatchingStrategy implements IEditorMatchingStrategy
{

	@Override
	public boolean matches(IEditorReference editorRef, IEditorInput input)
	{
		if (input instanceof IAdaptable)
		{
			IFile file = (IFile) input.getAdapter(IFile.class);
			
			CuinaProject cuinaProject = CuinaPlugin.getCuinaProject(file.getProject());
			ResourceProvider rp = cuinaProject.getService(ResourceProvider.class);
			String mapPath = rp.getIni().get(DatabasePlugin.PLUGIN_ID, DatabasePlugin.DATABASE_DIRECTORY_ID, "maps");
			return mapPath.equals(file.getFullPath().segment(1));
//			return (file.getName().matches("map\\d{3}\\..*"));
		}
		return false;
	}

}
