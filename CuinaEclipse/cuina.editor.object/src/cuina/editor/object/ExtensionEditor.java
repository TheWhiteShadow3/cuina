package cuina.editor.object;

import cuina.editor.object.internal.IExtensionContext;

import org.eclipse.swt.widgets.Composite;

public interface ExtensionEditor<E>
{
	public E getData();
	public void setData(E value);
	public void init(IExtensionContext context);
	public void createComponents(Composite parent);
}
