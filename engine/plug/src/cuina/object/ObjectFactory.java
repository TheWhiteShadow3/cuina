package cuina.object;


public class ObjectFactory
{
//	private ObjectFactory() {}
//	
//	public static MapObject newImageObject(String fileName)
//	{
//		return newImageObject(fileName, 0, 0);
//	}
//	
//	public static MapObject newImageObject(String fileName, int frames, int directions)
//	{
//		MapObject obj = new MapObject();
//		obj.setName(fileName);
//		obj.setModel(new Model(fileName, frames, directions, false));
//		addObject(obj);
//		return obj;
//	}
//	
//	public static MapObject newAnimation(String fileName, int index)
//	{
//		return newAnimation(fileName, 0, 0, index);
//	}
//	
//	public static MapObject newAnimation(String fileName, int frames, int directions, int index)
//	{
//		MapObject obj = new MapObject();
//		obj.setName(fileName);
//		obj.setModel(new Model(fileName, frames, directions, true));
//		obj.getModel().setAnimationIndex(index);
//		addObject(obj);
//		return obj;
//	}
//	
//	private static int addObject(CuinaObject obj)
//	{
//		CuinaWorld world = Game.getWorld();
//		if (world != null)
//		{
//			return world.addObject(obj);
//		}
//		return -1;
//	}
}
