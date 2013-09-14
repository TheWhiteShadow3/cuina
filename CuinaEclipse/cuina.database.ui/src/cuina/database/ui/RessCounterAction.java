package cuina.database.ui;

import cuina.database.Database;
import cuina.database.ui.ReferenceCounter.Reference;
import cuina.editor.core.CuinaCore;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class RessCounterAction extends AbstractHandler
{
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
		Object obj = selection.getFirstElement();
		
		if (obj instanceof IProject)
		{
			IProject project = (IProject) obj;
			try
			{
				if (project.getNature(CuinaCore.NATURE_ID) == null) return null;
			}
			catch (CoreException e)
			{
				e.printStackTrace();
			}
			
			Database db = CuinaCore.getCuinaProject(project).getService(Database.class);
			ReferenceCounter counter = new ReferenceCounter();
			counter.scanDatabase(db);
			Reference[] refs = counter.getReferences();
			for (Reference ref : refs)
			{
				System.out.println(ref);
			}
			ResourceView view = new ResourceView(window.getShell(), project, refs);
			view.open();
		}
		return null;
	}
}
