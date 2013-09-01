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
	
	private String key;
	private String spriteName;
	private int initialLevel;
	private int maximumLevel;
	
	private HashMap<String, Attribut> attributes = new HashMap<String, Attribut>();
	
	private ArrayList<State> states = new ArrayList<State>();
	private ArrayList<Equippable> equipments = new ArrayList<Equippable>(8);
	private ArrayList<Skill> skills = new ArrayList<Skill>();

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

	public String getSpriteName()
	{
		return spriteName;
	}

	public int getInitialLevel()
	{
		return initialLevel;
	}

	public void setInitialLevel(int level)
	{
		this.initialLevel = level;
	}

	public void setSpriteName(String spriteName)
	{
		this.spriteName = spriteName;
	}

	public int getMaximumLevel()
	{
		return maximumLevel;
	}

	public void setMaximumLevel(int maximumLevel)
	{
		this.maximumLevel = maximumLevel;
	}

	public HashMap<String, Attribut> getAttributes()
	{
		return attributes;
	}
	
	public void setAttributes(HashMap<String, Attribut> attributes)
	{
		this.attributes = attributes;
	}

	public ArrayList<State> getStates()
	{
		return states;
	}

	public ArrayList<Equippable> getEquipments()
	{
		return equipments;
	}

	public ArrayList<Skill> getSkills()
	{
		return skills;
	}
}
