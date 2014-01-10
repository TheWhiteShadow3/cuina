package cuina.rpg.inventory;

import cuina.rpg.actor.Equippable;

import java.util.HashMap;
import java.util.Map;

public class Equipment extends Item implements Equippable
{
	private static final long serialVersionUID = 5863289440992309662L;
	
	public final Map<String, Integer> attributs = new HashMap<String, Integer>();
	
	@Override
	public long getAttValue(String name)
	{
		return attributs.get(name);
	}
}
