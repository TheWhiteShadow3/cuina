package cuina.rpg;

import cuina.Game;
import cuina.Input;
import cuina.event.Event;
import cuina.map.CollisionBox;
import cuina.movement.Driver;
import cuina.movement.Motor;
import cuina.world.CuinaObject;
import cuina.world.CuinaWorld;

import org.lwjgl.input.Keyboard;

public class Player implements Driver
{
	/** Prüft den Trigger bei Kollision + Aktions-Taste mit einem Spieler. */
	public static final Event ACTION_BY_PLAYER = Event.getEvent("cuina.map.object.ActionByPlayer");
	
	public static boolean INPUT_8 = false;
	
	private static final long	serialVersionUID	= -8876917561357724127L;
	
	private static int count = 0;
	private int index;

	private Motor motor;
	
	public Player()
	{
		this.index = count;
		count++;
	}
	
	@Override
	public void init(Motor motor)
	{
		this.motor = motor;
	}
	
	public static Player newInstance()
	{
//		MapObject playerObject = new MapObject();
//		Model playerModel = new Model();
//		playerModel.setSprite(new Sprite(graphic_file), 4, 4);
//		playerObject.setModel(playerModel);
		return new Player();
	}

	@Override
	public void update()
	{
		CuinaObject object = motor.getObject();
		CollisionBox box = (CollisionBox) object.getExtension(CollisionBox.EXTENSION_KEY);
		if (box != null)
		{
			box.setThrough(Input.isDown(Keyboard.KEY_LCONTROL));
		}
		
		int dir = -1;
//		if (INPUT_8)
//			dir = Input.dir8();
//		else
//			dir = Input.dir4();
		switch(index)
		{
			case 0: dir = Input.dir8(Input.CONTROL_ARROWS); break;
			case 1: dir = Input.dir8(Input.CONTROL_WASD); break;
			case 2: dir = Input.dir8(Input.CONTROL_NUMPAD); break;
		}
		if (dir != -1)
		{
			motor.setDirection(dir);
			if (Input.isDown(Keyboard.KEY_LSHIFT))
				motor.setSpeed(3);
			else
				motor.setSpeed(1.5f);
//			System.out.println(getObject().getModel().getY());
		}
		else
		{
			motor.setSpeed(0);
		}
		
//		Model model = (Model) object.getExtension("model");
//		if (Input.isDown(Keyboard.KEY_Q))
//		{
//			object.setZ(object.getZ() + 1);
//			System.out.println("Model.Z = " + model.getZ());
////			Sprite sprite = object.getModel().getSprite();
////			sprite.setAngle(sprite.getAngle() + 0.5f);
//		}
//		if (Input.isDown(Keyboard.KEY_A))
//		{
//			object.setZ(object.getZ() - 1);
//			System.out.println("Model.Z = " + model.getZ());
//		}
		
		CuinaWorld world = Game.getWorld();
		if (Input.isPressed(Keyboard.KEY_RETURN) && (world == null || !world.isFreezed()))
		{
			CuinaObject other = box.testRelativePosition(motor.getDX(2), motor.getDY(2));
			if (other != null && other instanceof CuinaObject)
			{
				CuinaObject self = motor.getObject();
				other.testTriggers(ACTION_BY_PLAYER, self.getID(), self);
			}
		}
	}

	@Override
	public void blocked() {}
}
