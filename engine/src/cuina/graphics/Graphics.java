/*
 * Cuina Engine
 * Copyright (C) 2011 - 2012 by Cuina Team (http://www.cuina.byethost12.com/)
 *
 * see license.txt for more info
 */

package cuina.graphics;

import static org.lwjgl.opengl.GL11.*;

import cuina.Game;
import cuina.Input;
import cuina.Logger;
import cuina.util.LoadingException;

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.Util;
import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;

/**
 * Haupt-Schnittstelle für die OpenGL-API.
 * <p>
 * Das Cuina Graphic-Framework kapselt OpenGL-Befehle in eine praktikable OO-Struktur.
 * </p>
 * @author TheWhiteShadow
 */
public final class Graphics
{
	private static Graphics instance;
	/**
	 * Der hier referenzierte GraphicManager dient als Standard-Referenz für Grafiken.
	 * <p>
	 * Jede Grafik, die nicht explizit einem Kontainer zugewiesen wird, wird diesem hier hinzugefügt.
	 * Außerdem werden alle Elemente in jedem View gezeichnet, dem keine Grafik zugewiesen wurde.
	 * </p>
	 */
	public static final GraphicManager GraphicManager = new GraphicManager();
	public static final List<View> VIEWS = new ArrayList<View>();
	private static GraphicManager storedGM;
	
	protected final static LinkedList<RenderJob> renderJobs = new LinkedList<RenderJob>();
	
	private Font defaultFont = new Font("Times New Roman, Serif", 0, 20);
	private ReadableColor defaultColor = new Color(255, 255, 255);
	
	private int width;
	private int height;
	private float aspectRatio;
	private boolean freeze = false;
	private boolean init = false;
	
	transient private static int SecondFBO;
	transient private static int currentFBO;
	transient private static Shader renderShader;
	transient private static Shader currentShader;
	transient private static Thread graphicThread;
	transient private static View currentView;
		
	private Graphics() {}
	
	public static void init(String title)
	{
		if (Display.isCreated()) return;
		
		setupDisplay(title);
		getInstance().createDisplay();
	}
	
	private static void setupDisplay(String title)
	{
		int width  = Game.getProperty("Screen-Width", 640);
		int height = Game.getProperty("Screen-Height", 480);
		
		try
		{
			DisplayMode[] modes = Display.getAvailableDisplayModes();
			LinkedList<DisplayMode> possibleModes = new LinkedList<DisplayMode>();
			DisplayMode refereceMode = Display.getDesktopDisplayMode();
//			System.out.println(refereceMode);
			DisplayMode usedMode = null;
			for (DisplayMode mode : modes)
			{
//				System.out.println(mode);
				if (mode.getWidth() == width && mode.getHeight() == height 
						&& mode.getBitsPerPixel() == refereceMode.getBitsPerPixel())
				{
					if (mode.getFrequency() == refereceMode.getFrequency())
					{
						usedMode = mode;
						break;
					} else
						possibleModes.add(mode);
				}
			}
			
			// some modes have wrong frequency compared to refereceMode, but the same width, height and bitsPerPixel,
			// -> select a mode next to refereceMode's frequency, over 50 Hz
			// caused by a possible graphic card driver bug, especially on Linux
			if (!possibleModes.isEmpty() && usedMode == null)
			{
				Logger.log(Graphics.class, Logger.WARNING,
						"Your graphics card driver reports " + possibleModes.size() + " display mode(s) with " + width + "x" + height + "x" + refereceMode.getBitsPerPixel() + ",\n" +
						"but without the same frequency than your desktop display mode!\nTry to select a display mode next to your desktop display mode's frequency");
				DisplayMode nextToReference = null;
				for(DisplayMode mode : possibleModes)
				{
					if(mode.getFrequency() >= 50)
					{
						if(nextToReference == null)
							nextToReference = mode;
						else
							if(Math.abs(mode.getFrequency() - refereceMode.getFrequency()) <= Math.abs(nextToReference.getFrequency() - refereceMode.getFrequency()))
							{
								nextToReference = mode;
							}
					}
				}
				usedMode = nextToReference;
			}
			
			if (usedMode != null)
			{
				setupDisplay(title, usedMode);
			}
			else
				Logger.log(Graphics.class, Logger.CRIT_ERROR, "No display mode selected!");
		}
		catch (LWJGLException e)
		{
			Logger.log(Graphics.class, Logger.CRIT_ERROR, e);
		}
		
	}
	
