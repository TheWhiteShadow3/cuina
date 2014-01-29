/*
 * Cuina Engine
 * Copyright (C) 2011 - 2013 by Cuina Team (http://www.cuina.byethost12.com/)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY
 * KIND, either express or implied.
 */

package cuina;

import cuina.audio.AudioSystem;
import cuina.database.Database;
import cuina.debug.Debugger;
import cuina.graphics.GraphicManager;
import cuina.graphics.Graphics;
import cuina.graphics.TextureLoader;
import cuina.plugin.PluginManager;
import cuina.script.ScriptExecuter;
import cuina.util.Ini;
import cuina.util.InvalidFileFormatException;
import cuina.util.LoadingException;
import cuina.util.ResourceManager;
import cuina.world.CuinaWorld;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static cuina.Context.GLOBAL;
import static cuina.Context.SESSION;
import static cuina.Context.SCENE;

/**
 * Stellt das eigentliche Spiel sowie eine Spielinstanz da.
 * Verwaltet globale Spiel-Ereignisse und beinhaltet die Spiel-Session.
 * <p>
 * Die Session stellt einen Spielstand da, der zu beliebiger Zeit gespeichert und wiederhergestellt werden kann.
 * </p>
 * 
 * @author TheWhiteShadow
 */
public final class Game
{	
	private static final int MODULE_PLUGINS 	= 1;
	private static final int MODULE_DATABASE 	= 2;
	private static final int MODULE_SCRIPT 		= 4;
	private static final int MODULE_GRAPHIC 	= 8;
	private static final int MODULE_AUDIO 		= 16;
	private static final int MODULE_LOOP 		= 32;
	private static final int MODULE_ALL			= 255;
	
	/** Name der Ini-Datei. */
	public static final String CONFIG_FILE 				= "cuina.cfg";
	
	/** Eigenschaftsname für den Projekt-Pfad. */
	public static final String CUINA_GAMEPATH_KEY 		= "cuina.game.path";
	
	/** Eigenschaftsname für die Spiel-Session. */
	public static final String CUINA_SESSIONPATH_KEY 	= "cuina.session.path";
	
	/** Eigenschaftsname für den Pfad zur Grafik-Bibliothek. */
	public static final String LWJGL_LIBRARYPATH_KEY 	= "org.lwjgl.librarypath";


	/** Eigenschaftsname für das Haupt-Skript. */
	public static final String MAIN_SCRIPT_KEY 			= "Main-Script";
	
	/** Eigenschaftsname für eine Session beim Start der Engine. */
	public static final String CUINA_SESSION_KEY 		= "Create-Session";
	
	/** Eigenschaftsname für eine Szene beim Start der Engine. */
	public static final String CUINA_SCENE_KEY 			= "Start-Scene";
	
	/** Eigenschaftsname für die zu ladenden Module. */
	public static final String CUINA_MODULE_KEY			= "Modules";

	private static Game instance;
	private static boolean debug = false;
	
	private static String gameTitle;
	private static int modules;
	
	private static String rootDirectory;
	static long startTime;
	
	private Ini ini;
	private GameSession session;
	private Context globalContext = new Context(GLOBAL);
	private Context sessionContext;
	private Context sceneContext;
	
	private final ArrayList<GameListener> listeners = new ArrayList<GameListener>();
	
	/**
	 * Erzeugt die Spielinstanz.
	 * Wirft eine Ausnahme, wenn bereits eine Spiel-Instanz existiert.
	 * @throws IllegalStateException wenn bereits eine Instanz der Klasse existiert.
	 * @see #getInstance()
	 */
	public Game()
	{
		if (instance != null) throw new IllegalStateException("Game allready exists.");
		instance = this;
	}
	
	/**
	 * Gibt die Spielinstanz zurück.
	 * @return Die Instanz vom Spiel.
	 */
	protected static Game getInstance()
	{
		return instance;
	}
	
	/**
	 * Gibt die Session zurück. Wenn keine Session excistiert wird <code>null</code> zurückgegeben.
	 * @return die Spielsession.
	 */
	public static GameSession getSession()
	{
		return getInstance().session;
	}
	
