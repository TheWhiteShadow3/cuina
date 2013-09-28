package cuina.test;

import cuina.Game;
import cuina.animation.ModelData;
import cuina.event.Trigger;
import cuina.object.BaseObject;
import cuina.object.ObjectData;
import cuina.world.CuinaWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class AnimationStressTest
{
	private static ModelData model;
	private static int timer;
	private static int currentID;

	public static void setup()
	{
		// Erstelle Model-Daten, die wir für alle Objekte benutzen können.
		model = new ModelData();
		model.fileName = "charsets/Gothic-Lolita+44.png";
		model.frames = 4;
		model.animations = 4;
		model.standAnimation = true;
		model.ox = 16;
		model.oy = 48;
		model.animator = null;//"cuina.rpg.CharacterAnimator";
	}

	public static void update()
	{
		Game.getWorld().update();
		
		if (timer++ % 10 == 0)
		{
			createObject();
		}
	}
	
	private static void createObject()
	{
		/*
		 * Erstelle ein Objekt pro Frame.
		 * Symbolisiert das dynamische laden eines Templates und Erzeugen von Objekten durch ein Skript.
		 */
		ObjectData data = new ObjectData();
		data.id = currentID++;
		data.name = "Yuna";
		data.x = (int) (Math.random() * 640);
		data.y = (int) (Math.random() * 480 + 16);
		data.z = 1000 - data.y;
		data.extensions = new HashMap<String, Object>();
		data.triggers = new ArrayList<Trigger>();
		
		data.extensions.put("model", model);
		/*
		 * Erstelle eine Visuelle Erweiterung neben dem Model.
		 * Eine Lebensanzeige testet auch die erforderliche Synchronität zum Objekt. 
		 */
		data.extensions.put("health", new HealthBar());
		
		/*
		 * Erstelle einige Dummy-Erweiterungen um eine realistische Plugin-Nutzung zu testen.
		 * Hier werden Dummy-Zugriffe auf die Erweiterungen durchgeführt.
		 */
		data.extensions.put("dumm1", new DummyExtension());
		data.extensions.put("dumm2", new DummyExtension());
		data.extensions.put("dumm3", new DummyExtension());
		
		CuinaWorld world = Game.getWorld();
		BaseObject obj = new BaseObject(data);
		obj.setID(world.getAvilableID());
		world.addObject(obj);
		
		Set<Integer> keys = world.getObjectIDs();
		System.out.println("Objekte: " + keys.size());
	}
}
