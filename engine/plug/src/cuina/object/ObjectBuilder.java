package cuina.object;


public class ObjectBuilder
{
//	private MapObject obj;
//
//	public ObjectBuilder()
//	{
//		this.obj = new MapObject();
//	}
//	
//	public ObjectBuilder(String templateName)
//	{
//		cuina.data.MapObject source = Database.<ObjectTemplate>get("ObjectTemplate", templateName).sourceObject;
////		this.obj = new MapObject(source);
//	}
//	
//	public MapObject getObject()
//	{
//		return obj;
//	}
//	
//	public ObjectBuilder useAngle(boolean value)
//	{
//		obj.useAngle(value);
//		return this;
//	}
//	
//	public ObjectBuilder setModel(String fileName, int frames, int directions)
//	{
//		Model model = new Model(fileName, frames, directions, false);
////		model.ANIMATION_MASK = MapObject.DEFAULT_ANIMATION_MASK;
//		obj.setModel(model);
//		if (obj.getName() == null) obj.setName(fileName);
//		return this;
//	}
//	
//	public ObjectBuilder setModel(String fileName)
//	{
//		return setModel(fileName, 0, 0);
//	}
//	
//	public ObjectBuilder setModelOffset(int ox, int oy)
//	{
//		if (obj.getModel() == null) throw new BuilderException("use setModel vor setModelImage.");
//		obj.getModel().setOffset(ox, oy);
//		return this;
//	}
//	
//	public ObjectBuilder setModelImage(int frame, int diretion, int frameTime, boolean standAni)
//	{
//		if (obj.getModel() == null) throw new BuilderException("use setModel vor setModelImage.");
//		obj.getModel().setFrame(frame);
//		obj.getModel().setAnimationIndex(diretion);
//		obj.getModel().setFrameTime(frameTime);
////		obj.getModel().setStandAnimation(standAni);
//		return this;
//	}
//	
//	public ObjectBuilder setAnimation(int index, int frameTime, String eventName)
//	{
//		if (obj.getModel() == null) throw new BuilderException("use setModel vor setModelImage.");
//		obj.getModel().setAnimationIndex(index);
//		obj.getModel().setFrameTime(frameTime);
////		obj.getModel().setCallBack(new EventExecuter.EventCaller(eventName, new Object[] {null, obj, null}));
//		return this;
//	}
//	
//	public ObjectBuilder setStartPoint(int x, int y, int z)
//	{
//		this.obj.setX(x);
//		this.obj.setY(y);
//		this.obj.setZ(z);
//		return this;
//	}
//	
//	public ObjectBuilder setStartPoint(MapObject obj)
//	{
////		this.obj.setRealX(obj.getRealX());
////		this.obj.setRealY(obj.getRealY());
//		this.obj.setZ(obj.getZ());
//		return this;
//	}
//
//	public ObjectBuilder setStartPoint(MapObject obj, int ox, int oy, int oz)
//	{
////		this.obj.setRealX(obj.getRealX() + ox);
////		this.obj.setRealY(obj.getRealY() + oy);
//		this.obj.setZ(obj.getZ() + oz);
//		return this;
//	}
//	
////	public ObjectBuilder setMask(int alphaLevel, boolean through)
////	{
////		int x 		= (int) -obj.getModel().getOX();
////		int y 		= (int) -obj.getModel().getOY();
//////		int width 	= obj.getModel().getWidth();
//////		int height 	= obj.getModel().getHeight();
////		
////		obj.setCollisionMask(new CollisionMask(obj, new Rectangle(x, y, width, height), alphaLevel, through));
////		return this;
////	}
//	
////	public ObjectBuilder setMask(int left, int top, int right, int bottom, int alphaLevel, boolean through)
////	{
////		obj.setCollisionMask(
////			new CollisionMask(obj, new Rectangle(left, top, right - left, bottom - top), alphaLevel, through));
////		return this;
////	}
//	
////	public ObjectBuilder setMotor(int moveType, int moveSpeed, float direction)
////	{
////		obj.setMotor(new Motor_old(obj, moveType, moveSpeed, direction));
////		return this;
////	}
////	
////	public ObjectBuilder setMotor(int moveType, int moveSpeed, MapObject target)
////	{
////		Motor_old motor = new Motor_old(obj, moveType, moveSpeed, 0);
////		if (target != null)
////			motor.setDirection(target.getRealX() - obj.getRealX(), target.getRealY() - obj.getRealY());
////		obj.setMotor(motor);
////		return this;
////	}
////	
//	public ObjectBuilder setName(String name)
//	{
//		this.obj.setName(name);
//		return this;
//	}
//	
//	public ObjectBuilder setFlags(boolean useAngle, boolean directionLock)
//	{
//		this.obj.useAngle(useAngle);
//		this.obj.setDirectionLock(directionLock);
//		return this;
//	}
//	
//	public ObjectBuilder newObject(MapObject obj)
//	{
//		this.obj = new MapObject();
//		return this;
//	}
//	
//	public ObjectBuilder cloneObject(MapObject obj)
//	{
//		this.obj = new MapObject(obj);
//		return this;
//	}
//	
//	/*
//	 * Das Klonen eines Objekt führt zu Differenzen in der ersten Kollisionserkennung.
//	 * Während Das Original Eine Maske ohne konkrete Position haben kann,
//	 * wird bei der Kopie die Position des Originals übernommen, auch wenn anschließend die Position geändert wird.
//	 * Lösung wäre die Kollisionsmaske erst beim Test auf die absolute Position zu schieben.
//	 */
//	public ObjectBuilder cloneObject()
//	{
//		this.obj = new MapObject(obj);
//		return this;
//	}
//	
////	public ObjectBuilder addTrigger(Trigger trigger)
////	{
////		obj.addTrigger(trigger);
////		return this;
////	}
////	
////	public ObjectBuilder addTrigger(TriggerType type, int value, String eventName)
////	{
////		obj.addTrigger(new Trigger(type, value, eventName));
////		return this;
////	}
//	
////	public ObjectBuilder setCharacter(int moveType, int moveSpeed)
////	{
////		obj.setMotor(new Motor(moveType, moveSpeed, direction));
////		return this;
////	}
//	
//	public MapObject build()
//	{
//		CuinaWorld world = Game.getWorld();
//		if (world != null)
//		{
//			world.addObject(obj);
//		}
//		return obj;
//	}
}