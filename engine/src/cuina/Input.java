package cuina;

import java.awt.Rectangle;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;


/**
 * Überwacht Benutzereingaben und ermöglicht erfolgte Eingaben abzuprüfen.
 * @author TheWhiteShadow
 */
public class Input
{
	public static int[] OK 		= { Keyboard.KEY_RETURN, Keyboard.KEY_NUMPAD0 };
	public static int[] CANCEL 	= { Keyboard.KEY_ESCAPE, Keyboard.KEY_DELETE, Keyboard.KEY_NUMPADCOMMA };
	public static int[] L 		= { Keyboard.KEY_Q};
	public static int[] R 		= { Keyboard.KEY_E };
	public static int[] UP 		= { Keyboard.KEY_UP, 	Keyboard.KEY_NUMPAD8, Keyboard.KEY_W };
	public static int[] RIGHT 	= { Keyboard.KEY_RIGHT, Keyboard.KEY_NUMPAD4, Keyboard.KEY_A };
	public static int[] LEFT 	= { Keyboard.KEY_LEFT,  Keyboard.KEY_NUMPAD6, Keyboard.KEY_D };
	public static int[] DOWN 	= { Keyboard.KEY_DOWN,  Keyboard.KEY_NUMPAD2, Keyboard.KEY_S };
	
	// direction constants (like number's block on keyboards):
//	public static final int DIR_NOTHING 	= 5;
//	public static final int DIR_LEFT_DOWN 	= 1;
//	public static final int DIR_DOWN 		= 2;
//	public static final int DIR_RIGHT_DOWN 	= 3;
//	public static final int DIR_LEFT 		= 4;
//	public static final int DIR_RIGHT 		= 6;
//	public static final int DIR_LEFT_UP 	= 7;
//	public static final int DIR_UP 			= 8;
//	public static final int DIR_RIGHT_UP 	= 9;
	
	public static final int DIR_NOTHING 	= -1;
	public static final int DIR_LEFT_DOWN 	= 225;
	public static final int DIR_DOWN 		= 270;
	public static final int DIR_RIGHT_DOWN 	= 315;
	public static final int DIR_LEFT 		= 180;
	public static final int DIR_RIGHT 		= 0;
	public static final int DIR_LEFT_UP 	= 135;
	public static final int DIR_UP 			= 90;
	public static final int DIR_RIGHT_UP 	= 45;
	
	public static final int CONTROL_ARROWS = 1;
	public static final int CONTROL_WASD = 2;
	public static final int CONTROL_NUMPAD = 4;
	
	public static int REPEAT_DELAY = 30;
	
	// Tasten-Status
	private static int[] keys = new int[256];
	// Maus-Status
	private static int[] mouseKey = new int[4];
//	private static boolean mouseInside = false;

	public static void update()
	{
		if (Keyboard.isCreated())
		{
			Keyboard.poll();
			for (int i= 0; i < 256; i++)
			{
				if (Keyboard.isKeyDown(i))
					keys[i]++;
				else
					keys[i] = 0;
			}
		}
		
		if (Mouse.isCreated())
		{
			Mouse.poll();
			for(int i= 0; i < 4; i++)
			{
				if (Mouse.isButtonDown(i))
				{
					mouseKey[i]++;
				}
				else
					mouseKey[i] = 0;
			}	
		}
	}
	
	/**
	 * Gibt an, ob die Taste mit dem angegebenen KeyCode gedrückt ist.
	 * @param key - KeyCode der zu prüfenden Taste.
	 * @return <b>true</b>, wenn die Taste gedrückt ist, andernfalls <b>false</b>.
	 */
	public static boolean isDown(int key) throws IllegalArgumentException
	{
		if (key < 0 || key > 255) throw new IllegalArgumentException("Invalid KeyCode: " + key);
		return keys[key] > 0;
	}
	
	/**
	 * Gibt an, ob die Taste mit dem angegebenen KeyCode gedrückt ist.
	 * @param keys - KeyCode-Array der zu prüfenden Tasten.
	 * @return <b>true</b>, wenn die Taste gedrückt ist, andernfalls <b>false</b>.
	 */
	public static boolean isDown(int[] keys) throws IllegalArgumentException
	{
		boolean result = false;
		for(int key : keys)
		{
			result |= isDown(key);
		}
		return result;
	}
	
