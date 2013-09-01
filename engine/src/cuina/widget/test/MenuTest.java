package cuina.widget.test;

import cuina.Game;
import cuina.Input;
import cuina.graphics.Graphics;
import cuina.graphics.Panorama;
import cuina.widget.WidgetContainer;
import cuina.widget.WidgetDescriptor;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import de.matthiasmann.twl.Widget;

@SuppressWarnings("serial")
public class MenuTest implements WidgetDescriptor
{
	private TestMenu testMenu;
	
	@SuppressWarnings("unused")
	private static WidgetContainer container;
	private static Panorama background;

	public static void main(String[] args)
	{
		try
		{
			Game game = new Game();
			game.loadConfig();
			
			Graphics.setupDisplay("TWL Menu Demo", new DisplayMode(800, 600));
			Graphics.getInstance().createDisplay();
			// Display.setVSyncEnabled(true);

			background = new Panorama("backgrounds/BlueSky.jpg");
			background.setSpeedX(1);
			background.setSpeedY(1);
			
			container = new WidgetContainer(new MenuTest());

			while (!Graphics.isCloseRequested())
			{
				Input.update();
				
				Graphics.update();
				
				GL11.glGetError(); // force sync with multi threaded GL driver
				Display.sync(60); // ensure 60Hz even without vsync
				Display.processMessages(); // now process inputs
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		Graphics.dispose();
	}

	@Override
	public Widget createRoot()
	{
		testMenu =  new TestMenu();	
		return testMenu;
	}

	@Override
	public String getTheme()
	{
		return null;
	}

	@Override
	public Widget getWidget(String key)
	{
		return testMenu;
	}

	@Override
	public void postBuild()
	{
		testMenu.menu.requestKeyboardFocus();
	}
}
