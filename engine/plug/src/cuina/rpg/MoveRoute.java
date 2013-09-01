package cuina.rpg;

public class MoveRoute
{
	public static final int NONE 			= 0;
	public static final int MOVE 			= 1;
	public static final int SET_DIRECTION 	= 2;
	public static final int SET_SPEED 		= 3;
	public static final int WAIT 			= 4;
	
	public MoveCommand[] commands;
	public boolean repeat;

	public static class MoveCommand
	{	
		public int type;
		public float value;
		
		public MoveCommand(int type, float value)
		{
			this.type = type;
			this.value = value;
		}
	}
}
