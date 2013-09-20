package cuina.editor.gui.internal;

import cuina.editor.gui.internal.tree.WidgetTreeEditor;
import cuina.editor.gui.internal.tree.WidgetTreeEditorListener;
import cuina.editor.ui.AbstractSelectionPanel;
import cuina.gl.GC;
import cuina.gl.PaintListener;
import cuina.widget.WidgetFactory;
import cuina.widget.data.WidgetNode;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.lwjgl.LWJGLException;

import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;

public class WidgetEditorViewer extends AbstractSelectionPanel
{
	private URL themeURL;
	private LWJGLRenderer renderer;
	private GUI gui;
//	private WidgetTree tree;
	private Widget rootWidget;
//	private WidgetNode updateTarget;

	private WidgetFactory factory;
	private WidgetTreeEditor treeEditor;
	private WidgetTreeEditorListener treeListener;
	
	private final Map<WidgetNode, Widget> nodeMap = new HashMap<WidgetNode, Widget>();
	
	public WidgetEditorViewer(Composite parent, int width, int height)
	{
		super(parent, width, height);
		addPaintListener(getPaintListener());
		
		try
		{
			this.renderer = new LWJGLRenderer();
			renderer.setUseSWMouseCursors(true);
			rootWidget = new Widget();
			rootWidget.setTheme("");
		}
		catch (LWJGLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setWidgetTreeEditor(WidgetTreeEditor treeEditor)
	{
		if (this.treeEditor != null)
			treeEditor.removeWidgetTreeEditorListener(treeListener);
		this.treeEditor = treeEditor;
		if (treeEditor == null) return;

		treeEditor.addWidgetTreeEditorListener(getWidgetTreeEditorListener());
	}

	public WidgetTreeEditorListener getWidgetTreeListener()
	{
		return treeListener;
	}
	
	public URL getThemeURL()
	{
		return themeURL;
	}

	public void setThemeURL(URL themeURL)
	{
		this.themeURL = themeURL;
	}

	public WidgetFactory getWidgetFactory()
	{
		return factory;
	}

	public void setWidgetFactory(WidgetFactory factory)
	{
		this.factory = factory;
	}
	
	public Widget getWidget(WidgetNode widgetNode)
	{
		return nodeMap.get(widgetNode);
	}
	
	public WidgetNode findWidgetNode(Widget widget)
	{
		for (WidgetNode node : nodeMap.keySet())
		{
			if (nodeMap.get(node) == widget) return node;
		}
		return null;
	}

//	public void setInput(WidgetTree tree)
//	{
////		this.tree = tree;
//		this.gui = null;
//		this.rootWidget = null;
//		
//		refresh();
//	}

	public LWJGLRenderer getRenderer()
	{
		return renderer;
	}

	private PaintListener getPaintListener()
	{
		return new PaintListener()
		{
			@Override
			public void paint(GC gc)
			{
//				if (rootWidget == null) return;
				
				if (gui == null) createGUI();
				
//				if (updateTarget != null)
//				{
//					rebuildTree(updateTarget);
//					updateTarget = null;
//				}
				
				paintTWL();
				paintCursor(gc);
			}
		};
	}
	
//	@Override
//	public void refresh()
//	{
//		refresh(rootWidget);
//		
//		super.refresh();
//	}
	
//	public void refresh(WidgetNode data)
//	{
//		updateTarget = data;
//		
//		super.refresh();
//	}
	
//	private void rebuildTree(WidgetNode data)
//	{
//		WidgetItem item = tree.get(data);
//		
//		for(WidgetNode child : item.parent.node.children)
//		{
//			if (child == data)
//			{
//				factory.reapply(item.widget, data);
//				return;
//			}
//		}
//		
//		item.widget.removeAllChildren();
//		item.widget.destroy();
//		item.children.clear();
//	}
	
	private void createGUI()
	{
		try
		{
			gui = new GUI(rootWidget, renderer, null);
			gui.applyTheme(ThemeManager.createThemeManager(themeURL, renderer));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public GUI getGui()
	{
		return gui;
	}

	private void paintTWL()
	{
		Rectangle bounds = getViewportBounds();
		renderer.setViewport(0, 0, bounds.width, bounds.height);
		rootWidget.setSize(bounds.width, bounds.height);
		rootWidget.setPosition(-bounds.x, -bounds.y);
//		{
//			root.invalidateLayout();
//			root.reapplyTheme();
//		}
		gui.setSize();
		gui.updateTime();
//		gui.handleInput();
//		gui.handleKeyRepeat();
//		gui.handleTooltips();
		gui.updateTimers();
		gui.invokeRunables();
		gui.validateLayout();
		gui.draw();
	}
	
	public void dispose()
	{	
		gui.destroy();
		gui = null;
		rootWidget = null;
	}
	
	private WidgetTreeEditorListener getWidgetTreeEditorListener()
	{
		if (this.treeListener == null)
			this.treeListener = new WidgetTreeEditorListenerImpl();
		
		return this.treeListener;
	}
	
	class WidgetTreeEditorListenerImpl implements WidgetTreeEditorListener
	{
		@Override
		public void widgetAdded(WidgetTreeEditor treeEditor, WidgetNode parentNode, WidgetNode node)
		{
			Widget widget = factory.createWidget(node);
			Widget parent = getWidget(parentNode);
			if (parent == null)
			{
				parent = rootWidget;
				widget.setPosition(16, 16);
				setViewSize(widget.getRight() + 32, widget.getBottom() + 32);
			}
			parent.add(widget);
			nodeMap.put(node, widget);
			refresh();
		}
	
		@Override
		public void widgetRemoved(WidgetTreeEditor treeEditor, WidgetNode parentNode, WidgetNode node)
		{
			Widget widget = getWidget(node);
			widget.getParent().removeChild(widget);
			widget.destroy();
			nodeMap.remove(node);
			refresh();
		}
	
		@Override
		public void widgetChanged(WidgetTreeEditor treeEditor, WidgetNode node)
		{
			Widget widget = getWidget(node);
			factory.reapply(widget, node);
			widget.invalidateLayout();
			refresh();
		}
	}
	
//	private static class WidgetItem
//	{
//		private WidgetItem parent;
//		private List<WidgetItem> children;
//		private WidgetNode node;
//		private Widget widget;
//		
//		public WidgetItem(WidgetItem parent, WidgetNode node)
//		{
//			this.node = node;
//			this.parent = parent;
//			if (parent != null) parent.children.add(this);
//		}
//
//		public WidgetItem getParent()
//		{
//			return parent;
//		}
//
//		public WidgetNode getWidgetNode()
//		{
//			return node;
//		}
//
//		public Widget getWidget()
//		{
//			return widget;
//		}
//
//		public void setWidget(Widget widget)
//		{
//			this.widget = widget;
//		}
//	}
}
