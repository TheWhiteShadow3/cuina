package cuina.editor.eventx.internal;

import cuina.database.DataTable;
import cuina.database.DatabaseObjectValidator;
import cuina.database.ui.DatabaseUtil;
import cuina.editor.core.CuinaCore;
import cuina.eventx.Command;
import cuina.eventx.CommandList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

public class FlowValidator implements DatabaseObjectValidator
{
	private IFile file;
	private CommandLibrary library;
	private Map<String, IMarker> markers = new HashMap<String, IMarker>();
	
	@Override
	public boolean validate(IFile file, DataTable<?> table)
	{
		this.file = file;
		this.library = CuinaCore.getCuinaProject(file.getProject()).getService(CommandLibrary.class);
		for(CommandList list : ((DataTable<CommandList>) table).values())
		{
			readMarkers(table, list);
			validate(list);
		}
		return true;
	}

	private void validate(CommandList list)
	{
		for (int i = 0; i < list.commands.length; i++)
		{
			Command cmd = list.commands[i];
			FunctionEntry func = library.getFunction(cmd);
			IMarker marker;
			marker = getMarker(list, i);
			if (func == null)
			{
				if (marker == null) try
				{
					marker = file.createMarker(IMarker.PROBLEM);
					marker.setAttribute("index", i);
					marker.setAttribute(IMarker.LOCATION, list.getKey());
					marker.setAttribute(IMarker.MESSAGE,
							"Unbekannter Befehl: " + cmd.target + '.' + cmd.name);
					addMarker(list, i, marker);
				}
				catch (CoreException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				if (marker != null) try
				{
					marker.delete();
					removeMarker(list, i);
				}
				catch (CoreException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	private IMarker getMarker(CommandList list, int index)
	{
		return this.markers.get(list.getKey() + '$' + index);
	}
	
	private void removeMarker(CommandList list, int index)
	{
		this.markers.remove(list.getKey() + '$' + index);
	}
	
	private void addMarker(CommandList list, int index, IMarker marker)
	{
		this.markers.put(list.getKey() + '$' + index, marker);
	}
	
	private void readMarkers(DataTable<?> table, CommandList list)
	{
		try
		{
			List<IMarker> markerList = DatabaseUtil.getDatabaseMarker(table, list.getKey());
			for(IMarker m : markerList)
			{
				Integer i = (Integer) m.getAttribute("index");
				if (i != null)
					addMarker(list, i, m);
			}
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}
}
