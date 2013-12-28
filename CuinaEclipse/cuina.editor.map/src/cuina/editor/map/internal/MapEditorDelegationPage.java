package cuina.editor.map.internal;

import cuina.database.ui.DataEditorPage;
import cuina.database.ui.IDatabaseEditor;
import cuina.editor.core.CuinaCore;
import cuina.editor.core.CuinaProject;
import cuina.map.Map;
import cuina.map.MapInfo;
import cuina.resource.ResourceException;
import cuina.resource.SerializationManager;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;

@Deprecated
public class MapEditorDelegationPage implements DataEditorPage<MapInfo>
{
	private TerrainEditor editor;
	private IDatabaseEditor context;
	private MapInfo info;
	private Composite parent;
	
	public MapEditorDelegationPage()
	{
		this.editor = new TerrainEditor();
		editor.addPropertyListener(new IPropertyListener()
		{
			@Override
			public void propertyChanged(Object source, int propId)
			{
				if (propId == IEditorPart.PROP_DIRTY)
					context.fireDataChanged(editor, info);
			}
		});
	}

	@Override
	public void setValue(MapInfo obj)
	{
		try
		{
			IFile file = getPreferredDataFile(obj.getKey());
			if (file == null)
			{
				createNewMap(obj);
			}
			editor.init(context.getEditorSite(), new MapEditorImput(file));
			this.info = obj;
			// Wir brauchen den Input bevor die Gui erstellt wird.
			editor.createPartControl(parent);
		}
		catch (ResourceException | PartInitException e)
		{
			e.printStackTrace();
			this.info = null;
		}
	}

	private void createNewMap(MapInfo obj) throws ResourceException
	{
		IFile file = getMapFolder().getFile(obj.getKey() + ".cxm");
		SerializationManager.save(new Map(obj.getKey(), 20, 15), file);
	}

	@Override
	public void setChildValue(Object obj) {}

	@Override
	public MapInfo getValue()
	{
		if (info != null) editor.doSave(null);
		return info;
	}

	@Override
	public void createEditorPage(Composite parent, IDatabaseEditor context)
	{
		this.parent = parent;
		this.context = context;
	}
	
	private IFolder getMapFolder()
	{
		CuinaProject project = context.getProject();
		return project.getProject().getFolder(
				project.getIni().get(Activator.PLUGIN_ID, Activator.MAPS_DIRECTORY_ID, "maps"));
	}
	
	private IFile getPreferredDataFile(String name) throws ResourceException
	{
		IFolder folder = getMapFolder();
		IFile found = null;
		
		try
		{
			IResource[] elements = folder.members();
			for (IResource r : elements)
			{
				if (r instanceof IFile && r.getName().startsWith(name))
				{
					String ext = r.getFileExtension();
					if (ext != null && ext.equals(SerializationManager.getDefaultExtension()) )
					{
						return (IFile) r;
					}
					if (found == null) found = (IFile) r;
				}
			}
			return found;
		}
		catch (CoreException e)
		{
			throw new ResourceException("Map '" + name + "' not found!", e);
		}
	}
	
	static class MapEditorImput implements IEditorInput
	{
		private IFile file;
		
		public MapEditorImput(IFile file)
		{
			this.file = file;
		}

		@Override
		public Object getAdapter(Class adapter)
		{
			if (adapter.equals(IFile.class)) return file;
			if (adapter.equals(IProject.class)) return file.getProject();
			if (adapter.equals(CuinaProject.class)) return CuinaCore.getCuinaProject(file.getProject());
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
	}
}
