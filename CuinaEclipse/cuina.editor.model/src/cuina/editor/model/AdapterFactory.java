package cuina.editor.model;

import cuina.animation.ModelData;
import cuina.editor.object.ObjectAdapter;
import cuina.editor.object.ObjectGraphic;

import org.eclipse.core.runtime.IAdapterFactory;

public class AdapterFactory implements IAdapterFactory
{
	private static final Class[] ADAPTER_LIST = new Class[] {ModelData.class};
	
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType)
	{
		if (adapterType == ObjectGraphic.class && adaptableObject instanceof ObjectAdapter)
		{
			return new ModelGraphic((ObjectAdapter) adaptableObject);
		}
		return null;
	}

	@Override
	public Class[] getAdapterList()
	{
		return ADAPTER_LIST;
	}
}
