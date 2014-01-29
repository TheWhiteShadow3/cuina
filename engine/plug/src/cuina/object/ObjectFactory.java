package cuina.object;

import cuina.Game;
import cuina.Logger;
import cuina.database.Database;
import cuina.util.Rectangle;
import cuina.util.Vector;
import cuina.world.CuinaMask;
import cuina.world.CuinaModel;
import cuina.world.CuinaWorld;


public class ObjectFactory
{
	private ObjectFactory() {}
	
	public static BaseObject createFromTemplate(Vector pos, String template)
	{
		return createFromTemplate(pos.x, pos.y, pos.z, template);
	}
	
	public static BaseObject createFromTemplate(float x, float y, float z, String template)
	{
		ObjectTemplate tObj = Database.<ObjectTemplate>get("Template", template);
		BaseObject obj = new BaseObject(tObj.createNewObject(-1));
		addObject(obj);
		
		return obj;
	}
	
	public static BaseObject createFromImage(Vector pos, String imageName, Rectangle mask, boolean through)
	{
		return createFromImage(pos.x, pos.y, pos.z, imageName, mask, through);
	}
	
	public static BaseObject createFromImage(float x, float y, float z, String imageName, Rectangle mask, boolean through)
	{
		BaseObject obj = new BaseObject();
		obj.setPosition(x, y, z);
		obj.addExtension(CuinaModel.EXTENSION_KEY, new StaticModel(imageName));
		obj.addExtension(CuinaMask.EXTENSION_KEY, new CollisionMask(obj, mask, through));
		addObject(obj);
		
		return obj;
	}
	
	private static boolean addObject(BaseObject obj)
	{
		CuinaWorld world = Game.getWorld();
		if (world == null)
		{
			Logger.log(ObjectFactory.class, Logger.WARNING, "Can not add Object. World does not exists.");
		}
		else
		{
			obj.setID(world.getAvilableID());
			world.addObject(obj);
		}
		return false;
	}
}
