package utils;



import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import cuina.FrameTimer;
import cuina.Game;
import cuina.Logger;
import cuina.audio.AudioSystem;
import cuina.database.Database;
import cuina.graphics.Graphics;
import cuina.plugin.PluginManager;
import cuina.script.ScriptExecuter;
import cuina.util.LoadingException;

import java.io.FileNotFoundException;

/**
 * Initialisiert Teile der Engine, die zum Testen der Scriptengine benötigt werden.
 * @author TheWhiteShadow
 */
public class TestStartupManager
{
//	public static final int GAME		= 1;
	public static final int DATABASE	= 2;
	public static final int PLUGINS		= 4;
	public static final int SCRIPTS 	= 8;
	public static final int SOUND		= 16;
	public static final int GRAPHIC		= 32;
	public static final int SESSION		= 64;
	public static final int GAME_LOOP	= 128;
	public static final int ALL			= 255;
	
	public static Game testInstance;
	
	public static void setTestProject(String path)
	{
		System.setProperty(Game.CUINA_GAMEPATH_KEY, path);
	}
	
	public static void setupTests(int modules)
	{
		initLogging();
		initGame();
		
		if ((modules & DATABASE) != 0) loadDatabase();
		if ((modules & PLUGINS) != 0) loadPlugins();
		if ((modules & SCRIPTS) != 0)
			startScriptExecuter();
		else
			Game.getIni().set("Game", "Main-Script", null);
		if ((modules & SOUND) != 0) AudioSystem.start();
		if ((modules & GRAPHIC) != 0) initGraphics();
		if ((modules & SESSION) != 0) Game.newGame();
		if ((modules & GAME_LOOP) != 0) FrameTimer.run();
	}
	
	public static void shutdown()
	{
		testInstance.dispose();
		testInstance = null;
	}
	
	private static void startScriptExecuter()
	{
		ScriptExecuter.init();
		ScriptExecuter.loadScripts();
		assertTrue(ScriptExecuter.getState() == ScriptExecuter.STARTED);
	}
	
	private static void loadDatabase()
	{
		assertNotNull(Game.CUINA_GAMEPATH_KEY + " nicht gesetzt", System.getProperty(Game.CUINA_GAMEPATH_KEY));
		
		testInstance.loadDatabase();
		assertTrue("Keine Database geladen.", Database.getTables().size() > 0);
	}
	
	private static void loadPlugins()
	{
		assertNotNull(PluginManager.CUINA_PLUGINPATH_KEY + " nicht gesetzt", 
				System.getProperty(PluginManager.CUINA_PLUGINPATH_KEY));
		
		testInstance.loadPlugins();
		assertTrue("Keine Plugins geladen.", PluginManager.getPluginFiles().size() > 0);
	}
	
	private static void initLogging()
	{
		Logger.logLevel = Logger.DEBUG;
		
		try
		{	// erzeugt nur Konsolenausgaben.
			Logger.setLogFile(null);
		}
		catch (FileNotFoundException e)
		{ fail("Logger akzeptiert kein null-File"); }
	}
	
	private static void initGame()
	{
		testInstance = new Game();
		try
		{
			testInstance.loadConfig();
		}
		catch (LoadingException e)
		{
			fail("Game-Instanz konnte nicht initialisiert werden.");
		}
	}
	
	private static void initGraphics()
	{
		Graphics.init("JUnit-Test running...");
	}
}
