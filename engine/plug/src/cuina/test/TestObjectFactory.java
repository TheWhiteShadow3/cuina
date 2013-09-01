package cuina.test;

import cuina.animation.ModelData;
import cuina.event.Trigger;
import cuina.map.movement.MotorData;
import cuina.object.ObjectData;

import java.util.ArrayList;
import java.util.HashMap;

public class TestObjectFactory
{
	public static ModelData createModel()
	{
		ModelData model = new ModelData();
		model.fileName = "charsets/Gothic-Lolita+44.png";
		model.frames = 4;
		model.animations = 4;
		model.standAnimation = true;
		model.ox = 16;
		model.oy = 48;
		model.animator = "cuina.rpg.CharacterAnimator";
		return model;
	}
	
	public static MotorData createPlayer()
	{
		MotorData motor = new MotorData();
		motor.driver = "cuina.rpg.Player";
		return motor;
	}
	
	public static ObjectData createObject(int id, String name, int x, int y)
	{
		ObjectData data = new ObjectData();
		data.id = id;
		data.name = name;
		data.x = x;
		data.y = y;
		data.z = 0;
		data.extensions = new HashMap<String, Object>();
		data.triggers = new ArrayList<Trigger>();
		return data;
	}
	
	public static ObjectData createDefaultPlayerObject(String name)
	{
		ObjectData data = createObject(1000, name, 320, 240);
		ModelData model = createModel();
		MotorData motor = createPlayer();
		
		data.extensions.put("model", model);
		data.extensions.put("motor", motor);
		return data;
	}
}