	/**
	 * @return true, wenn das Spiel im Debug-Modus läuft, andernfalls false.
	 */
	public static boolean isDebug()
	{
		return debug;
	}
	
	/**
	 * Gibt den Title des Spiels zurück.
	 * @return Title des Spiels.
	 */
	public static String getTitle()
	{
		return gameTitle;
	}
	
	/**
	 * Gibt den Kontext zum angegebenen Typ zurück.
	 * Wenn der Kontext nicht existiert wird ein Ausnahme geworfen.
	 * @param contextType Kontext-Type.
	 * @return Der Kontext.
	 * @throws IllegalStateException wenn der Kontext nicht gesetzt ist.
	 */
	public static Context getContext(int contextType)
	{
		Context context;
		switch(contextType)
		{
			case GLOBAL: context = getInstance().globalContext; break;
			case SESSION: context =  getInstance().sessionContext; break;
			case SCENE: context =  getInstance().sceneContext; break;
			default: context = null;
		}
		if (context == null) throw new IllegalStateException("Context " + contextType + " is not set.");
		return context;
	}
	
	/**
	 * Prüft, ob der angegebene Kontext-Txp existiert.
	 * @param contextType Kontext-Type.
	 * @return <code>true</code>, wenn der Kontext existiert, andernfalls <code>false</code>.
	 */
	public static boolean contextExists(int contextType)
	{
		switch(contextType)
		{
			case GLOBAL: return getInstance().globalContext != null;
			case SESSION: return getInstance().sessionContext != null;
			case SCENE:return getInstance().sceneContext != null;
		}
		return false;
	}
	
	private void setContext(int contextType, Context context)
	{
		Context oldContext = null;
		switch(contextType)
		{
			case GLOBAL:
				oldContext = globalContext;
				globalContext = context;
				break;
				
			case SESSION:
				oldContext = sessionContext;
				sessionContext = context;
				break;
				
			case SCENE:
				oldContext = sceneContext;
				sceneContext = context;
				break;
			default: throw new IllegalArgumentException("Invalid context");
		}
		if (oldContext != null) oldContext.dispose();
	}
	
	/**
	 * Gibt die aktuelle Szene zurück. Wenn keine Szene excistiert wird <code>null</code> zurückgegeben.
	 * @return Aktuelle Szene.
	 */
	public static String getCurrentScene()
	{
		return getSession().sceneName;
	}

	/**
	 * Gibt den absoluten Pfad zum Projekt zurück.
	 * @return Den absoluten Pfad zum Projekt.
	 */
	public static String getRootPath()
	{
		return rootDirectory;
	}
	
	/**
	 * Gibt die Konfigurations-Datei vom Projekt zurück.
	 * @return Die Konfigurations-Datei vom Projekt.
	 */
	public static Ini getIni()
	{
		return getInstance().ini;
	}
	
	/**
	 * Fügt einen Listener hinzu, der Spielevents empfängt.
	 * @param l Der Listener.
	 */
	public static void addGameListener(GameListener l)
	{
		if (instance.listeners.contains(l)) return;
		instance.listeners.add(l);
	}

	/**
	 * Entfernt den angegebenen Listener aus der Liste.
	 * @param l Der Listener.
	 */
	public static void removeGameListener(GameListener l)
	{
		instance.listeners.remove(l);
	}
	
	void fireEvent(int type)
	{
		GameEvent ev = new GameEvent(type, getScene(), session);
		for(int i = 0; i < listeners.size(); i++)
		{
			listeners.get(i).gameStateChanged(ev);
		}
	}

