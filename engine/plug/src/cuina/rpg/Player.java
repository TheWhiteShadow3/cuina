package cuina.rpg;

import cuina.Game;
import cuina.event.Event;
import cuina.input.DirectionalControl;
import cuina.input.Input;
import cuina.map.CollisionBox;
import cuina.movement.Driver;
import cuina.movement.Motor;
import cuina.object.CollisionMask;
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
	private float playerSpeed = 1.5f;
	
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
		
		DirectionalControl c = (DirectionalControl) Input.getControl(CONTROL_MOVE);
		if (c != null)
		{
			float dir = c.getDirection();
			if (dir != -1) motor.setDirection(dir);
			float cSpeed = playerSpeed * c.getValue();
			if (Input.isDown(CONTROL_RUN)) cSpeed *= 2;
			motor.setSpeed(cSpeed);
		}
		
		handleCollisions();
	}
	
	private CollisionBox getCollisionMask()
	{
		CuinaObject object = motor.getObject();
		CuinaMask mask = (CuinaMask) object.getExtension(CuinaMask.EXTENSION_KEY);
		if (mask instanceof CollisionBox)
			return (CollisionBox) mask;
		else
			return null;
	}
	
	private void handleDebugMovement()
	{
		CollisionBox mask = getCollisionMask();
		if (mask == null) return;
		
		mask.setThrough(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT));
	}
	
	private void handleCollisions()
	{
		CollisionBox mask = getCollisionMask();
		if (mask == null) return;
		
		CuinaWorld world = Game.getWorld();
		if (Input.isPressed(CONTROL_ACTION) && (world == null || !world.isFreezed()))
		{
			CuinaObject other = mask.getObjectOn(mask.getX() + motor.getDX(2), mask.getY() + motor.getDY(2));
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
