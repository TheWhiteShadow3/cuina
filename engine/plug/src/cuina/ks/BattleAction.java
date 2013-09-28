package cuina.ks;
 
import cuina.database.Database;
import cuina.database.NamedItem;
import cuina.rpg.Skill;
import cuina.rpg.actor.Actor;

import java.util.ArrayList;
 
public class BattleAction implements NamedItem
{
    private String      name;
    private Actor       user;
    private Skill       skill;
    private float       time;
    public final ArrayList<Actor> targets = new ArrayList<Actor>();
    
    public BattleAction(Actor user, String skillKey)
    {
        this.user = user;
        this.skill = Database.get("Skill", skillKey);
        this.name = skill.getName();
        this.time = skill.time;
    }
    
    public BattleAction(Actor user, String name, float time)
    {
        this.name = name;
        this.user = user;
        this.time = time;
    }
 
    @Override
	public String getName()
    {
        return name;
    }
 
    public Actor getUser()
    {
        return user;
    }
 
    void setUser(Actor user)
    {
        this.user = user;
    }
 
    public Skill getSkill()
    {
        return skill;
    }
 
    public void addTarget(Actor actor)
    {
        if (actor == null) return;
        targets.add(actor);
    }
    
    public void removeTarget(Actor actor)
    {
        targets.remove(actor);
    }
    
    public ArrayList<Actor> getTargets()
    {
        return targets;
    }
    
    public Actor getTarget(int index)
    {
        return targets.get(index);
    }
 
    public float getExecutionTime()
    {
        return time;
    }
}