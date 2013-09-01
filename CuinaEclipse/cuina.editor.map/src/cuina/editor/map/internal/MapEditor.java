package cuina.editor.map.internal;

import cuina.editor.core.CuinaPlugin;
import cuina.editor.core.CuinaProject;
import cuina.map.Map;
import cuina.resource.ResourceException;
import cuina.resource.SerializationManager;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.MultiPageEditorPart;

public class MapEditor extends MultiPageEditorPart
{
	private TerrainEditor terrainEditor;
	private MapPropertyEditor propertyEditor;
	// private EventEditor triggerEditor;
	private IFile file;
	private Map map;
	


	public void setDirty(boolean value)
	{
		terrainEditor.setDirty(value);
		propertyEditor.setDirty(value);
		firePropertyChange(PROP_DIRTY);
	}

	public TerrainEditor getTerrainEditor()
	{
		return terrainEditor;
	}

	public MapPropertyEditor getPropertyEditor()
	{
		return propertyEditor;
	}
	
	@Override
	protected void createPages()
	{
		this.file = (IFile) getEditorInput().getAdapter(IFile.class);
		try
		{
			map = (Map) SerializationManager.load(file, Map.class.getClassLoader());

			IEditorInput input = getMapEditorImput();
			
			terrainEditor = new TerrainEditor();
			addEditor(terrainEditor, "Terrain", input);
			propertyEditor = new MapPropertyEditor();
			addEditor(propertyEditor, "Properties", input);
			// triggerEditor = new EventEditor();
			// addEditor(triggerEditor, "Events");
		}
		catch (ResourceException e)
		{
			e.printStackTrace();
		}
	}
	
	private void addEditor(EditorPart editor, String name, IEditorInput input)
	{
		try
		{
			int index = addPage(editor, input);
			setPageText(index, name);
		}
		catch (PartInitException e)
		{
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested editor", e.getMessage(), e.getStatus());
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor)
	{
		try
		{
			SerializationManager.save(map, file);
			setDirty(false);
		}
		catch (ResourceException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void doSaveAs()
	{}

	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}
	
	private IEditorInput getMapEditorImput()
	{
		return new IEditorInput()
		{
			@Override
			public Object getAdapter(Class adapter)
			{
				if (adapter.equals(Map.class)) return map;
				if (adapter.equals(IFile.class)) return file;
				if (adapter.equals(IProject.class)) return file.getProject();
				if (adapter.equals(CuinaProject.class)) return CuinaPlugin.getCuinaProject(file.getProject());
				return null;
			}

			@Override
			public String getToolTipText()
			{
				return "Cuina Map";
			}

			@Override
			public IPersistableElement getPersistable()
			{
				return null;
			}

			@Override
			public String getName()
			{
				return file.getName();
			}

			@Override
			public ImageDescriptor getImageDescriptor()
			{
				return null;
			}

			@Override
			public boolean exists()
			{
				return true;
			}
		};
	}
}
