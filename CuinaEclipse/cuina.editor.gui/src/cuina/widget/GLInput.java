package cuina.widget;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.opengl.GLCanvas;

import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.input.Input;

public class GLInput implements Input
{
	private GLCanvas canvas;
	private GUI gui = null;

	private int mouseX, mouseY, mouseButton, mouseWheel;
	private boolean mousePressed, mouseStatusChanged, mouseWheelStatusChanged;
	
	private int keyCode;
	private char keyChar;
	private boolean keyPressed, keyStatusChanged;
	
	public GLInput(GLCanvas glCanvas)
	{
		this.canvas = glCanvas;

		canvas.addMouseListener(new MouseListener()
		{
			
			@Override
			public void mouseUp(MouseEvent e)
			{
				mouseX = e.x;
				mouseY = e.y;
				mouseButton = e.button;
				mousePressed = false;
				
				mouseStatusChanged = true;
				
				update(gui);
				canvas.redraw();
			}
			
			@Override
			public void mouseDown(MouseEvent e)
			{
				mouseX = e.x;
				mouseY = e.y;
				mouseButton = e.button;
				mousePressed = true;

				mouseStatusChanged = true;

				update(gui);
				canvas.redraw();
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e)
			{
				mouseX = e.x;
				mouseY = e.y;
				mouseButton = e.button;
				mousePressed = true;

				mouseStatusChanged = true;
				
				update(gui);
				canvas.redraw();
			}
		});
		

		canvas.addMouseWheelListener(new MouseWheelListener()
		{
			
			@Override
			public void mouseScrolled(MouseEvent e)
			{
				mouseWheel = e.count;

				update(gui);
				canvas.redraw();
			}
		});


		canvas.addKeyListener(new KeyListener()
		{
			
			@Override
			public void keyReleased(KeyEvent e)
			{
				keyCode = e.keyCode;
				keyChar = e.character;
				keyPressed = false;
				
				keyStatusChanged = true;
				
				update(gui);
				canvas.redraw();
			}
			
			@Override
			public void keyPressed(KeyEvent e)
			{
				keyCode = e.keyCode;
				keyChar = e.character;
				keyPressed = true;
				
				keyStatusChanged = true;

				update(gui);
				canvas.redraw();
				
			}
		});
	}

	@Override
	public boolean pollInput(final GUI gui)
	{
		if(canvas == null || gui == null)
			return false;
		
		this.gui = gui;

		if(mouseStatusChanged)
			gui.handleMouse(mouseX, mouseY, mouseButton, mousePressed);
		mouseStatusChanged = false;
		
		if(mouseWheelStatusChanged)
			gui.handleMouseWheel(mouseWheel);
		mouseWheelStatusChanged = false;
		
		if(keyStatusChanged)
			gui.handleKey(keyCode, keyChar, keyPressed);
		keyStatusChanged = false;
		
		return true;
	}
	
	private void update(final GUI gui)
	{
		if(gui != null)
		{
			gui.setSize();
			gui.updateTime();
			gui.handleInput();
			gui.handleKeyRepeat();
			gui.handleTooltips();
			gui.updateTimers();
			gui.invokeRunables();
			gui.validateLayout();
			gui.draw();
		}
		
	}

}
