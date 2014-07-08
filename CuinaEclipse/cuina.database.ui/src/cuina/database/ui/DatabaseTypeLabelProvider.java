package cuina.database.ui;

import cuina.database.IDatabaseDescriptor;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class DatabaseTypeLabelProvider extends LabelProvider
{
	@Override
	public Image getImage(Object element)
	{
		return ((IDatabaseDescriptor) element).getImage();
	}

	@Override
	public String getText(Object element)
	{
		return ((IDatabaseDescriptor) element).getName();
	}
}