	/**
	 * Initialisiert die Engine.<br>
	 * Liest die Ini-Datei aus, ladet plugins und startet die Grafik- und Skript-Engine.
	 */
	public void init() throws LoadingException
	{
		if (FrameTimer.isRunning()) return;
		
//		setClassLoader();
		loadConfig();
		final Thread setupThread = new Thread()
		{
			@Override
			public void run()
			{
				if (isModuleActive(MODULE_PLUGINS)) loadPlugins();
				if (isModuleActive(MODULE_DATABASE)) loadDatabase();
				InjectionManager.loadContextObjects(Context.GLOBAL);
				initStartingState();
			}
		};
		Thread scriptThread = null;
		if (isModuleActive(MODULE_SCRIPT))
		{
			scriptThread = new Thread()
			{
				@Override
				public void run()
				{
					ScriptExecuter.init();
					try
					{
						setupThread.join();
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					ScriptExecuter.loadScripts();
				}
			};
		}
//		System.out.println("[Game] Zeit bis zur Parallelisierung: " + FrameTimer.getTime());
		setupThread.start();
		if (scriptThread != null) scriptThread.start();
		if (isModuleActive(MODULE_GRAPHIC)) Graphics.init(gameTitle);
//		System.out.println("[Game] Zeit bis Graphics.start fertig: " + FrameTimer.getTime());
		try
		{
			setupThread.join();
			if (scriptThread != null) scriptThread.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
//		System.out.println("[Game] Zeit bis zum Start: " + FrameTimer.getTime());
		start();
		close();
		dispose();
	}
	
	/**
	 * Ladet die Plugins.
	 * Wenn die Property "cuina.plugin.path" angegeben wurde, wird diese standardmäßig benutzt.
	 * Nur wenn diese <code>null</code> ist oder ein leerer String, wird das Verzeichnis <i>plugins</i> durchsucht.
	 */
	public void loadPlugins()
	{
		String name = getProperty(PluginManager.CUINA_PLUGINPATH_KEY, "plugins");
		Path path = searchPath(name);
		if (path == null)
		{
			Logger.log(Game.class, Logger.ERROR, "Plugin-Path can not resolve.");
			return;
		}
		PluginManager.findPlugins(path.toFile());
		InjectionManager.loadPluginClasses();
	}
	
	public void loadDatabase()
	{
		String name = getProperty(Database.CUINA_DATABASEPATH_KEY, "data");
		Path path = searchPath(name);
		if (path == null)
		{
			Logger.log(Game.class, Logger.ERROR, "Database-Path can not resolve.");
			return;
		}
		Database.loadDatabases(path.toFile());
	}
	
	public void initStartingState()
	{
		String session = getProperty(CUINA_SESSIONPATH_KEY, null);
		if (session != null)
		{
			File file = new File(session);
			if (file.exists())
			{
				loadGame(file);
				return;
			}
		}
		else
		{
			if ( "true".equals(getProperty(CUINA_SESSION_KEY, null)) ) newGame();
			newScene(getProperty(CUINA_SCENE_KEY, null));
		}
	}
	
	public void start()
	{
		if (debug) initDebugger();
		
		try
		{
			if (isModuleActive(MODULE_AUDIO)) AudioSystem.start();
//			Graphics.start();
			
			callMainScript("start");
			
			
			if (isModuleActive(MODULE_LOOP)) FrameTimer.run();
		}
		catch (Throwable e)
		{
			Logger.log(Game.class, Logger.CRIT_ERROR, e);
		}
	}
	
	/**
	 * Startet eine neues Spiel-Session.
	 */
	public static GameSession newGame()
	{
		getInstance().newSession();
		return getSession();
	}
	
	public void newSession()
	{
		Logger.log(Game.class, Logger.DEBUG, "create session");
		setContext(Context.SESSION, new Context(Context.SESSION));
		this.session = new GameSession(sessionContext);
		InjectionManager.loadContextObjects(Context.SESSION);
		fireEvent(GameEvent.OPEN_SESSION);
		
		callMainScript("newGame");
	}

	public static GameSession loadGame(File file)
	{
		getInstance().loadSession(file);
		return getSession();
	}
	
	public void loadSession(File file)
	{
		Logger.log(Game.class, Logger.DEBUG, "load session " + file);
		session = (GameSession) Database.loadData(file);
		setContext(Context.SESSION, session.context);
		Graphics.GraphicManager.merge( (GraphicManager) session.pop());
		System.gc();
		// führe die heilige Refesh-Methode aus, die alle Grafiken neu erschafft. (zumindest in der Threorie)
		Graphics.GraphicManager.refresh();

//		InjectionManager.persistentObjects = (HashMap<String, Object>) session.pop();
		
		newScene(session.sceneName);
		fireEvent(GameEvent.SESSION_LOADED);
		callMainScript("loadGame");
	}
	
	public static boolean saveGame(File file)
	{
		getInstance().saveSession(file);
		return true;
	}
	
	public void saveSession(File file)
	{
		if (session == null) throw new NullPointerException("Session is null");
		
		Logger.log(Game.class, Logger.DEBUG, "save session " + file);
		session.sceneName = getScene().getName();
//		session.push(InjectionManager.persistentObjects);
		// referenziere den Graphicmanager für die Wiederherstellung
		session.push(Graphics.GraphicManager);
		Database.saveData(file, session);
//		session.clearStack();
		fireEvent(GameEvent.SESSION_SAVED);
		callMainScript("saveGame");
	}
	
	/**
	 * Beendet die Spiel-Session.
	 * Mehrfache Aufrufe sind möglich.
	 */
	public static void endGame()
	{
		Logger.log(Game.class, Logger.DEBUG, "close session");
		getInstance().fireEvent(GameEvent.CLOSING_SESSION);
		getInstance().callMainScript("endGame");
		getInstance().setContext(Context.SESSION, null);
		getInstance().session = null;
	}
	
	/**
	 * Beendet die Engine.
	 * Diese Methode ruft die close-Methode des Main-Skripts auf und
	 * delegiert dann den Aufruf an FrameTimer.stop() weiter.
	 */
	public static void close()
	{
		if (getSession() != null) endGame();
		getInstance().fireEvent(GameEvent.END_GAME);
		if (!(Boolean) getInstance().callMainScript("close")) return;
		
		FrameTimer.stop();
	}
	
	public void dispose()
	{
		FrameTimer.stop();
		ScriptExecuter.shutdown();
		AudioSystem.dispose();
		Graphics.dispose();
		TextureLoader.clear();
		PluginManager.clear();
		instance = null;
		
		Logger.log(Game.class, Logger.INFO, "Engine is terminated.");
	}
	
	private Object callMainScript(String method)
	{
		if (!isModuleActive(MODULE_SCRIPT)) return null;
		
		String script = getProperty(MAIN_SCRIPT_KEY, null);
		if (script == null) return null;

		Object[] args;
		if ("start".equals(method) || "close".equals(method))
			args = new Object[0];
		else
			args = new Object[] {instance.session};
		return ScriptExecuter.executeDirect(script, method, args);
	}
	
	/**
	 * Gibt die aktuelle Spiel-Szene zurück.
	 * <p>
	 * Gibt das Selbe wie wie <pre>Game.Global.get(Scene.INSTANCE_KEY)</pre> zurück.
	 * </p>
	 * @return aktuelle Spiel-Szene.
	 */
	public static Scene getScene()
	{
		return getContext(GLOBAL).<Scene>get(Scene.INSTANCE_KEY);
	}
	
	/**
	 * Gibt die aktuelle Spielwelt zurück.
	 * <p>
	 * Gibt das Selbe wie <pre>Game.getContext(SESSION).get(CuinaWorld.INSTANCE_KEY)</pre> zurück.
	 * </p>
	 * @return Die Spielwelt
	 */
	public static CuinaWorld getWorld()
	{
		return getContext(SESSION).<CuinaWorld>get(CuinaWorld.INSTANCE_KEY);
	}
	
	/**
	 * Setzt die Spielwelt.
	 * @param world
	 */
	public static void setWorld(CuinaWorld world)
	{
		getContext(SESSION).set(CuinaWorld.INSTANCE_KEY, world);
	}
	
	/**
	 * Setzt die aktuelle Spielszene.<br>
	 * Die folgenden Aktion werden dabei ausgeführt:
	 * <ul>
	 * <li>Die globale Kontext-Referenz <code>Scene</code> zeigt auf die neue Instanz.</li>
	 * <li>Der Szenen-Kontext wird neu angelegt.</li>
	 * <li>Falls eine Session existiert, wird der Name der Szene dort abgespeichert
	 * um beim Laden wiederhergestellt zu werden.</li>
	 * </ul>
	 * @param sceneName Name der neuen Szene.
	 * @return Die neue Szene.
	 */
	public static Scene newScene(String sceneName)
	{
		Scene scene = null;
		try
		{
			if (sceneName != null)
			{
				scene = new Scene(sceneName);
				getInstance().setContext(Context.SCENE, new Context(SCENE));
			}
			else
			{
				getInstance().setContext(Context.SCENE, null);
			}
			
			getContext(GLOBAL).set("Scene", scene);
			if (getSession() != null)
				getSession().sceneName = sceneName;
		}
		catch (Exception e)
		{
			Logger.log(Game.class, Logger.ERROR, e);
		}
		return scene;
	}

	public static boolean getSwitch(int index)
	{
		return getInstance().session.switches[index];
	}

	public static void setSwitch(int index, boolean value)
	{
		getInstance().session.switches[index] = value;
	}

	public static long getVar(int index)
	{
		return getInstance().session.vars[index];
	}

	public static void setVar(int index, long value)
	{
		getInstance().session.vars[index] = value;
	}
	
	private static void initDebugger()
	{
		InjectionManager.addObject(new Debugger(instance), "Debugger", Context.GLOBAL, Scene.ALL_SCENES);
	}
	
	private static String getLwjglPath(String os)
	{
		return searchPath("lib").resolve("lwjgl/native/" + os).toString();
	}
	
	public void loadConfig() throws LoadingException
	{
		// Pfade und Properties setzen
		rootDirectory = System.getProperty(CUINA_GAMEPATH_KEY);
		if (rootDirectory == null)
			rootDirectory = System.getProperty("user.dir");
		Logger.log(Game.class, Logger.INFO, "Project-Directory is: " + rootDirectory);
		
		ini = getIni(rootDirectory);
		
		gameTitle 	= ini.get("Game", "Title", "");
		modules		= getProperty(CUINA_MODULE_KEY, MODULE_ALL);

		String path;
		path = ResourceManager.getResourcePath(ResourceManager.KEY_GRAPHICS);
		checkPath(Paths.get(rootDirectory, path), ResourceManager.KEY_GRAPHICS);
		
		path = ResourceManager.getResourcePath(ResourceManager.KEY_AUDIO);
		checkPath(Paths.get(rootDirectory, path), ResourceManager.KEY_AUDIO);
		
//		System.out.println();
//		System.out.println("  running under Java " + System.getProperty("java.version") + " at " + System.getProperty("os.name")  + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch") + "\n");
		
		//workaround for JRE 7 x64 at Linux
		if(System.getProperty("java.version").contains("1.7") && System.getProperty("os.name").toLowerCase().contains("linux"))
		{
			String osArch = System.getProperty("os.arch");
			boolean is64bit = "amd64".equals(osArch) || "x86_64".equals(osArch);

			java.awt.Toolkit.getDefaultToolkit(); // loads libmawt.so (needed by jawt)
					
			if (is64bit) 
				System.load(System.getProperty("java.home") + "/lib/amd64/libjawt.so");
			else 
				System.load(System.getProperty("java.home") + "/lib/i386/libjawt.so");
		}
		
		String lwjglPath = System.getProperty(LWJGL_LIBRARYPATH_KEY);
		if (lwjglPath == null)
		{
			String os = System.getProperty("os.name").toLowerCase();
			String nativePath = null;
			
			if(os.contains("linux") || os.contains("bsd") || os.contains("nix")) nativePath = "linux";
			if(os.contains("windows")) nativePath = "windows";
			if(os.contains("mac os x")) nativePath = "macosx";
			if(os.contains("sunos")) nativePath = "solaris";
			if(nativePath != null)
			{
				lwjglPath = getLwjglPath(nativePath);
				System.setProperty(LWJGL_LIBRARYPATH_KEY, lwjglPath);
			}
		}
		checkPath(Paths.get(lwjglPath), "lwjgl-Library");
	}
	
	private static Ini getIni(String pathname) throws LoadingException
	{
		Ini ini;
		File iniFile = new File(pathname, CONFIG_FILE);
		checkPath(iniFile.toPath(), "Config-File");
		try
		{
			Logger.log(Game.class, Logger.DEBUG, "lese Ini-Datei: " + iniFile.getAbsolutePath());
			
			ini = new Ini(iniFile);
		}
		catch (IOException | InvalidFileFormatException e)
		{
			throw new LoadingException(iniFile, e);
		}

		return ini;
	}
	
	public static int getProperty(String key, int def)
	{
		try
		{ return Integer.parseInt(getProperty(key, null)); }
		catch (NumberFormatException e)
		{ return def; }
	}
	
	public static String getProperty(String key, String def)
	{
		String value = System.getProperty(key);
		if (value == null)
		{
			value = getIni().get("Game", key, def);
		}
		return value;
	}
	
	private boolean isModuleActive(int module)
	{
		return (modules & module) != 0;
	}
	
	private static Path searchPath(String pathName)
	{
		if (pathName == null) return null;
		
		Path path = Paths.get(pathName);
		if (path.isAbsolute()) return path;
		// Suche Pfad im Projekt
		path = Paths.get(Game.getRootPath(), pathName).toAbsolutePath();
		if (Files.exists(path)) return path;
		// Suche Pfad neben der Engine
		try
		{
			URL url = Game.class.getProtectionDomain().getCodeSource().getLocation();
			path = Paths.get(url.toURI()).resolve("../" + pathName).normalize();
			if (Files.exists(path)) return path;
		}
		catch (URISyntaxException e)
		{
			Logger.log(Game.class, Logger.WARNING, "could not resolve engine path.");
		}
		// Suche Pfad in CUINA_HOME
		String cuinaHome = System.getenv("CUINA_HOME");
		if (cuinaHome == null) return null;
		path = Paths.get(cuinaHome, pathName);
		if (Files.exists(path)) return path;
		
		return null;
	}
	
	private static void checkPath(Path path, String resourceName) throws LoadingException
	{
		if (!Files.exists(path))
			throw new LoadingException(resourceName);
	}

	/**
	 * Startet das Spiel.<br>
	 * Mögliche Parameter:
	 * <ul>
	 * 	<li>debug - Führt das Spiel im Debug-Modus aus.</li>
	 *	<li>cuina.game.path= - Setzt den Pfad für das Projekt.</li>
	 *	<li>cuina.session.path= - Setzt den Pfad für eine Spiel-Session die geladen werden soll.</li>
	 *	<li>cuina.plugin.path= - Setzt den Pfad für die Plugins.</li>
	 *	<li>cuina.plugin.list= - Gibt eine LIste der zu ladenden Plugins an.</li>
	 *  <li>logfile= - Ändert den Namen der Logdatei. Wird null angegeben wird die Konsole benutzt.</li>
	 *  <li>loglevel= - Ändert das Log-Level.</li>
	 *  
	 * </ul>
	 * @param args Parameterliste.
	 */
	public static void main(String[] args) throws InterruptedException
	{
		startTime = System.currentTimeMillis();
		
		System.out.println("CuinaEngine (c) 2011-2013 by Cuina Team\n");
		System.out.println("This is free software; you can redistribute and/or modify it");
		System.out.println("under the terms of GNU GENERAL PUBLIC LICENSE, either version 3");
		System.out.println("of the License, or (at your option) any later version.");
		System.out.println("This program comes with ABSOLUTELY NO WARRANTY; see license.txt for details.\n");
		
		File logFile = new File("cuina.log");
		for(String str : args)
		{
			if ("debug".equals(str))
			{
				debug = true;
				Logger.logLevel = Logger.DEBUG;
			}
			else if (str.startsWith("logfile="))
			{
				String filename = str.substring(8);
				logFile = filename.equals("null") ? null : new File(filename);
			}
			else if (str.startsWith("loglevel="))
			{
				Logger.logLevel = Integer.parseInt(str.substring(9));
			}
		}
		
		try
		{
			Logger.setLogFile(logFile);
		}
		catch (FileNotFoundException e)
		{
			Logger.log(Game.class, Logger.ERROR, e);
			return;
		}
		
		try
		{
			new Game().init();
		}
		catch (LoadingException e)
		{
			e.printStackTrace();
		}
	}
}
