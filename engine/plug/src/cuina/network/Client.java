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
}
