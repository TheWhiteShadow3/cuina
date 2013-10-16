package cuina.database;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.graphics.Image;

public interface IDatabaseDescriptor<E extends DatabaseObject>
{
	public String getName();

	public IConfigurationElement getConfiguration();

	public Class<E> getDataClass();

	public Image getImage();
	
	public String getEditorID();
}