	/**
	 * Gibt an, ob die Taste mit dem angegebenen KeyCode gerade gedrückt wurde.
	 * @param key - KeyCode der zu prüfenden Taste.
	 * @return <b>true</b>, wenn die Taste gerade gedrückt wurde, andernfalls <b>false</b>.
	 */
	public static boolean isPressed(int key) throws IllegalArgumentException
	{
		if (key < 0 || key > 255) throw new IllegalArgumentException("Invalid KeyCode: " + key);
		return keys[key] == 1;
	}
	
	/**
	 * Gibt an, ob die Taste mit dem angegebenen KeyCode gerade gedrückt wurde.
	 * @param keys - KeyCode-Array der zu prüfenden Tasten.
	 * @return <b>true</b>, wenn die Taste gerade gedrückt wurde, andernfalls <b>false</b>.
	 */
	public static boolean isPressed(int[] keys) throws IllegalArgumentException
	{
		boolean result = false;
		for(int key : keys)
		{
			result |= isPressed(key);
		}
		return result;
	}
	
	/**
	 * Gibt an, ob die Taste mit dem angegebenen KeyCode gerade gedrückt wurde.
	 * Gibt intervalmaessig true zurück, wenn die Taste länger als REPEAT_DELAY Frames gedrückt bleibt.
	 * @param key - KeyCode der zu prüfenden Taste.
	 * @return <b>true</b>, wenn die Taste gerade gedrückt wurde oder REPEAT_DELAY Frames gedrückt bleibt, andernfalls <b>false</b>.
	 */
	public static boolean isRepeated(int key) throws IllegalArgumentException
	{
		if (key < 0 || key > 255) throw new IllegalArgumentException("Invalid KeyCode: " + key);
		return isPressed(key) || (keys[key] > REPEAT_DELAY && keys[key] % 3 == 0);
	}
	
	/**
	 * Gibt an, ob die Taste mit dem angegebenen KeyCode gerade gedrückt wurde.
	 * Gibt intervalmaessig true zurück, wenn die Taste länger als REPEAT_DELAY Frames gedrückt bleibt.
	 * @param keys - KeyCode-Array der zu prüfenden Tasten.
	 * @return <b>true</b>, wenn die Taste gerade gedrückt wurde oder REPEAT_DELAY Frames gedrückt bleibt, andernfalls <b>false</b>.
	 */
	public static boolean isRepeated(int[] keys) throws IllegalArgumentException
	{
		boolean result = false;
		for(int key : keys)
		{
			result |= isRepeated(key);
		}
		return result;
	}
	
	public static void resetKey(int key)
	{
		keys[key] = 0;
	}
	
	public static void consume()
	{
		for (int i = 0; i < keys.length; i++)
		{
			keys[i] = 0;
		}
	}
	
	private static int lastKey = 0;
	public static int dir4()
	{
		if(!isDown(lastKey))
		{
			lastKey = 0;
		}
		
		if((isPressed(Keyboard.KEY_UP)) || ((lastKey == 0)&&(isDown(Keyboard.KEY_UP))))
		{
			lastKey = Keyboard.KEY_UP;
		}
		else if((isPressed(Keyboard.KEY_DOWN)) || ((lastKey == 0)&&(isDown(Keyboard.KEY_DOWN))))
		{
			lastKey = Keyboard.KEY_DOWN;
		}
		else if((isPressed(Keyboard.KEY_LEFT)) || ((lastKey == 0)&&(isDown(Keyboard.KEY_LEFT))))
		{
			lastKey = Keyboard.KEY_LEFT;
		}
		
		else if((isPressed(Keyboard.KEY_RIGHT)) || ((lastKey == 0)&&(isDown(Keyboard.KEY_RIGHT))))
		{
			lastKey = Keyboard.KEY_RIGHT;
		}
		
		switch(lastKey)
		{
			case Keyboard.KEY_UP:	 return DIR_UP;
			case Keyboard.KEY_DOWN:  return DIR_DOWN;
			case Keyboard.KEY_LEFT:  return DIR_LEFT;
			case Keyboard.KEY_RIGHT: return DIR_RIGHT;
		}
		return DIR_NOTHING;
	}
	
