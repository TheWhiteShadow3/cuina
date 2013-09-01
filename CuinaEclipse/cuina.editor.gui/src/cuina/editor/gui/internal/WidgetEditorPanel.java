package cuina.editor.gui.internal;

import cuina.editor.ui.AbstractSelectionPanel;
import cuina.gl.GC;
import cuina.gl.PaintListener;
import cuina.widget.WidgetFactory;
import cuina.widget.data.WidgetNode;
import cuina.widget.data.WidgetTree;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.lwjgl.LWJGLException;

import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;

public class WidgetEditorPanel extends AbstractSelectionPanel
{
	private URL themeURL;
	private LWJGLRenderer renderer;
	private GUI gui;
	private WidgetFactory factory;
	private WidgetTree tree;
	private Widget rootWidget;
	private WidgetNode updateTarget;
	
	private final HashMap<WidgetNode, WidgetItem> contentMap = new HashMap<WidgetNode, WidgetItem>();
	
	public WidgetEditorPanel(Composite parent, int width, int height)
	{
		super(parent, width, height);
		addPaintListener(getPaintListener());
		
		try
		{
			this.renderer = new LWJGLRenderer();
			renderer.setUseSWMouseCursors(true);
		}
		catch (LWJGLException e)
		{
			e.printStackTrace();
		}
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

	public void setWidgetTree(WidgetTree tree)
	{
		this.tree = tree;
		this.rootWidget = null;
		this.gui = null;
		
		refresh();
	}

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
				if (rootWidget == null) return;
				
				if (gui == null) createGUI();
				
				if (updateTarget != null)
				{
					rebuildTree(updateTarget);
					updateTarget = null;
				}
				
				paintTWL();
				paintCursor(gc);
			}
		};
	}
	
	@Override
	public void refresh()
	{
		refresh(tree.root);
		
		super.refresh();
	}
	
	public void refresh(WidgetNode data)
	{
		updateTarget = data;
		
		super.refresh();
	}
	
	private void rebuildTree(WidgetNode data)
	{
		WidgetItem item = contentMap.get(data);
		
		for(WidgetNode child : item.parent.node.children)
		{
			if (child == data)
			{
				factory.reapply(item.widget, data);
				return;
			}
		}
		
		item.widget.removeAllChildren();
		item.widget.destroy();
		item.children.clear();
	}
	
	private void createGUI()
	{
		try
		{
			rootWidget = new Widget();
			rootWidget.setTheme("");
			
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
	
	private static class WidgetItem
	{
		private WidgetItem parent;
		private List<WidgetItem> children;
		private WidgetNode node;
		private Widget widget;
		
		public WidgetItem(WidgetItem parent, WidgetNode node)
		{
			this.node = node;
			this.parent = parent;
			if (parent != null) parent.children.add(this);
		}

		public WidgetItem getParent()
		{
			return parent;
		}

		public WidgetNode getWidgetNode()
		{
			return node;
		}

		public Widget getWidget()
		{
			return widget;
		}

		public void setWidget(Widget widget)
		{
			this.widget = widget;
		}
	}
}
