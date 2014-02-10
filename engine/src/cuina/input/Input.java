package cuina.input;

import cuina.input.Control.ControllerButton;
import cuina.input.Control.Key;
import cuina.input.Control.MouseButton;
import cuina.input.DirectionalControl.DirectionalButton;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;


/**
 * Überwacht Benutzereingaben und ermöglicht erfolgte Eingaben abzuprüfen.
 * @author TheWhiteShadow
 */
public class Input
{
	public static int REPEAT_DELAY = 30;
	
	public static final int CONTROLLER_1	= 0;
	public static final int CONTROLLER_2 	= 1;
	public static final int CONTROLLER_3 	= 2;
	public static final int CONTROLLER_4 	= 3;

	private static final Map<String, Control> controls = new HashMap<String, Control>();
	
	public static void initControls()
	{
		addControl(new Control("mouse.left", new MouseButton(MouseButton.MOUSE_LBUTTON) ));
		addControl(new Control("mouse.right", new MouseButton(MouseButton.MOUSE_RBUTTON) ));
		
		addControl(new Control("left", new Key(Key.KEY_LEFT) ));
		addControl(new Control("right", new Key(Key.KEY_RIGHT) ));
		addControl(new Control("up", new Key(Key.KEY_UP) ));
		addControl(new Control("down", new Key(Key.KEY_DOWN) ));
		
		addControl(new Control("c1", new Key(Key.KEY_SPACE), new ControllerButton(CONTROLLER_1, 1) ));
		addControl(new Control("c2", new Key(Key.KEY_RETURN), new ControllerButton(CONTROLLER_1, 2) ));
		addControl(new Control("c3", new Key(Key.KEY_LCONTROL), new ControllerButton(CONTROLLER_1, 3) ));
		addControl(new Control("c4", new Key(Key.KEY_ESCAPE), new ControllerButton(CONTROLLER_1, 4) ));
		addControl(new Control("c5", new Key(Key.KEY_Q), new ControllerButton(CONTROLLER_1, 5) ));
		addControl(new Control("c6", new Key(Key.KEY_E), new ControllerButton(CONTROLLER_1, 6) ));
		addControl(new DirectionalControl("a1",
				DirectionalControl.ARROWS, new DirectionalButton(CONTROLLER_1, 1, 2) ));
		addControl(new DirectionalControl("a2",
				DirectionalControl.WASD, new DirectionalButton(CONTROLLER_1, 3, 4) ));
//		}
//		else
//		{
//			for(Entry entry : section.getEntries()) try
//			{
//				String value = entry.getValue();
//				char ch = value.charAt(0);
//				int key = Integer.parseInt(value.substring(1));
//				Source s;
//				switch(ch)
//				{
//					case 'k': s = new Key(key); break;
//					case 'm': s = new MouseButton(key); break;
//					case 'c': s = new ControllerButton(CONTROLLER_1, key); break;
//					case 'd': s = new DirectionalButton(CONTROLLER_1, key); break;
//				}
//				addControl(new Control(
//			}
//			catch (Exception e)
//			{
//				Logger.log(Input.class, Logger.ERROR, e);
//			}
//			
//		}
	}
	
	public static void update()
	{
		if (Keyboard.isCreated()) Keyboard.poll();
		if (Mouse.isCreated()) Mouse.poll();
		if (Controllers.isCreated()) Controllers.poll();
		
		for (Control c : controls.values())
			c.update();
	}
	
	/**
	 * Fügt ein neues Control hinzu.
	 * @param control Das Control.
	 */
	public static void addControl(Control control)
	{
		controls.put(control.getName(), control);
	}
	
	/**
	 * Gibt das Control mit dem angegebenen Namen zurück.
	 * @param name Name des Controls.
	 * @return Das Control.
	 */
	public static Control getControl(String name)
	{
		return controls.get(name);
	}
	
	/**
	 * Gibt eine Liste aller registrierten Controls zurück.
	 * @return Liste aller registrierten Controls.
	 */
	public static List<Control> getControls()
	{
		return new ArrayList<Control>(controls.values());
	}
	
	public static boolean isPressed(String cntlName)
	{
		if (cntlName == null) return false;
		
		Control b = controls.get(cntlName);
		if (b == null) return false;
		
		return b.isPressed();
	}
	
	public static boolean isDown(String cntlName)
	{
		if (cntlName == null) return false;
		
		Control b = controls.get(cntlName);
		if (b == null) return false;
		
		return b.isDown();
	}
	
	public static boolean isRepeated(String cntlName)
	{
		if (cntlName == null) return false;
		
		Control b = controls.get(cntlName);
		if (b == null) return false;
		
		return b.isRepeated();
	}
	
	public static void resetAll(int key)
	{
		for (int i = 0; i < controls.size(); i++)
		{
			controls.get(i).reset();
		}
	}
	
	public static int mouseX()
	{
		if (!Mouse.isInsideWindow()) return -1;
		return Mouse.getX();
	}
	
	public static int mouseY()
	{
		if (!Mouse.isInsideWindow()) return -1;
		return Display.getDisplayMode().getHeight() - Mouse.getY();
	}
	
	/**
	 * Gibt an, ob die Maus innerhalb eines angegebenen Rechtecks liegt.
	 * @param rect Zu prüfendes Rechteck.
	 * @return true, wenn die Maus im Rechteck liegt, andernfalls false.
	 */
	public static boolean mouseInRect(Rectangle rect)
	{
		return rect.contains(mouseX(), mouseY());
	}
}
