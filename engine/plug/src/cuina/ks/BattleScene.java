package cuina.ks;

import cuina.database.DatabaseObject;
import cuina.event.Trigger;

import java.util.ArrayList;

/**
 * Stellt eine Kampf-Szene da.
 * @author TheWhiteShadow
 */
public class BattleScene implements DatabaseObject
{
	private static final long serialVersionUID = 4766338256687029327L;
	
	public String key;
	public String name;
	public String battlegroundKey;
	
	public EnemyTroop troop;
	public final ArrayList<Trigger> triggers = new ArrayList<Trigger>(4);
	
	@Override
	public void setKey(String key)
	{
		this.key = key;
	}

	@Override
	public String getKey()
	{
		return key;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public void setName(String name)
	{
		this.name = name;
	}
}
