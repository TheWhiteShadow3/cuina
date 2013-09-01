package plugin;

import static org.junit.Assert.*;

import cuina.Context;
import cuina.FrameTimer;
import cuina.Game;
import cuina.InjectionManager;
import cuina.plugin.CuinaPlugin;
import cuina.plugin.PluginManager;

import java.util.Map;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import plugin.simple.SimplePlugin;
import utils.TestStartupManager;

public class PluginTest
{
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		TestStartupManager.setupTests(TestStartupManager.DATABASE + TestStartupManager.PLUGINS);
		InjectionManager.loadContextObjects(Context.GLOBAL);
	}

	@After
	public void postTest()
	{	// beende Szene und Session, falls forhanden
		Game.endGame();
		Game.newScene(null);
		FrameTimer.syncScene();
	}
	
	@Test
	public void testLoading()
	{
		Map<String, CuinaPlugin> plugins = PluginManager.getPluginFiles();
		
		CuinaPlugin sp = plugins.get("junit.simpleplugin.jar");
		
		assertNotNull(sp);
		assertNotNull(plugins.get("junit.depententplugin.jar"));
		assertNotNull(plugins.get("junit.recursive.dependency1.jar"));
		assertNotNull(plugins.get("junit.recursive.dependency2.jar"));
		assertNull(plugins.get("junit.invalidplugin.jar"));
		
		assertNotNull("Plugin Skriptlibary ist null.", sp.getScriptLib());
	}
	
	@Test
	public void testContextInjection()
	{
		assertNotNull(Game.getContext(Context.GLOBAL).get("junit_global"));
		assertFalse(Game.contextExists(Context.SESSION));
		assertFalse(Game.contextExists(Context.SCENE));
		
		Game.newGame();
		
		assertNotNull(Game.getContext(Context.SESSION).get("junit_session"));
		
		Game.newScene("junit");
		FrameTimer.syncScene();
		
		assertNotNull(Game.getContext(Context.SCENE).get("junit_scene"));
	}
	
	@Test
	public void testLifecycle()
	{
		Game.newScene("junit");
		FrameTimer.syncScene();
		
		SimplePlugin plugin = new SimplePlugin();
		Game.getScene().injectIntoScene(plugin, "blub");
		
		assertEquals(1, SimplePlugin.init_value);
		
		FrameTimer.nextFrame();
		
		assertEquals(1, SimplePlugin.update_value);
		assertEquals(1, SimplePlugin.post_update_value);
		
		Game.newScene(null);
		FrameTimer.syncScene();
		
		assertEquals(1, SimplePlugin.dispose_value);
	}
}
