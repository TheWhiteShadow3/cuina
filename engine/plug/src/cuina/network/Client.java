package cuina.network;

import cuina.database.NamedItem;
import cuina.network.core.NetID;

public class Client implements NamedItem
{
	private final NetID netID;
	private String name;

	public Client(NetID netID)
	{
		this(netID, null);
		
	}

	public Client(NetID netID, String name)
	{
		this.netID = netID;
		this.name = name;
	}
	
	public NetID getID()
	{
		return netID;
	}

	/**
	 * @return Der Name.
	 */
	@Override
	public String getName()
	{
		return name;
	}

	/**
	 * @param name Der Name.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public int hashCode()
	{
		return 271 * netID.get();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (obj instanceof Client)
		{
			Client other = (Client) obj;
			return (netID.equals(other.netID));
		}
		return false;
	}
}
