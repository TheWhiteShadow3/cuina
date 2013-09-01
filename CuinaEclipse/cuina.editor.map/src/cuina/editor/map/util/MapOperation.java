package cuina.editor.map.util;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class MapOperation extends AbstractOperation
{
	private MapSavePoint undoPoint;
	private MapSavePoint redoPoint;
	
	public MapOperation(String label, MapSavePoint undoPoint, MapSavePoint redoPoint)
	{
		super(label);
		addContext(MapContext.INSTANCE);
		
		this.redoPoint = redoPoint;
		this.undoPoint = undoPoint;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException
	{
		return redo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException
	{
		redoPoint.apply();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException
	{
		undoPoint.apply();
		return Status.OK_STATUS;
	}
	
	public static class MapContext implements IUndoContext
	{
		public static final MapContext INSTANCE = new MapContext();
		
		private MapContext() {}
		
		@Override
		public String getLabel()
		{
			return "Map";
		}

		@Override
		public boolean matches(IUndoContext context)
		{
			return context == this;
		}
	}
}
