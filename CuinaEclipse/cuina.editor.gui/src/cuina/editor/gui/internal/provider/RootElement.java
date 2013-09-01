package cuina.editor.gui.internal.provider;

/**
 * class to show the root widget in TreeViewer, otherwise it will only show the children
 * of root widget, but not the root widget itself
 *
 * @author fireandfuel
 *
 */
public class RootElement
{
	private Object[] children;
	
	public RootElement(Object... children)
	{
		this.children = children;
	}
	
	public Object[] getChildren()
	{
		return children;
	}
}