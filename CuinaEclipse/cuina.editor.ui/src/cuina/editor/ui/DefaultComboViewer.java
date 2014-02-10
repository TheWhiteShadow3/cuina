package cuina.editor.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

import cuina.database.NamedItem;

public class DefaultComboViewer<E> extends ComboViewer
{
	/** NULL-Element. */
	private static Object NULL = new String();
	
	public DefaultComboViewer(Composite parent, int style)
	{
		super(parent, style);
		setContentProvider(ArrayContentProvider.getInstance());
		setLabelProvider(new DefaultLabelProvider());
	}
	
	public void setList(List list, boolean withNull)
	{
		if (withNull)
		{
			list = new ArrayList(list);
			list.add(0, NULL);
		}
		super.setInput(list);
	}
	
	/**
	 * Gibt das ausgewählte Element zurück oder <code>null</code>, wenn keins ausgewählt ist.
	 * @return ausgewählte Element.
	 */
	public E getSelectedElement()
	{
		Object obj = ((IStructuredSelection) getSelection()).getFirstElement();
		if (obj == NULL) return null;
		return (E) obj;
	}
	
	public void setSelectedElement(Object obj)
	{
		if (obj == null) obj = NULL;
		setSelection(new StructuredSelection(obj));
	}
	
	private static class DefaultLabelProvider extends LabelProvider
	{
		@Override
		public String getText(Object element)
		{
			if (element instanceof NamedItem)
				return ((NamedItem) element).getName();
			
			return super.getText(element);
		}
	}
}
