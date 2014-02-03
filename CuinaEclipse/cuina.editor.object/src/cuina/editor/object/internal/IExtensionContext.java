package cuina.editor.object.internal;

import cuina.editor.core.CuinaProject;
import cuina.object.ObjectData;

public interface IExtensionContext
{
	public ObjectData getObjectData();
	
	public CuinaProject getCuinaProject();
}