	public static void setupDisplay(String title, DisplayMode mode)
	{
		graphicThread = Thread.currentThread();
		Logger.log(Graphics.class, Logger.INFO, "set Graphicmode: " + mode);
		try
		{
			Display.setTitle(title);
			Display.setDisplayMode(mode);
			getInstance().setSize(mode.getWidth(), mode.getHeight());
			try
			{
				Display.setIcon(TextureLoader.loadIcons("CE_Icon16.png", "CE_Icon32.png"));
			}
			catch (LoadingException e)
			{
				e.printStackTrace();
			}
		}
		catch (LWJGLException e)
		{
			Logger.log(Graphics.class, Logger.CRIT_ERROR, e);
		}
	}
	
	private void setSize(int width, int height)
	{
		this.width = width;
		this.height = height;
		this.aspectRatio = (float)width / (float)height;
	}
	
	public static int getWidth()
	{
		return getInstance().width;
	}

	public static int getHeight()
	{
		return getInstance().height;
	}

	public static float getAspectRatio()
	{
		return getInstance().aspectRatio;
	}

	public static Font getDefaultFont()
	{
		return getInstance().defaultFont;
	}
	
	public static ReadableColor getDefaultColor()
	{
		return getInstance().defaultColor;
	}
	
	public static void setDefaultFont(Font defaultFont)
	{
		getInstance().defaultFont = defaultFont;
	}

	public static void setDefaultColor(Color defaultColor)
	{
		getInstance().defaultColor = defaultColor;
	}
	
	public static Thread getGraphicThread()
	{
		return graphicThread;
	}

	public void createDisplay()
	{
		if (Display.isCreated()) return;
		init = true;
		try
		{
			Display.create();
			Keyboard.create();
			Mouse.create();
		}
		catch (LWJGLException e)
		{
			Logger.log(Graphics.class, Logger.CRIT_ERROR, e);
		}
		// Hintergrundfarbe: Schwarz
		glClearColor(0f, 0f, 0f, 1f);
		// Setze Flags
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glEnable(GL_LIGHTING);
		
		glEnable(GL_CULL_FACE);
		
		// Setze Funktionen
		glAlphaFunc(GL_GREATER, 0f);
		glDepthFunc(GL_LEQUAL);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		// GL14.glBlendEquation(GL14.GL_FUNC_ADD);

		// glLightModeli(GL12.GL_LIGHT_MODEL_COLOR_CONTROL,
		// GL12.GL_SEPARATE_SPECULAR_COLOR );
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		// Wir brauchen eine initiale Matrix.
		GLCache.setMatrix(GL_PROJECTION);
//		glViewport(0, 0, width, height);

//		System.out.println("Multitexturen: " + GL11.glGetInteger(ARBMultitexture.GL_MAX_TEXTURE_UNITS_ARB));

		//XXX: Definition der Kamera. In GraphicsUtil steht auch noch ein Part zur Kamera.
//		camera = new Camera();
//		camera.fromZ = 15;
//		camera.fromY = 20;
		
		VIEWS.add(new View(width, height));

		Util.checkGLError();

		Logger.log(Graphics.class, Logger.INFO, "Setup complete.");
	}

	public static void toggleFullscreen()
	{
		try
		{
			Display.setFullscreen( !Display.isFullscreen() );
		}
		catch (LWJGLException e)
		{
			Logger.log(Graphics.class, Logger.ERROR, e);
		}
	}
	
	public static boolean isInitialized()
	{
		return instance != null && instance.init;
	}
	
	public static boolean isCloseRequested()
	{
		return isInitialized() && Display.isCloseRequested();
	}
	
	public static void dispose()
	{
		if (GraphicManager != null)
		{
			GraphicManager.dispose();
//			GraphicManager = null;
		}
		if (storedGM != null)
		{
			storedGM.dispose();
			storedGM = null;
		}
		Display.destroy();
		renderJobs.clear();
		currentFBO = 0;
		SecondFBO = 0;
		currentShader = null;
		renderShader = null;
	}

//	private static void reSizeGLScene(int width, int height)
//	{
//	     if (height == 0) height = 1;
//
//	     GL11.glViewport(0,0,width,height);
//	     GL11.glMatrixMode(GL11.GL_PROJECTION);
//	     GL11.glLoadIdentity();
//	     GLU.gluPerspective(45.0f,(float)width/(float)height,0.1f,100.0f);
//	     GL11.glMatrixMode(GL11.GL_MODELVIEW);
//	     GL11.glLoadIdentity();
//	}
	
	public static void setShader(Shader shader)
	{
		currentShader = shader;
		bindShader();
	}

	public static Shader getShader()
	{
		return currentShader;
	}

	protected static void bindShader()
	{
		if (renderShader == currentShader) return;
		
		renderShader = currentShader;
		if (renderShader == null)
			Shader.unbind();
		else
			renderShader.bind();
	}
	
