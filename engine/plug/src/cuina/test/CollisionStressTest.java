package cuina.test;

import cuina.animation.ModelData;
import cuina.event.Trigger;
import cuina.map.BoxData;
import cuina.map.CollisionBox;
import cuina.map.GameMap;
import cuina.movement.Motor;
import cuina.movement.MotorData;
import cuina.object.BaseObject;
import cuina.object.ObjectData;
import cuina.rpg.CharacterAnimator;
import cuina.rpg.CharacterDriver;
import cuina.world.CuinaMask;
import cuina.world.CuinaModel;
import cuina.world.CuinaMotor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class CollisionStressTest
{
	private static ModelData modelData;
	private static BoxData maskData;
	private static MotorData motorData;
	private static ObjectData objectData;

	public static void setup()
	{
		// Erstelle Model-Daten, die wir für alle Objekte benutzen können.
		modelData = new ModelData();
		modelData.frames = 4;
		modelData.animations = 4;
		modelData.standAnimation = false;
		modelData.ox = 16;
		modelData.oy = 48;
		modelData.animator = CharacterAnimator.class.getName();
		
		maskData = new BoxData();
		maskData.x = -12;
		maskData.y = -16;
		maskData.width = 24;
		maskData.height = 16;
		maskData.through = false;
		
		motorData = new MotorData();
		motorData.driver = CharacterDriver.class.getName();
		
		createObjectData();
		
		for(int i = 0; i < 100; i++)
		{
			createObject();
		}
	}
	
	private static void createObjectData()
	{
		objectData = new ObjectData();
		objectData.name = "Statist";
		objectData.extensions = new HashMap<String, Object>();
		objectData.triggers = new ArrayList<Trigger>();
		objectData.extensions.put(CuinaModel.EXTENSION_KEY, modelData);
		objectData.extensions.put(CuinaMask.EXTENSION_KEY, maskData);
		objectData.extensions.put(CuinaMotor.EXTENSION_KEY, motorData);
//		objectData.x = 100;
//		objectData.y = GameMap.getInstance().getHeight() - 8;
	}
	
	private static void createObject()
	{
		modelData.fileName = chooseFileName();
		System.out.println("Image: " + modelData.fileName);
		
		GameMap world = GameMap.getInstance();
		BaseObject obj = new BaseObject(objectData);
		
		CollisionBox mask = (CollisionBox) obj.getExtension(CuinaMask.EXTENSION_KEY);
		do
		{
			mask.setPosition(
					(int) (Math.random() * world.getWidth()),
					(int) (Math.random() * world.getHeight()));
		}
		while(!mask.isRelativePositionFree(0, 0, 0));
		obj.setPosition(mask.getX(), mask.getY(), 0);
		
		obj.setID(world.getAvilableID());
		world.addObject(obj);
		
		Motor motor = (Motor) obj.getExtension(CuinaMotor.EXTENSION_KEY);
		CharacterDriver driver = (CharacterDriver) motor.getDriver();
		driver.setMoveSpeed(1);
		driver.setMoveType(CharacterDriver.MOVE_RANDOM);
		
		Set<Integer> keys = world.getObjectIDs();
		System.out.println("Objekte: " + keys.size());
	}

	private static String chooseFileName()
	{
		int num = (int) (Math.random() * 16 + 1);
		
		if (num <= 7)
		{
			return "charsets/Schuljunge " + num + ".png";  
		}
		else
		{
			return "charsets/Schulmädchen " + (num - 7) + ".png"; 
		}
	}
}
