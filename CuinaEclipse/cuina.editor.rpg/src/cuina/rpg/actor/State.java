package cuina.rpg.actor;
 
import cuina.database.DatabaseObject;

import java.util.HashMap;
 

public class State implements DatabaseObject
{
    private static final long   serialVersionUID    = -3900910353967138283L;
    
    private String key;
    private String name;
    /** Ablaufzeit */
    private float time;
    private boolean removeAfterBattle;
    private boolean unresistable;
    public final HashMap<String, Float> attModifier = new HashMap<String, Float>(8);
    public final HashMap<String, Float> effects = new HashMap<String, Float>(8);
    
    public State(String name)
    {
        this(name, -1);
    }
    
    public State(String name, float time)
    {
        this.name = name;
        this.time = Float.MIN_VALUE;
    }
    
    public float getAttModifierValue(String name)
    {
        Float f = attModifier.get(name);
        return f == null ? 1 : f;
    }
 
    public float getTime()
    {
        return time;
    }
 
    public boolean isRemoveAfterBattle()
    {
        return removeAfterBattle;
    }
 
    public boolean isUnresistable()
    {
        return unresistable;
    }
 
    @Override
    public String getName()
    {
        return name;
    }

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
	public void setName(String name)
	{
		this.name = name;
	}
}