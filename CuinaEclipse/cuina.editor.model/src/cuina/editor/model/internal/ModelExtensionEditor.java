package cuina.editor.model.internal;

import cuina.animation.ModelData;
import cuina.editor.object.ExtensionEditor;
import cuina.editor.object.internal.IExtensionContext;

import org.eclipse.swt.widgets.Composite;

public class ModelExtensionEditor implements ExtensionEditor<ModelData>
{
	private ModelData model;
	
	@Override
	public ModelData getData()
	{
		return model;
	}

	@Override
	public void setData(ModelData model)
	{
		this.model = model;
	}

	@Override
	public void init(IExtensionContext context)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createComponents(Composite parent)
	{
		// TODO Auto-generated method stub
		
	}

}