	public static int getCurrentFBO()
	{
		return currentFBO;
	}

	public static void bindFBO(Texture tex)
	{
		if (SecondFBO == 0)
		{
			EXTFramebufferObject.glGenFramebuffersEXT(GraphicUtil.TEMP_INT_BUFFER);
			SecondFBO = GraphicUtil.TEMP_INT_BUFFER.get(0);
		}
		if (currentFBO != SecondFBO)
		{
			currentFBO = SecondFBO;
			EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, currentFBO);
		}
		
        EXTFramebufferObject.glFramebufferTexture2DEXT(
        		EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
        		EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT,
                GL11.GL_TEXTURE_2D, tex.textureID, 0);
	}
	
	public static void unbindFBO()
	{
		if (currentFBO == 0) return;
		
		currentFBO = 0;
        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, currentFBO);
	}
	
	public static boolean isFreeze()
	{
		return getInstance().freeze;
	}

	public static void setFreeze(boolean freeze)
	{
		getInstance().freeze = freeze;
	}
	
	public static void update()
	{
		if (Graphics.isFreeze() || !isInitialized()) return;
		if (Input.isPressed(Keyboard.KEY_F5))
		{
			Graphics.toggleFullscreen();
		}

//		glMatrixMode(GL11.GL_MODELVIEW);
//		glLoadIdentity();
		
//		MapObject player = GameMap.getInstance().getScrollTarget();
//		camera.fromX = player.getLogicX() - GameMap.getInstance().getScrollX();
//		camera.fromY = (player.getLogicY() - GameMap.getInstance().getScrollY()) -10;
		
//		camera.toX   = camera.fromX;
//		camera.toY   = player.getLogicY() - GameMap.getInstance().getScrollY();
		
//		camera.fromY = 0;//-GameMap.getInstance().getScrollY();
//		camera.fromZ = 10;//200;
		
//		GLU.gluLookAt(0, 150, 150, 0,0,0, 0, 0, 1);
//	    D3D.set3DView(false);
	    GLCache.restore();
		while(renderJobs.size() > 0)
		{
			renderJobs.poll().render();
		}
		
		// setze das Rendertarget zurück auf das Fenster
		unbindFBO();
	    
//	    cube.pan += 0.5;
//        GL11.glMatrixMode(GL11.GL_PROJECTION);
//        GL11.glLoadIdentity();
//        // set ortho to same size as viewport, positioned at 0,0
//        GL11.glOrtho(
//        		0, getInstance().width,  // left,right
//        		getInstance().height, 0,  // bottom,top
//        		-500,500);    // Zfar, Znear
//        // return to modelview matrix
//        GL11.glMatrixMode(GL11.GL_MODELVIEW);
//        GL11.glLoadIdentity();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		for (View view : VIEWS)
		{
			currentView = view;
			view.draw();
		}
		currentView = null;
	    
//		GraphicManager.draw();
//		D3D.set3DView(true);
//		

//		GLU.gluLookAt(0, 5, 15, 0,0,0, 0, 1, 0);
//        GL11.glPushMatrix();
//        {
//        	GL11.glRotatef(rotation, 0, 1, 0);
//        	GL11.glColor4f(0f, .5f, 1f, 1f);
//        	D3D.drawCube(4, D3D.TEST_TEXTUR);
//        }
//        GL11.glPopMatrix();
		
		Display.update();
	}
	
	/**
	 * Gibt den aktuellen View zurück, der gezeichnet wird.
	 * <p>
	 * Die Methode gibt nur innerhalb der Zeichenroutine im aktuellen Thread ein View-Objekt zurück.
	 * Außerhalb davon ist der Rückgabewert immer <code>null</code>.
	 * </p>
	 * @return Der aktuelle View.
	 */
	public static View getCurrentView()
	{
		if (graphicThread != Thread.currentThread()) return null;
		return currentView;
	}
	
	public static Graphics getInstance()
	{
		if (instance == null)
			instance = new Graphics();
		return instance;
	}
	
	// ein paar Util-methoden um Arrays und Listen zu disposen
	
	public static void disposeGraphics(Graphic[] array)
	{
		for(int i = 0; i < array.length; i++)
		{
			if (array[i] != null) array[i].dispose();
		}
	}
	
	public static void disposeGraphics(Graphic[][] array)
	{
		for(int i = 0; i < array.length; i++)
		{
			if (array[i] != null) disposeGraphics(array[i]);
		}
	}
	
	public static void disposeGraphics(HashMap<Integer, ? extends Graphic> map)
	{
		for(Integer key : map.keySet())
		{
			map.get(key).dispose();
		}
	}
}

