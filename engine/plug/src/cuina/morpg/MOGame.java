package cuina.morpg;

import cuina.map.GameMap;
import cuina.network.NetworkExtension;
import cuina.network.ServerEvent;
import cuina.network.ServerListener;
import cuina.object.BaseObject;
import cuina.world.CuinaObject;

public class MOGame
{
	public void start(String mapName)
	{
		GameMap map = GameMap.getInstance();
		map.load(mapName);
		for(int id : map.getObjectIDs())
		{
			CuinaObject obj = map.getObject(id);
			if (obj instanceof BaseObject)
			{
				BaseObject bObj = (BaseObject) obj;
				bObj.addExtension("network", new NetworkExtension(id, session, object));
			}
		}
	}
	
	class ServerHandler implements ServerListener
	{
		@Override
		public void clientConnected(ServerEvent event)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void clientDisconnected(ServerEvent event)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sessionCreated(ServerEvent event)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sessionDestroyed(ServerEvent event)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sessionJoined(ServerEvent event)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sessionLeaved(ServerEvent event)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void roomCreated(ServerEvent event)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void roomDestroyed(ServerEvent event)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void roomJoined(ServerEvent event)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void roomLeaved(ServerEvent event)
		{
			// TODO Auto-generated method stub
			
		}
	}
}
