package cuina.network;

import cuina.animation.Animator;
import cuina.animation.Model;
import cuina.animation.ModelImpl;
import cuina.movement.Driver;
import cuina.movement.Motor;
import cuina.network.core.NetID;
import cuina.world.CuinaObject;

import java.nio.ByteBuffer;

public class RemoteDriver implements Driver, Control
{
	private static final long serialVersionUID = 1900868326754776335L;
	private INetworkSession session;
	private NetID netID;
	private CuinaObject object;
	private Motor motor;
	private float x;
	private float y;
	private float z;
	private float speed;
	private float dir;
	private boolean update;
	
	public RemoteDriver(INetworkSession session, NetID netID)
	{
		this.session = session;
		this.netID = netID;
	}
	
	@Override
	public NetID getID()
	{
		return netID;
	}

	public INetworkSession getSession()
	{
		return session;
	}

	@Override
	public void init(Motor motor)
	{
		this.motor = motor;
		update();
	}

	@Override
	public void update()
	{
		if (motor == null || !update) return;
		
		CuinaObject obj = motor.getObject();
		obj.setX(x);
		obj.setY(y);
		obj.setZ(z);
		motor.setSpeed(speed);
		motor.setDirection(dir);
		update = false;
	}

	@Override
	public void blocked() {}

	@Override
	public void recieveData(ByteBuffer data)
	{
		int messageType = data.getInt();
		switch(messageType)
		{
			case INetworkSession.MESSAGE_OBJECT_CREATE: readCreateData(data); break;
			case INetworkSession.MESSAGE_OBJECT_UPDATE: readUpdateData(data); break;
			case INetworkSession.MESSAGE_OBJECT_DISPOSE: readDisposeData(data); break;
		}
		update = true;
	}

	private void readCreateData(ByteBuffer data)
	{
		this.x = data.getFloat();
		this.x = data.getFloat();
		this.x = data.getFloat();
		
		if (data.get() == 1)
		{
			this.dir = data.getFloat();
			this.speed = data.getFloat();
		}
		if (data.get() == 2)
		{
			byte[] bytes = new byte[data.get()];
			data.get(bytes);
			Model model = new ModelImpl(new String(bytes), data.getInt(), data.getInt(), data.get() == 1);
			model.setFrame(data.getInt());
			model.setAnimationIndex(data.getInt());
			String animatorClass = StreamUtils.readString(data);
			if (animatorClass != null) try
			{
				Animator animator = (Animator) Class.forName(animatorClass).newInstance();
				model.setAnimator(animator);
			}
			catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			
			motor.getObject().addExtension(Model.EXTENSION_KEY, model);
		}
	}

	private void readUpdateData(ByteBuffer data)
	{
		this.x = data.getFloat();
		this.x = data.getFloat();
		this.x = data.getFloat();
		
		if (data.get() == 1)
		{
			this.dir = data.getFloat();
			this.speed = data.getFloat();
		}
	}

	private void readDisposeData(ByteBuffer data)
	{
		
	}
}
