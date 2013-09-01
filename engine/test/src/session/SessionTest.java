package session;

import cuina.Context;
import cuina.FrameTimer;
import cuina.Game;
import cuina.InjectionManager;
import cuina.Scene;
import cuina.graphics.Graphics;

import java.io.File;

import static junit.framework.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utils.TestStartupManager;

public class SessionTest
{
	private static final File SAVE_FILE = new File("test/junit_save.ser");
	
	@BeforeClass
	public static void setUp()
	{
		TestStartupManager.setupTests(TestStartupManager.DATABASE | TestStartupManager.GRAPHIC);
	}
	
	@AfterClass
	public static void tearDown()
	{
		TestStartupManager.shutdown();
	}
	
	/**
	 * Getestet wird das Speichern und Laden einer Session.
	 * Hier wird speziell auf den Zustand der Grafiken im GraphicManager geachtet.
	 * <ul>
	 * <li>Zuerst werden drei Grafiken erzeugt. Jede in einem anderen Kontext.</li>
	 * <li>Eine Szene wird erstellt, danach wird die Session abgespeichert.</li>
	 * <li>Anschließend wird die Szene gewechselt. Danach wurde die Grafik im Szene-Kontext disposed.</li>
	 * <li>Nun wird die Session wiederhergestellt. Es sind alle drei Grafiken wirde vorhanden.</li>
	 * </ul>
	 */
	@Test
	public void testSession()
	{
		Game.newGame();
		Game.newScene("junit"); // ab hier existiert die Szene.
		
		// Jeder Kontext bekommt ein Objekt mit einer Grafik injiziert.
		InjectionManager.injectObject(new GlobalObject(), "global_object", Scene.ALL_SCENES, Context.GLOBAL);
		InjectionManager.injectObject(new SessionObject(), "session_object", Scene.ALL_SCENES, Context.SESSION);
		InjectionManager.injectObject(new SceneObject(), "scene_object", Scene.ALL_SCENES, Context.SCENE);
		// Ab hier ist nur die globale Grafik und der Session-Kontainer vorhanden.
		assertEquals(2, Graphics.GraphicManager.toList().size());
		
		FrameTimer.nextFrame(); // ab hier wird die Szene initialisiert.
		assertEquals(4, Graphics.GraphicManager.toList().size());
		
		Game.saveGame(SAVE_FILE);
		Game.endGame();
		
		FrameTimer.nextFrame();
		assertEquals(4, Graphics.GraphicManager.toList().size());
		
		Game.newScene("blub");
		
		FrameTimer.nextFrame();
		// Die lose Session-Grafik ist nun gelöscht.
		assertEquals(3, Graphics.GraphicManager.toList().size());
		
		Game.loadGame(SAVE_FILE);
		// Die Szene ist die gleiche wie zu Beginn
		assertEquals("junit", Game.getScene().getName());
		// Es sollten wieder alle 4 Grafiken im Manager sein.
		assertEquals(4, Graphics.GraphicManager.toList().size());
		
		assertTrue("Session Grafik ist null!", Graphics.GraphicManager.toList().contains(SessionObject.SESSION_SET));
		assertNotNull("Session Grafik ist null!", Graphics.GraphicManager.getContainer("SessionSet"));
		
		FrameTimer.nextFrame();
	}
}
