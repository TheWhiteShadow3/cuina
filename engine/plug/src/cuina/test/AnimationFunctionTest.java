package cuina.test;

import static cuina.test.TestObjectFactory.createDefaultPlayerObject;

import cuina.Game;
import cuina.Input;
import cuina.animation.ModelImpl;
import cuina.movement.Motor;
import cuina.movement.MovementUtil;
import cuina.object.BaseObject;
import cuina.object.ObjectData;
import cuina.rpg.CharacterAnimator;
import cuina.rpg.CharacterDriver;
import cuina.rpg.MoveRoute;
import cuina.rpg.MoveRoute.MoveCommand;
import cuina.rpg.Player;

import org.lwjgl.input.Keyboard;

@SuppressWarnings("deprecation")
public class AnimationFunctionTest
{
	private static BaseObject obj;

	public static void setup()
	{
		ObjectData data = createDefaultPlayerObject("Yuna");
		
		obj = new BaseObject(data);
		Game.getWorld().addObject(obj);
	}
	
	public static void update()
	{
		ModelImpl model = (ModelImpl) obj.getExtension("model");
		Motor motor = (Motor) obj.getExtension("motor");
		
		if (Input.isPressed(Keyboard.KEY_NEXT))
		{
			model.setAnimationIndex(model.getAnimationIndex() + 1);
		}
		
		if (Input.isPressed(Keyboard.KEY_PRIOR))
		{
			model.setAnimationIndex(model.getAnimationIndex() - 1);
		}
		
		if (Input.isPressed(Keyboard.KEY_END))
		{
			model.setAnimate(!model.isAnimate());
		}
		
		if (Input.isPressed(Keyboard.KEY_HOME))
		{
			model.setFrame(0);
		}
		
		if (Input.isPressed(Keyboard.KEY_A))
		{
			model.setAnimator(model.getAnimator() == null ? new CharacterAnimator() : null);
		}
		
		if (Input.isPressed(Keyboard.KEY_P))
		{
			motor.setDriver(new Player());
		}
		
		if (Input.isPressed(Keyboard.KEY_C))
		{
			setCharacterDriverFor(motor);
		}
		
		if (Input.mousePressed(0))
		{
			float dist = MovementUtil.getDistance(Input.mouseX(), Input.mouseY(), obj.getX(), obj.getY());
			float dir  = MovementUtil.getDirection(obj.getX(), obj.getY(), Input.mouseX(), Input.mouseY());
			
			MoveRoute route = new MoveRoute();
			route.commands = new MoveCommand[3];
			route.commands[0] = new MoveCommand(MoveRoute.SET_DIRECTION, dir);
			route.commands[1] = new MoveCommand(MoveRoute.MOVE, dist);
			route.commands[2] = new MoveCommand(MoveRoute.NONE, 0);
			
			setCharacterDriverFor(motor);
			
			CharacterDriver dirver = (CharacterDriver) motor.getDriver();
			dirver.setMoveRoute(route, true);
		}
	}
	
	private static void setCharacterDriverFor(Motor motor)
	{
		CharacterDriver driver = new CharacterDriver();
		driver.setMoveSpeed(1.2f);
		driver.setMoveType(CharacterDriver.MOVE_RANDOM);
		motor.setDriver(driver);
	}
}
