package cuina.rpg;

import cuina.Game;
import cuina.event.Event;
import cuina.input.DirectionalControl;
import cuina.input.Input;
import cuina.movement.Driver;
import cuina.movement.Motor;
import cuina.world.CuinaMask;
import cuina.world.CuinaObject;
import cuina.world.CuinaWorld;

import org.lwjgl.input.Keyboard;

public class Player implements Driver
{
	/** Prüft den Trigger bei Kollision + Aktions-Taste mit einem Spieler. */
	public static final Event ACTION_BY_PLAYER = Event.getEvent("cuina.rpg.ActionByPlayer");
	
	public static boolean INPUT_8 = false;
	
	private static final long	serialVersionUID	= -8876917561357724127L;
	
	public String CONTROL_MOVE 		= "a1";
	public String CONTROL_ACTION 	= "c2";
	public String CONTROL_RUN 		= "c3";
	
	private Motor motor;
	
	public Player()
	{
	}
	
	@Override
	public void init(Motor motor)
	{
		this.motor = motor;
	}

	@Override
	public void update()
	{
		handleDebugMovement();
		
//		DirectionalControl c = (DirectionalControl) Input.getControl(CONTROL_MOVE);
//		if (c != null)
//		{
//			float dir = c.getDirection();
//			if (dir != -1) motor.setDirection(dir);
//			float speed = 1.5f * c.getValue();
//			if (Input.isDown(CONTROL_RUN)) speed *= 2;
//			motor.setSpeed(speed);
//		}
		
		// DEBUG: Teste Platform-Fähigkeiten
		
		if (Input.isDown("left"))
		{
			motor.setDirection(180);
			motor.setSpeedX(-3);
		}
		else if (Input.isDown("right"))
		{
			motor.setDirection(0);
			motor.setSpeedX(3);
		}
		else
		{
			motor.setDirection(270);
			motor.setSpeedX(0);
		}
		if (Input.isPressed("c1")) motor.setSpeedY(-30);
		
		handleCollisions();
	}
	
	private CuinaMask getCollisionMask()
	{
		CuinaObject object = motor.getObject();
		return (CuinaMask) object.getExtension(CuinaMask.EXTENSION_KEY);
	}
	
	private void handleDebugMovement()
	{
		CuinaMask box = getCollisionMask();
		if (box == null) return;
		
		box.setThrough(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT));
	}
	
	private void handleCollisions()
	{
		CuinaMask box = getCollisionMask();
		if (box == null) return;
		
		CuinaWorld world = Game.getWorld();
		if (Input.isPressed(CONTROL_ACTION) && (world == null || !world.isFreezed()))
		{
			CuinaObject other = box.testRelativePosition(motor.getDX(2), motor.getDY(2), 0);
			if (other != null)
			{
				CuinaObject self = motor.getObject();
				other.testTriggers(ACTION_BY_PLAYER, self.getID(), other, self);
			}
		}
	}

	@Override
	public void blocked() {}
}
