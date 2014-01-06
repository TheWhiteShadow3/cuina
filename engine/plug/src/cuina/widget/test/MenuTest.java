package cuina.widget.test;

import cuina.FrameTimer;
import cuina.Game;
import cuina.InjectionManager;
import cuina.Logger;
import cuina.graphics.Graphic;
import cuina.graphics.GraphicContainer;
import cuina.graphics.Graphics;
import cuina.graphics.Images;
import cuina.graphics.Panorama;
import cuina.graphics.PictureSprite;
import cuina.graphics.Sprite;
import cuina.graphics.TextureLoader;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class MenuTest
{
	private static Panorama background;
	private static Sprite sprite;
	
	@SuppressWarnings("serial")
	public static void main(String[] args)
	{
		try
		{
			Logger.setLogFile(null);
			Logger.logLevel = Logger.DEBUG;
			
			Game game = new Game();
			game.loadConfig();
			game.initStartingState();
			
			Graphics.setupDisplay("TWL Menu Demo", new DisplayMode(800, 600));
			Graphics.getInstance().createDisplay();
			
			TestMenu testMenu = new TestMenu();
			InjectionManager.addObject(testMenu, "testMenu");
			

			
			background = new Panorama("backgrounds/BlueSky.jpg");
			background.setSpeedX(0.5f);
			background.setSpeedY(0.4f);
			
			FrameTimer.nextFrame();
			
			for(Graphic g : Graphics.GraphicManager.toList())
			{
				System.out.println(g);
				if (g instanceof GraphicContainer)
					System.out.println("\t" + ((GraphicContainer) g).toList());
			}
		
			GL11.glGetError(); // force sync with multi threaded GL driver
			Display.sync(60); // ensure 60Hz even without vsync
			Display.setVSyncEnabled(true);
			System.out.println("Start loop");
			while (!Graphics.isCloseRequested())
			{
				FrameTimer.nextFrame();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		Graphics.dispose();
	}
}
