package cuina.widget;

import cuina.Game;
import cuina.Logger;
import cuina.graphics.GLCache;
import cuina.graphics.Graphic;
import cuina.graphics.GraphicContainer;
import cuina.graphics.GraphicSet;
import cuina.graphics.Graphics;
import cuina.util.LoadingException;
import cuina.util.ResourceManager;

import java.io.IOException;
import java.net.URL;

import org.lwjgl.LWJGLException;

import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;

public class WidgetContainer implements Graphic
{
	private static final long serialVersionUID = -8465714659505831740L;
	
	public static final String TWL_RESOURE_PATH = "cuina.twl.path";
	public static final String DEFAULT_THEME = Game.getIni().get("TWL", "theme.path");
	private static final int DEPTH = 100;
	
	private static LWJGLRenderer renderer;
	private static ThemeManager themeManager;
	private static GUI gui;
	
	private static final GraphicContainer WIDGET_CONTAINER =
			new GraphicSet("cuina.widgets", DEPTH, Graphics.GraphicManager);
	
//	private static WidgetContainer instance;
	
	private transient Widget root;
	private GraphicContainer container;
	private WidgetDescriptor descriptor;
	
//	public static WidgetContainer getInstance()
//	{
//		if (instance == null)
//		{
//			instance = new WidgetContainer();
//		}
//		return instance;
//	}
	
//	public void addWidgetDescriptor(WidgetDescriptor descriptor)
//	{
//		
//	}
	
	public WidgetContainer(WidgetDescriptor descriptor)
	{
		if (descriptor == null) throw new NullPointerException();
		this.descriptor = descriptor;
		
		WIDGET_CONTAINER.addGraphic(this);
		refresh();
	}
	
//	public WidgetContainer(WidgetDescriptor descriptor)
//	{
//		this(descriptor, WIDGET_CONTAINER);
//	}
	
	public WidgetDescriptor getDescriptor()
	{
		return descriptor;
	}

	@Override
	public void setContainer(GraphicContainer container)
	{
		this.container = container;
	}

	@Override
	public GraphicContainer getContainer()
	{
		return container;
	}

	@Override
	public int getDepth()
	{
		return 0;
	}
	
	/**
	 * Gibt das Wurzel-Element zurück.
	 * Der Rückgabewert kann null sein!
	 * @return das Wurzel-Element oder null.
	 */
	public Widget getRoot()
	{
		return root;
	}
	
	public void setVisible(boolean value)
	{
		root.setVisible(value);
	}
	
	public boolean isVisible()
	{
		return root.isVisible();
	}
	
	@Override
	public void refresh()
	{
		try
		{
			if (renderer == null)
			{
				createGUI();
			}
			
			this.root = descriptor.createRoot();
			gui.getRootPane().add(root);
			descriptor.postBuild();
		}
		catch (LWJGLException | IOException e)
		{
			Logger.log(WidgetContainer.class, Logger.ERROR, e);
		}
	}
	
	private void createGUI() throws LWJGLException, IOException
	{
		renderer = new LWJGLRenderer();
		renderer.setFontMapper(new CuinaFontMapper(renderer));
		themeManager = ThemeManager.createThemeManager(getThemeURL(), renderer);
		
		gui = new GUI(renderer);
		gui.applyTheme(themeManager);
	}
	
	private URL getThemeURL() throws LoadingException
	{
		String theme = descriptor.getTheme();
		if (theme == null) theme = DEFAULT_THEME;
		return ResourceManager.getResource(TWL_RESOURE_PATH, DEFAULT_THEME).getURL();
	}

	@Override
	public void draw()
	{
		if (gui != null)
		{
			renderer.setViewport(0, 0, Graphics.getWidth(), Graphics.getHeight());
			
			gui.update();

			GLCache.restore();
		}
	}

	@Override
	public void dispose()
	{
		if(root != null)
		{
			root.destroy();
			gui.getRootPane().removeChild(root);
		}
	}
}
