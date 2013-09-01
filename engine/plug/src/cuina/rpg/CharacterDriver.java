package cuina.rpg;

import cuina.Game;
import cuina.map.movement.Driver;
import cuina.map.movement.MovementUtil;
import cuina.map.movement.Motor;
import cuina.rpg.MoveRoute.MoveCommand;
import cuina.world.CuinaObject;

public class CharacterDriver implements Driver
{
    private static final long serialVersionUID = 5138884954795651119L;
 
    public static final int MOVE_NONE       = 0;
    public static final int MOVE_FORWARD    = 1;
    public static final int MOVE_RANDOM     = 2;
    public static final int MOVE_TO_TARGET  = 3;
    public static final int MOVE_ROUTE      = 4;
    
    private Motor motor;
    private float moveSpeed;
    private int moveType = MOVE_NONE;
    private int stopTime;
    private float moveDistance;
    private MoveRoute moveRoute; //TODO: Klasse muss erstellt werden.
    private int moveRouteIndex;
    private int targetID;
    
    @Override
    public void init(Motor motor)
    {
        this.motor = motor;
    }
    
    public float getMoveSpeed()
    {
        return moveSpeed;
    }
 
	public void setMoveSpeed(float moveSpeed)
	{
		this.moveSpeed = moveSpeed;
	}

	public int getMoveType()
	{
		return moveType;
	}

	public void setMoveType(int moveType)
	{
		this.moveType = moveType;
	}

	public MoveRoute getMoveRoute()
	{
		return moveRoute;
	}

	public void setMoveRoute(MoveRoute moveRoute, boolean start)
	{
		this.moveRoute = moveRoute;
		this.moveRouteIndex = 0;
		if (start) this.moveType = MOVE_ROUTE;
	}

	@Override
    public void update()
    {
        if (stopTime > 0)
        {
            motor.setSpeed(0);
            stopTime--;
            return;
        }
        
        if (moveDistance > 0)
        {
            motor.setSpeed(Math.min(moveSpeed, moveDistance));
            moveDistance -= moveSpeed;
            return;
        }
        
        switch (moveType)
        {
            case MOVE_FORWARD:      moveForward(); break;
            case MOVE_RANDOM:       moveRandom(); break;
            case MOVE_TO_TARGET:    moveToTarget(targetID); break;
            case MOVE_ROUTE:        moveRoute(moveRoute); break;
        }
    }
    
    @Override
    public void blocked()
    {
        stopTime = 0;
    }
    
    private void moveForward()
    {
        motor.setSpeed(moveSpeed);
    }
    
    private void moveRandom()
    {
        int rnd = (int)(Math.random() * 100);
        if (rnd > 90)
        {
        	moveDistance = (int) (moveSpeed * Math.random() * 60);
            motor.setDirection( ((int)(Math.random() * 360) / 90) * 90);
            motor.setSpeed(moveSpeed);
        }
        else if (rnd > 85)
        {
            stopTime = 10;
            return;
        }
    }
 
    private void moveToTarget(int targetID)
    {
    	CuinaObject other = Game.getWorld().getObject(targetID);
    	motor.setDirection(MovementUtil.getDirection(motor.getObject(), other));
    	motor.setSpeed(moveSpeed);
    }
    
    private void moveRoute(MoveRoute moveRoute)
    {
    	if (moveRoute == null) return;
    	
		if (moveRouteIndex >= moveRoute.commands.length)
		{
			if (moveRoute.repeat)
			{
				moveRouteIndex = 0;
			}
			else
			{
				moveType = MOVE_NONE;
				return;
			}
		}
		MoveCommand cmd = moveRoute.commands[moveRouteIndex++];
		if (cmd == null) return;
		
		switch (cmd.type)
		{
			case MoveRoute.MOVE:
				moveDistance = (int) cmd.value; break;
				
			case MoveRoute.SET_DIRECTION:
				motor.setDirection(cmd.value); break;
				
			case MoveRoute.SET_SPEED:
				motor.setSpeed(cmd.value); break;
				
			case MoveRoute.WAIT:
				stopTime = (int) cmd.value; break;
				
			case MoveRoute.NONE:
				motor.setSpeed(0); break;
				
			default:
				assert false : "unexpected MoveCommand";
		}
		update();
	}
}