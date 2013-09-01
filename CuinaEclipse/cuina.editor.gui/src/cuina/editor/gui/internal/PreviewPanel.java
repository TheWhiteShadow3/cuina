package cuina.editor.gui.internal;

import cuina.editor.ui.AbstractSelectionPanel;
import cuina.gl.GC;
import cuina.gl.PaintListener;

import java.io.IOException;
import java.net.URL;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.lwjgl.LWJGLException;

import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;

public class PreviewPanel extends AbstractSelectionPanel
{
	private GUI gui;
	private LWJGLRenderer renderer;
	private Widget root;
	private Widget widget;
	private URL themeURL;
//	private GLInput input;
	
	public PreviewPanel(Composite parent, int width, int height)
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
	
	public void setWidget(Widget widget, URL themeURL)
	{
		this.widget = widget;
		this.themeURL = themeURL;
		this.root = null;
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
				if (widget == null) return;
				
				if (gui == null) createGUI();
				
				paintTWL();
				paintCursor(gc);
			}
		};
	}
	
	private void createGUI()
	{
		try
		{
			root = new Widget();
			root.setTheme("");
			root.add(widget);
			
			gui = new GUI(root, renderer, null);
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

	public Widget getRoot()
	{
		return root;
	}

	private void paintTWL()
	{
		Rectangle bounds = getViewportBounds();
		renderer.setViewport(0, 0, bounds.width, bounds.height);
		root.setSize(bounds.width, bounds.height);
		root.setPosition(-bounds.x, -bounds.y);
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
		root = null;
	}
}
