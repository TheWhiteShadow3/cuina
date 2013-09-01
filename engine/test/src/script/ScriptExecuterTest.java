package script;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import cuina.FrameTimer;
import cuina.Game;
import cuina.database.DataTable;
import cuina.database.Database;
import cuina.script.MainScript;
import cuina.script.ScriptExecuter;

import org.jruby.Ruby;
import org.jruby.RubyFixnum;
import org.jruby.exceptions.RaiseException;
import org.jruby.internal.runtime.GlobalVariables;
import org.jruby.runtime.builtin.IRubyObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utils.TestStartupManager;


public class ScriptExecuterTest
{
	@BeforeClass
	public static void setUp()
	{
		TestStartupManager.setupTests(TestStartupManager.DATABASE + TestStartupManager.PLUGINS + TestStartupManager.SCRIPTS);
	}

	@AfterClass
	public static void tearDown()
	{
		ScriptExecuter.shutdown();
	}
	
	@After
	public void postTest()
	{	// beende Szene und Session, falls vorhanden
		Game.endGame();
		Game.newScene(null);
		FrameTimer.syncScene();
	}

	// testet Funktion des ScriptExecuters.
	@Test
	public void testEvaluation() throws RaiseException
	{
		IRubyObject result = ScriptExecuter.eval("a = 5\n");
		assertTrue(result instanceof RubyFixnum);
		RubyFixnum num = (RubyFixnum) result;
		assertTrue(num.getLongValue() == 5L);
	}
	
	// testet, ob die Skripte korrekt geladen wurden.
	@Test
	public void testScripts() throws RaiseException
	{
		assertTrue("Runtime nicht gesetzt!", Ruby.isGlobalRuntimeReady());
		Ruby runtime = Ruby.getGlobalRuntime();
		
		// Ist ein Hauptskript registriert?
		// Falls nicht, sollte die Script.cxd im JUnit-Projekt überprüft werden.
		MainScript main = null;
		DataTable<?> table = Database.getDataTable(ScriptExecuter.SCRIPT_DB);
		assertFalse("Skript-Tabelle ist leer.", table.isEmpty());
		
		for(String key : table.keySet())
		{
			@SuppressWarnings("deprecation")
			Object script = runtime.getCurrentContext().getConstant("OBJ_" + key);
			if (script instanceof MainScript) main = (MainScript) script;
		}
		assertNotNull("Haupt-Skript nicht gefunden.", main);
	}
	
	// testet ob die Libary korrekt geladen wurde.
	@Test
	public void testLibary()
	{
		assertTrue("Runtime nicht gesetzt!", Ruby.isGlobalRuntimeReady());
		Ruby runtime = Ruby.getGlobalRuntime();
		
		assertNotNull("Modul 'Cuina' nicht definiert!", runtime.getModule("Cuina"));
		
		GlobalVariables vars = runtime.getGlobalVariables();
		assertEquals("Variable $switches nicht definiert!", "Switch", vars.get("$switches").getType().getName());
		assertEquals("Variable $variables nicht definiert!", "Variable", vars.get("$variables").getType().getName());
	}
	
	// testet Skript-Zugriff auf den globalen Kontext
	@Test
	public void testGlobalContext()
	{
		String script = "GlobalContext['test'] = 1\nGlobalContext['test']";
		RubyFixnum result = (RubyFixnum) ScriptExecuter.eval(script);
		assertEquals(1, result.getLongValue());
	}
	
	// testet Skript-Zugriff auf den Session-Kontext
	@Test
	public void testSessionContext()
	{
		Game.newGame();
		
		String script = "SessionContext['test'] = 2\nSessionContext['test']";
		RubyFixnum result = (RubyFixnum) ScriptExecuter.eval(script);
		assertEquals(2, result.getLongValue());
	}
	
	// testet Skript-Zugriff auf den Szenen-Kontext
	@Test
	public void testSceneContext()
	{
		Game.newScene("JUnit");
		
		String script = "SceneContext['test'] = 3\nSceneContext['test']";
		RubyFixnum result = (RubyFixnum) ScriptExecuter.eval(script);
		assertEquals(3, result.getLongValue());
	}
}
