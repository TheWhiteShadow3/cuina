package cuina.rpg.actor;

import cuina.database.NamedItem;
import cuina.rpg.Attributable;
import cuina.rpg.Skill;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class Actor implements Attributable, Serializable, NamedItem
{
	private static final long serialVersionUID = -4707910212716248964L;
	
	public static final float DEFAULT_ELEMENT_RESISTANCE = 1.0f;
	public static final float DEFAULT_STATE_RESISTANCE = 0.2f;

	private String key;
	private String name;
	private int level = 1;
	private int maxLevel = 99;
	private long xp;
	/** Liste der Attribute, wie HP, MP, ... */
	private final HashMap<String, Attribut> atts;
	/** Liste der Zustände, welche der Actor besitzt. */
	private final ArrayList<ActorState> states = new ArrayList<ActorState>();
	/** Wiederstandswerte gegen Elemente. */
	private final HashMap<String, Float> elementResists = new HashMap<String, Float>();
	private final HashMap<String, Float> stateResists = new HashMap<String, Float>();
	
	private Equippable[] equipments;
	private final ArrayList<Skill> skills;

	public Actor()
	{
		equipments = new Equippable[8];
		atts = new HashMap<String, Attribut>();
		skills = new ArrayList<Skill>();
	}

	public Actor(ActorData data)
	{
		this.key = data.getKey();
		this.name = data.getName();
		this.level = data.getInitialLevel();
		this.maxLevel = data.getMaximumLevel();
		this.atts = data.getAttributes();
		// this.equipments = data.getEquipments();
		this.skills = data.getSkills();
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}

	public int getMaxLevel()
	{
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel)
	{
		this.maxLevel = maxLevel;
		if (level > maxLevel)
		{
			level = maxLevel;
		}
	}

	public long getXp()
	{
		return xp;
	}

	public void setXp(long xp)
	{
		this.xp = xp;
	}

	public void gainXP(long value)
	{
		this.xp += value;
	}

	public void addState(State state)
	{
		states.add(new ActorState(state));
		updateAttributes();
	}

	public boolean containsState(State state)
	{
		for (ActorState s : getStates())
		{
			if (s.state.getName().equals(state.getName())) return true;
		}
		return false;
	}

	public float getElementResistance(String element)
	{
		Float f = elementResists.get(element);
		return f != null ? f : DEFAULT_ELEMENT_RESISTANCE;
	}

	public void setElementResistance(String name, float value)
	{
		elementResists.put(name, value);
	}
	
	public float getStateResistance(String element)
	{
		Float f = stateResists.get(element);
		return f != null ? f : DEFAULT_STATE_RESISTANCE;
	}

	public void setStateResistance(String name, float value)
	{
		stateResists.put(name, value);
	}

	public ArrayList<ActorState> getStates()
	{
		return states;
	}

	public void removeState(State state)
	{
		if (state == null) return;

		for (int index = 0; index < states.size(); index++)
			if (state.equals(states.get(index).state))
			{
				states.remove(index);
				return;
			}
	}

	public void addAttribut(Attribut att)
	{
		if (att.isCalculable())
		{
			att.calc(level);
			att.fill();
		}
		atts.put(att.getName(), att);
	}

	@Override
	public Attribut getAttribut(String name)
	{
		if (atts.containsKey(name)) return atts.get(name);
		return null;
	}

	private void updateAttributes()
	{
		for (String key : atts.keySet())
		{
			float add = 0;
			for (Equippable e : equipments)
			{
				if (e == null) continue;
				add += e.getAttValue(key);
			}

			float fac = 1;
			for (ActorState state : states)
			{
				fac *= state.state.getAttModifierValue(key);
			}
			atts.get(key).setSecondaryCalculation(add, fac, Attribut.MODIFY_NONE);
		}
	}

	public long getAttributValue(String name)
	{
		Attribut att = getAttribut(name);
		return (att != null) ? att.getValue() : 0;
	}

	public Set<String> getAttributNames()
	{
		return atts.keySet();
	}

	public void addSkill(Skill skill)
	{
		for (Skill s : skills)
		{
			if (s.getName() == skill.getName()) return;
		}
		skills.add(skill);
	}

	public Skill removeSkill(String name)
	{
		for (int i = 0; i < skills.size(); i++)
		{
			if (skills.get(i).getName() == name) { return skills.remove(i); }
		}
		return null;
	}

	public ArrayList<Skill> getSkills()
	{
		return skills;
	}

	public void setEquipment(int index, Equippable e)
	{
		equipments[index] = e;
		updateAttributes();
	}

	public Equippable getEquipment(int index)
	{
		return equipments[index];
	}

	@Override
	public String toString()
	{
		return "Actor(" + key + "): " + name + " lv " + level + "[" + atts + "]";
	}

	public class ActorState
	{
		public float time;
		public final State state;

		public ActorState(State state)
		{
			this.state = state;
			this.time = state.getTime();
		}
	}
}
