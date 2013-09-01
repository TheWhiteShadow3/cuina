package cuina.data;

import cuina.database.DatabaseObject;

import java.io.IOException;

public class ObjectTemplate implements DatabaseObject
{
	private static final long	serialVersionUID	= 4006961914581064447L;
	
	private String key;
	public String name;
	public MapObject sourceObject;
	public boolean isExternal = false;
	
	public ObjectTemplate()
	{
		this.name = "new Template";
		this.sourceObject = new MapObject();
	}
	
	public ObjectTemplate(MapObject sourceObject, String name)
	{
		this.name = name;
		this.sourceObject = sourceObject;
	}
	
	@Override
	public void setKey(String key)
	{
		this.key = key;
	}

	@Override
	public String getKey()
	{
		return key;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
		if (sourceObject != null)
			sourceObject.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}
	
	/**
	 * Setzt die Position der Quell-Objekts.
	 * @param x X-Koordinate in Pixel.
	 * @param y Y-Koordinate in Pixel.
	 */
	public void setPosition(int x, int y)
	{
		sourceObject.x = x;
		sourceObject.y = y;
	}
	
	public MapObject createNewObject(int id) throws IOException
	{
		MapObject obj = new MapObject();
		obj.id 		= id;
		obj.name 	= sourceObject.name;
		obj.x 		= sourceObject.x;
		obj.y 		= sourceObject.y;
		obj.z 		= sourceObject.z;
		obj.cMask 	= sourceObject.cMask;
		obj.templateName = name;
		return obj;
//		if (sourceObject.model != null)
//		{
//			obj.model = new Model();
////			if (false)//isExternal)
////			{	// Erstelle einen Pfad relativ zum Projekt
////				Path path = Paths.get(sourceObject.model.fileName);
////				Path newPath = Ress.toProjectPath(path);
////				obj.model.fileName = newPath.toString();
////				// Ändere den Pfad realtiv zum Editor um
////				String projectDir = System.getProperty("project.dir");
////				newPath = Paths.get(projectDir, newPath.toString());
////				if (Files.notExists(newPath))
////				{
////					Files.createDirectories(newPath.getParent());
////					Files.copy(path, newPath);
////				}
////			}	
////			else
////			{
////				obj.model.fileName = sourceObject.model.fileName;
////			}
//			
//			obj.model.fileName 	 = sourceObject.model.fileName;
//			obj.model.frames 	 = sourceObject.model.frames;
//			obj.model.directions = sourceObject.model.directions;
//			obj.model.ox 		 = sourceObject.model.ox;
//			obj.model.oy 		 = sourceObject.model.oy;
//		}
//		
//		if (sourceObject.motor != null)
//		{
//			obj.motor = new Motor();
//			obj.motor.direction = sourceObject.motor.direction;
//			obj.motor.motorType = sourceObject.motor.motorType;
//			obj.motor.moveSpeed = sourceObject.motor.moveSpeed;
//			obj.motor.moveType 	= sourceObject.motor.moveType;
//		}
//		return obj;
	}
}