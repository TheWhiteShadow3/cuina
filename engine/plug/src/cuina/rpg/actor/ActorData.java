package cuina.rpg.actor;

import cuina.database.DatabaseObject;
import cuina.rpg.Skill;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ActorData implements Serializable, DatabaseObject
{
	private static final long serialVersionUID = 5058399167059715639L;
	public String name = "";
	
	public String key;
	public String spriteName;
	public int initialLevel;
	public int maximumLevel;
	
	public HashMap<String, Attribut> attributes = new HashMap<String, Attribut>();
	
	public ArrayList<State> states = new ArrayList<State>();
	public ArrayList<Equippable> equipments = new ArrayList<Equippable>(8);
	public ArrayList<Skill> skills = new ArrayList<Skill>();
	public HashMap<String, Object> extensions;

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
