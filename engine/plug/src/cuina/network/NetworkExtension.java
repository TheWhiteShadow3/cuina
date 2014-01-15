package cuina.network;

import cuina.animation.Model;
import cuina.animation.ModelImpl;
import cuina.movement.Motor;
import cuina.network.core.NetID;
import cuina.object.BaseObject;
import cuina.plugin.LifeCycleAdapter;

import java.io.IOException;
import java.nio.ByteBuffer;

public class NetworkExtension extends LifeCycleAdapter
{
	private NetID netID;
	private INetworkSession session;
	private BaseObject object;
	
	public NetworkExtension(INetworkSession session, BaseObject object) throws IOException
	{
		this.session = session;
		this.object = object;
		session.requestNetworkID(netID);
	}

	public NetID getID()
	{
		return netID;
	}
	
	public INetworkSession getSession()
	{
		return session;
	}

	public BaseObject getObject()
	{
		return object;
	}

	@Override
	public void init()
	{
		if (!netID.isSet() || !session.isOpen()) return;
		
		ByteBuffer buffer = prepareBuffer();
		fillCreateData(buffer);
		try
		{
			session.sendEvent(buffer.array());
		}
		catch (IOException e)
		{
			handleException(e);
		}
	}

	@Override
	public void postUpdate()
	{
		if (!netID.isSet() || !session.isOpen()) return;
		
		ByteBuffer buffer = prepareBuffer();
		fillUpdateData(buffer);
		try
		{
			session.sendData(buffer.array());
		}
		catch (IOException e)
		{
			handleException(e);
		}
	}

	@Override
	public void dispose()
	{
		if (!netID.isSet() || !session.isOpen()) return;
		
		ByteBuffer buffer = prepareBuffer();
		fillDisposeData(buffer);
		try
		{
			session.sendEvent(buffer.array());
		}
		catch (IOException e)
		{
			handleException(e);
		}
	}
	
	private void handleException(IOException e)
	{
		e.printStackTrace();
	}
	
	private ByteBuffer prepareBuffer()
	{
		ByteBuffer buffer = ByteBuffer.allocate(256);
		buffer.putInt(session.getID().get());
		buffer.putInt(netID.get());
		return buffer;
	}
	
	protected void fillCreateData(ByteBuffer buffer)
	{
		buffer.putInt(INetworkSession.MESSAGE_OBJECT_CREATE);
		buffer.putFloat(object.getX());
		buffer.putFloat(object.getY());
		buffer.putFloat(object.getZ());
		Motor motor = (Motor) object.getExtension(Motor.EXTENSION_KEY);
		if (motor != null)
		{
			buffer.put((byte) 1);
			buffer.putFloat(motor.getDirection());
			buffer.putFloat(motor.getSpeed());
		}
		Model model = (Model) object.getExtension(Model.EXTENSION_KEY);
		if (model != null && model instanceof ModelImpl)
		{
			ModelImpl modelImpl = (ModelImpl) model;
			buffer.put((byte) 2);
			StreamUtils.writeString(buffer, modelImpl.getFileName());
			buffer.putInt(modelImpl.getFrameCount());
			buffer.putInt(modelImpl.getAnimationCount());
			buffer.put((byte) (modelImpl.isAnimate() ? 1 : 0));
			buffer.putInt(modelImpl.getFrame());
			buffer.putInt(modelImpl.getAnimationIndex());
			if (modelImpl.getAnimator() != null)
			{
				StreamUtils.writeString(buffer, modelImpl.getAnimator().getClass().getName());
			}
			else
			{
				buffer.put((byte) -1);
			}
		}
	}
	
	protected void fillUpdateData(ByteBuffer buffer)
	{
		buffer.putInt(INetworkSession.MESSAGE_OBJECT_UPDATE);
		buffer.putFloat(object.getX());
		buffer.putFloat(object.getY());
		buffer.putFloat(object.getZ());
		Motor motor = (Motor) object.getExtension(Motor.EXTENSION_KEY);
		if (motor != null)
		{
			buffer.put((byte) 1);
			buffer.putFloat(motor.getDirection());
			buffer.putFloat(motor.getSpeed());
		}
	}
	
	
	protected void fillDisposeData(ByteBuffer buffer)
	{
		buffer.putInt(INetworkSession.MESSAGE_OBJECT_DISPOSE);
	}
}
