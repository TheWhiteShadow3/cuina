package cuina.network;

import cuina.database.NamedItem;

public class Client implements NamedItem
{
	public final int id;
	private String name;

	public Client(int id)
	{
		this.id = id;
	}

	public Client(int id, String name)
	{
		this(id);
		this.name = name;
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
		return 271 * id;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (obj instanceof Client)
		{
			return (id == ((Client) obj).id);
		}
		return false;
	}
}
