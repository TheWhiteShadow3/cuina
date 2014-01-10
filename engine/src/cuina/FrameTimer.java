package cuina;

import cuina.graphics.Graphics;
import cuina.input.Input;

import org.lwjgl.opengl.Display;

/**
 * Der FrameTimer sorgt dafür, dass das Spiel am Leben bleibt und Ein/Ausgaben
 * jeden Frame aktualisiert werden.
 * @author TheWhiteShadow
 */
public final class FrameTimer
{
	// Frame-Timing in Microsekunden
	private static long frameTime = 16666; // 16,666ms ~ 60 fps
//	private static long frameTimeout = 10000;
	private static long lastUpdate = 0;
	// FPS-Analyse
	private static long frameCount = 0;
	protected static long lastCall;
	private static long[] frameTimes = new long[20];
	private static int frameTimesPos = 0;
	private static Scene scene = null;
	private static boolean stop = true;
	private static boolean run = false;
	private static boolean inFrame = false;
	
	private FrameTimer() {}

	/**
	 * Startet den FrameTimer.
	 * <p>Zunächst wird überprüft ob eine Szene vorhanden ist.
	 * Wenn das nicht der fall ist, terminiert die Methode sofort.
	 * Gibt es eine Szene, wird diese initialisiert und Graphics.update() aufgerufen.
	 * Danach wird die Hauptschleife gestartet in der folgende Aktionen wiederholt statt finden:
	 * <ul>
	 * <li>Prüfen, ob die Schleife beendet wurde,
	 * z.B. durch schließen des Fensters oder aufruf der {@link #stop()}-Methode.</li>
	 * <li>Anhalten des Threads um die FPS zu gewährleisten.</li>
	 * <li>Aufruf von Input.update()</li>
	 * <li>Aufruf von Scene.update()</li>
	 * <li>Synchronisation der Frame-Szene mit der Game-Szene.</li>
	 * <li>Aufruf von Graphics.update()</li>
	 * </ul>
	 * </p>
	 * @see #nextFrame()
	 */
	public static void run()
	{
		if (run) return;
		run = true;
		stop = false;
		// setze Timeout-Referenzpunkt
		lastUpdate = System.nanoTime();
		
		if (scene == null)
		{
			scene = Game.getScene();
			if (scene == null) return;
			scene.init();
			
			Graphics.update();
			Logger.log(FrameTimer.class, Logger.INFO, "First Frame completed in: " + getTime() + " ms");
		}
		
		while (!Display.isCloseRequested() && !stop)
		{
			waitForNextFrame();
			executeFrame();
		}
		Logger.log(FrameTimer.class, Logger.INFO, "FrameTimer stopped");
		run = false;
	}
	
	private static void waitForNextFrame()
	{
		long sleepMillis = 0;
		long sleepNanos = 0;
		if (System.nanoTime() - lastUpdate <= frameTime * 1000)
		{
			long diff = frameTime * 1000 - (System.nanoTime() - lastUpdate);
			sleepMillis = diff / 1000000;
			sleepNanos = diff % 1000000;
			if(sleepNanos >= 500000 || sleepMillis <= 0) sleepMillis++;
			try
			{
				Thread.sleep(sleepMillis);
			}
			catch (InterruptedException e) {}
		}
	}
	
	private static void executeFrame()
	{
		inFrame = true;
		frameCount++;
		long now = System.nanoTime();
		long diff = now - lastUpdate;
		lastUpdate = now;
		// Callback-Timer, falls das Spiel hängt
		// TODO: Observer erstellen
		frameTimes[frameTimesPos] = diff;
		frameTimesPos = ++frameTimesPos % frameTimes.length;
		
		lastCall = System.nanoTime();
		// hier findet der Spiel-Frame statt
		Input.update();
		scene.update();
		
//		if (Input.isPressed(Keyboard.KEY_F11))
//			Game.saveGame(new File("save.ser"));
//		
//		if (Input.isPressed(Keyboard.KEY_F12))
//			Game.loadGame(new File("save.ser"));
		
		if (Game.getScene() != scene)
		{
			scene.dispose();
			scene = Game.getScene();
			if (scene == null)
			{
				stop = true;
				return;
			}
			scene.init();
		}

		// Frame-rate im Debug-Modus (kann erst hier ermittelt werden.)
		if (/*Game.isDebug() && */frameTimesPos == 0)
			Display.setTitle("FPS: " + FPS() + " - " + Game.getTitle() + ": " + scene.getName() + " (Debug-Modus)");

		Graphics.update();
		inFrame = false;
	}
	
	/**
	 * Führt einen einzelnen Frame aus.
	 * Die Methode ruft zuerst {@link #syncScene()} auf, um in die Szene synchron zu halten.
	 * Gibt es keine Szene, terminiert die Methode.
	 * Ansonsten finden folgende Aktionen statt:
	 * <ul>
	 * <li>Aufruf von Input.update()</li>
	 * <li>Aufruf von Scene.update()</li>
	 * <li>Synchronisation der Frame-Szene mit der Game-Szene.</li>
	 * <li>Aufruf von Graphics.update()</li>
	 * </ul>
	 * <p>
	 * <b>Achtung!</b> Die Methode darf nicht aus einem Frame aufgerufen werden.
	 * </p>
	 * @throws IllegalStateException wenn die Methoe innerhalb des Frames aufgerufen wird.
	 * @see #run()
	 */
	public static void nextFrame()
	{
		syncScene();
		if (scene != null) executeFrame();
	}
	
	/**
	 * Synchronisiert die Szene des Frames mit der Spielszene.
	 * Wenn sich die Szene geändert hat, wird für die alte Szene {@link Scene#dispose()} aufgerufen
	 * und für die Neue {@link Scene#init()}.
	 * Wenn sich die Szene nicht geändert hat, macht die Methode nichts.
	 * <p>
	 * <b>Achtung!</b> Die Methode darf nicht aus einem Frame aufgerufen werden.
	 * </p>
	 * @throws IllegalStateException wenn die Methode innerhalb des Frames aufgerufen wird.
	 */
	public static void syncScene()
	{
		if (inFrame) throw new IllegalStateException("Frame is running.");
		
		Scene nScene = Game.getScene();
		if (nScene != scene)
		{
			if (scene != null) scene.dispose();
			if (nScene != null) nScene.init();
			scene = nScene;
		}
	}
	
	/**
	 * Beendet die Haupt-Schleife des Frame-Timers.
	 */
	public static void stop()
	{
		stop = true;
	}
	
	protected static boolean isRunning()
	{
		return run;
	}

	/**
	 * Gibt die verstrichene Zeit seit Spielstart in Millisekunden zurück.
	 * @return Die verstrichene Zeit in Millisekunden.
	 */
	public static long getTime()
	{
		return System.currentTimeMillis() - Game.startTime;
	}
	
	/**
	 * Gibt die angestrebten FPS zurück.
	 * @return Ziel-FPS
	 */
	public static int getTargetFPS()
	{
		return (int)(1000000L / frameTime);
	}
	
	public static void setFPS(int fps)
	{
		frameTime = 1000000L / fps;
	}
	
	/**
	 * Gibt die FPS errechnet aus den letzten 5 Frame-Zyklen.
	 * @return FPS
	 */
	public static int FPS()
	{
		long times = 0;
		for(int i = 0; i < frameTimes.length; i++)
		{
			times += frameTimes[i];
		}
		if (times == 0) return 0;
		return (int)(frameTimes.length * 1000000000L / times);
	}
	
	public static long getFrameCount()
	{
		return frameCount;
	}
}
