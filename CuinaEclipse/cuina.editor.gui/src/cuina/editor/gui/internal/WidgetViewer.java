package cuina.editor.gui.internal;

import cuina.widget.data.WidgetNode;
import cuina.widget.data.WidgetTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;

import de.matthiasmann.twl.Widget;

public class WidgetViewer
{
	private GLCanvas glCanvas;
	private WidgetTree tree;
	
//	private final List<TreeListener> treeListener = new ArrayList<TreeListener>();
	
	
	public WidgetViewer(GLCanvas glCanvas)
	{
		this.glCanvas = glCanvas;
	}
	
	public WidgetTree getWidgetTree()
	{
		return tree;
	}

	public void setWidgetTree(WidgetTree tree)
	{
		this.tree = tree;
		refresh();
	}

	public void refresh()
	{
		refresh(tree.root);
	}
	
	public void refresh(WidgetNode node)
	{
		
	}
}
