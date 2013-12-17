package cuina.network;

import cuina.map.movement.Driver;
import cuina.map.movement.Motor;

public class RemoteDriver implements Driver
{
	private static final long serialVersionUID = 1900868326754776335L;
	private NetworkSession session;

	public RemoteDriver(NetworkSession session)
	{
		this.session = session;
	}
	
	@Override
	public void init(Motor motor)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void update()
	{
		session.update();
	}

	@Override
	public void blocked()
	{
		// TODO Auto-generated method stub

	}
}
