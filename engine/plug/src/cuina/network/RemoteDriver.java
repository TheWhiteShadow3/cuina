package cuina.network;

import cuina.map.movement.Driver;
import cuina.map.movement.Motor;
import cuina.world.CuinaObject;

import java.nio.ByteBuffer;

public class RemoteDriver implements Driver, Control
{
	private static final long serialVersionUID = 1900868326754776335L;
	private NetworkSession session;
	private Motor motor;
	private float x;
	private float y;
	private float speed;
	private float dir;
	private boolean update;
	
	public RemoteDriver(NetworkSession session)
	{
		this.session = session;
		session.addControl(this);
	}
	
	@Override
	public void init(Motor motor)
	{
		this.motor = motor;
	}

	@Override
	public void update()
	{
		if (motor == null || !update) return;
		
		CuinaObject obj = motor.getObject();
		obj.setX(x);
		obj.setY(y);
		motor.setSpeed(speed);
		motor.setDirection(dir);
		update = false;
	}

	@Override
	public void blocked()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void recieveData(byte[] data)
	{
		ByteBuffer buffer = ByteBuffer.wrap(data);
		this.x = buffer.getFloat();
		this.y = buffer.getFloat();
		this.speed = buffer.getFloat();
		this.dir = buffer.getFloat();
		this.update = true;
	}
}