	private static int dir8(boolean left, boolean right, boolean up, boolean down)
	{
		int keys = DIR_NOTHING;
		// illegale kombinationen
		if (up   && down)  return DIR_NOTHING;
		if (left && right) return DIR_NOTHING;
		
		if (up)
		{
			if (left) 		keys = DIR_LEFT_UP;
			else if (right) keys = DIR_RIGHT_UP;
			else 			keys = DIR_UP;
		}
		else if (down)
		{
			if (left) 		keys = DIR_LEFT_DOWN;
			else if (right) keys = DIR_RIGHT_DOWN;
			else 			keys = DIR_DOWN;
		}
		else if (left) 		keys = DIR_LEFT;
		else if (right) 	keys = DIR_RIGHT;
		return keys;
	}
	
	/**
	 * 8-Richtungseingabe über Pfeiltasten, WASD oder NUMPAD.
	 * @return Richtung entsrechnend Zehnertastertur.
	 */
	public static int dir8()
	{
		return dir8(CONTROL_ARROWS + CONTROL_WASD + CONTROL_NUMPAD);
	}
	
	/**
	 * 8-Richtungseingabe über die angegebenen Tasten.
	 * @return Richtung entsrehcnend Zehnertastertur.
	 */
	public static int dir8(int control_type)
	{

		boolean left = ((control_type & CONTROL_ARROWS) != 0) && isDown(Keyboard.KEY_LEFT) ||
					   ((control_type & CONTROL_WASD)   != 0) && isDown(Keyboard.KEY_A) ||
					   ((control_type & CONTROL_NUMPAD) != 0) && isDown(Keyboard.KEY_NUMPAD4);
		
		boolean right = ((control_type & CONTROL_ARROWS) != 0) && isDown(Keyboard.KEY_RIGHT) ||
				   		((control_type & CONTROL_WASD)   != 0) && isDown(Keyboard.KEY_D) ||
				   		((control_type & CONTROL_NUMPAD) != 0) && isDown(Keyboard.KEY_NUMPAD6);
		
		boolean up = ((control_type & CONTROL_ARROWS) != 0) && isDown(Keyboard.KEY_UP) ||
				   	 ((control_type & CONTROL_WASD)   != 0) && isDown(Keyboard.KEY_W) ||
				   	 ((control_type & CONTROL_NUMPAD) != 0) && isDown(Keyboard.KEY_NUMPAD8);
		
		boolean down = ((control_type & CONTROL_ARROWS) != 0) && isDown(Keyboard.KEY_DOWN) ||
				   	   ((control_type & CONTROL_WASD)   != 0) && isDown(Keyboard.KEY_S) ||
				   	   ((control_type & CONTROL_NUMPAD) != 0) && isDown(Keyboard.KEY_NUMPAD2);
		
		return dir8(left, right, up, down);
	}
	
	public static int mouseX()
	{
		if (!Mouse.isInsideWindow()) return -1;
		return Mouse.getX();// - Graphics.ox;
	}
	
	public static int mouseY()
	{
		if (!Mouse.isInsideWindow()) return -1;
		return Display.getDisplayMode().getHeight() - Mouse.getY();// - Graphics.oy;
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
	
//	protected static int realMouseX()
//	{
//		if (!mouse_inside) return -1;
//		return mouse_x;
//	}
//	
//	protected static int realMouseY()
//	{
//		if (!mouse_inside) return -1;
//		return mouse_y;
//	}
	
	/**
	 * Gibt an, ob die angegebene Maus-Taste gedrückt ist.
	 * @param key Nummer der Maustaste (0 - 3)
	 * @return true, wenn die Taste gerdückt ist, andernfalls false.
	 * @throws IllegalArgumentException
	 */
	public static boolean mouseDown(int key) throws IllegalArgumentException
	{
		if (key < 0 || key > 3) throw new IllegalArgumentException("Invalid MouseButton: " + key);
		return mouseKey[key] > 0;
	}
	
	/**
	 * Gibt an, ob die angegebene Maus-Taste gerade gedrückt wird.
	 * @param key Nummer der Maustaste (0 - 3)
	 * @return true, wenn die Taste gerade gerdückt wird, andernfalls false.
	 * @throws IllegalArgumentException wenn 
	 */
	public static boolean mousePressed(int key) throws IllegalArgumentException
	{
		if (key < 0 || key > 3) throw new IllegalArgumentException("Invalid MouseButton: " + key);
		return mouseKey[key] == 1;
	}
}
