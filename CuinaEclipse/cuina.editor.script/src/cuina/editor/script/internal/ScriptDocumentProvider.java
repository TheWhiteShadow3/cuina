package cuina.editor.script.internal;

import cuina.database.DataTable;
import cuina.database.DatabaseInput;
import cuina.editor.script.Scripts;
import cuina.resource.ResourceException;
import cuina.script.Script;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;

public class ScriptDocumentProvider extends AbstractDocumentProvider
{
	private DatabaseInput input;
	private Script script;

	@Override
	protected IDocument createDocument(Object element) throws CoreException
	{
		if (!(element instanceof DatabaseInput))
		throw new CoreException(new Status(IStatus.ERROR, Scripts.PLUGIN_ID, "Invalid Input-Type."));
	
		this.input = (DatabaseInput) element;
		try
		{
			this.script = (Script) input.getData();
		}
		catch (ResourceException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, Scripts.PLUGIN_ID, e.getMessage(), e));
		}
			
		return new Document(script.getCode());
	}

	@Override
	protected IAnnotationModel createAnnotationModel(Object element) throws CoreException
	{
		return new AnnotationModel();
	}

	@Override
	protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite) throws CoreException
	{
		try
		{
			DataTable table = input.getTable();
			table.getDatabase().saveTable(table);
		}
		catch (ResourceException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, Scripts.PLUGIN_ID, e.getMessage(), e));
		}
	}

	@Override
	protected IRunnableContext getOperationRunner(IProgressMonitor monitor)
	{
		return null;
	}
}
