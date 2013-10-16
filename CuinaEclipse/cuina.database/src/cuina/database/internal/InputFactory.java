package cuina.database.internal;

import cuina.database.DatabaseInput;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

public class InputFactory implements IElementFactory
{
	@Override
	public IAdaptable createElement(IMemento memento)
	{
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(memento.getString("file")));
		String key = memento.getString("key");
		
		return new DatabaseInput(file, key);
	}
}
