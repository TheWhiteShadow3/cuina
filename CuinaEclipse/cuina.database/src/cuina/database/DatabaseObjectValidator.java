package cuina.database;

import org.eclipse.core.resources.IFile;

public interface DatabaseObjectValidator
{
	public boolean validate(IFile file, DataTable<?> table);
}
