package cuina.ks;
 
import static java.lang.Math.random;

import cuina.database.Database;
import cuina.ks.Battle.ActionResult;
import cuina.ks.Battle.AnimationType;
import cuina.rpg.Skill;
import cuina.rpg.actor.Actor;
import cuina.rpg.actor.Attribut;
import cuina.rpg.actor.State;
import cuina.rpg.actor.Actor.ActorState;

import java.util.HashMap;
 
public class DefaultBattleStrategy implements BattleStrategy
{
    // benutzte Attribut-Konstanten für diese Battlestrategy:
    public static final String ATT_HP   = "HP";
    public static final String ATT_MP   = "MP";
    public static final String ATT_SPD  = "SPD";
    public static final String ATT_STR  = "STR";
    public static final String ATT_INT  = "INT";
    public static final String ATT_DEF  = "DEF";
    public static final String ATT_ATK  = "ATK";
    public static final String ATT_RES	= "RES"; //XXX: Geändert von MDEF nach RES (Analogie zu disgaea)
    
    // benutzte Zustands-Effekte für diese Battlestrategy:
    public static final String EFF_POISON       = "poison";
    public static final String EFF_SILENCE      = "silence";
    public static final String EFF_BLIND        = "blind";
    public static final String EFF_HEAL         = "heal";
    public static final String EFF_TIME         = "time";
    public static final String EFF_STOP         = "stop";
    public static final String EFF_DEAD         = "dead";
 
    /** Gibt an um welchen Faktor die Stärke beim Schaden veringert werden. */
    public int   ATTRIBUT_REDUCTION = 10;
    
    /** Konstante für einen Tod-Zustand. */
    public static final State STATE_DEATH;
    
    static
    {
        STATE_DEATH = new State("dead");
        STATE_DEATH.effects.put(EFF_DEAD, 1f);
    }
 
    @Override
    public boolean canUseAction(BattleAction action)
    {
        Actor actor = action.getUser();
        Skill skill = action.getSkill();
        if ((skill.flags & Skill.FLAG_MAGIC) != 0 && isStateEffected(actor, EFF_SILENCE)) return false;
        
        for(String key : skill.costs.keySet())
        {
            if (actor.getAttributValue(key) < skill.costs.get(key)) return false;
        }
        return true;
    }
 
    @Override
    public void paySkillCost(BattleAction action)
    {
        Actor actor = action.getUser();
        for(String key : action.getSkill().costs.keySet())
        {
            Attribut att = actor.getAttribut(key);
            if (att != null)
                att.sub(action.getSkill().costs.get(key));
        }
    }
 
    @Override
    public void executeAction(BattleAction action, ActionResult[] results)
    {
        for(int i = 0; i < action.getTargets().size(); i++)
        {
            executeAction(action, action.getTarget(i), results[i]);
        }
    }
    
    public void executeAction(BattleAction action, Actor target, ActionResult result)
    {
        long damage = calcBaseDamage(action.getUser(), action.getSkill(), target);
        if (damage < 0) result.flags |= HEAL;
        
        if (damage < 0 || isHit(action, target))
        {
            // faktoren
            damage *= getElementFactor(action.getSkill(), target);
            damage *= damageInfluenceFactor(action, target, result);
            
            target.getAttribut(ATT_HP).sub(damage);
            if (!isAlive(target))
                result.flags |= DEATH;
            else
                if (getStateEffectValueSum(target, EFF_DEAD) > 0)
                    result.flags |= REANIMATE;
            
            changeStates(action, target, result);
        
            result.damage = damage;
            System.out.println(action.getName() + " auf " + target.getName() + ": " + damage);
            if (result.flags != 0) System.out.println("Flags: " + result.flags);
        }
        else
        {
            result.flags |= MISS;
            result.message = "verfehlt";
            System.out.println(action.getName() + " auf " + target.getName() + ": " + result.message);
        }
    }
    
    protected float damageInfluenceFactor(BattleAction action, Actor target, ActionResult result)
    {
        float fac = 1;
        // Kritisch
        if (0.1 > random())
        {
            fac *= 2;
            result.flags |= CRIT;
        }
        // Block
        if (0.1 > random())
        {
            fac /= 2;
            result.flags |= BLOCK;
        }
        // Streuung
        fac *= 1 + (random() - random()) * 0.15;
        
        return fac;
    }
 
    protected boolean isHit(BattleAction action, Actor target)
    {
    	float userSpeed;
    	if (action.getSkill() != null)
    		userSpeed = action.getUser().getAttributValue(ATT_SPD) * action.getSkill().hit;
    	else
    		userSpeed = action.getUser().getAttributValue(ATT_SPD);
        
        return random() * userSpeed * 5 > target.getAttributValue(ATT_SPD);
    }
    
    protected void changeStates(BattleAction action, Actor target, ActionResult result)
    {
    	if (action.getSkill() == null) return;
    	
        for(String key : action.getSkill().states.keySet())
        {
            float resist = target.getStateResistance(key);
            
            State state = Database.get("States", key);
            if (resist >= 1.0)
            {
                result.flags |= IMUN;
                result.message = "imun";
            }
            else if ((result.flags & HEAL) != 0 || state.isUnresistable() || resist < random())
            {
                target.addState(state);
                System.out.println(target.getName() + " ist betroffen von " + state.getName());
            }
        }
        
        if (isAlive(target))
            target.removeState(STATE_DEATH);
        else
            target.addState(STATE_DEATH);
    }
    
	protected long calcBaseDamage(Actor user, Skill skill, Actor target)
	{
		long atk, str, intl, def, res;
		
		if (skill != null)
		{
			atk  = getUserAttribut(user, skill, ATT_ATK);
			if (atk == 0) return 0;
	
			str  = getUserAttribut(user, skill, ATT_STR);
			intl = getUserAttribut(user, skill, ATT_INT);
			def  = getUserAttribut(target, skill, ATT_DEF);
			res  = getUserAttribut(target, skill, ATT_RES);
		}
		else
		{
			atk = user.getAttributValue(ATT_ATK);
			if (atk == 0) return 0;
			
			str = user.getAttributValue(ATT_STR);
			intl = def = res = 0;
		}
	
		long power = atk * (ATTRIBUT_REDUCTION + str + intl) / ATTRIBUT_REDUCTION;
		long damage;

		if (atk > 0)
		{
			long defens = (def + res) / 2;
			damage = Math.max(power - defens, 0);
		}
		else damage = Math.min(power, 0);
		return damage;
	}
    
    protected long getUserAttribut(Actor user, Skill skill, String name)
    {
        Float f = skill.attModifier.get(name);
        return f != null ? (long)(user.getAttributValue(name) * f.floatValue()) : 0L;
    }
    
    protected float getElementFactor(Skill skill, Actor target)
    {
    	if (skill == null) return 1f;
    	
        float result = 1;
        float minResist = Float.MAX_VALUE;
        
        HashMap<String, Boolean> elements = skill.elements;
        for(String key : elements.keySet())
        {
            if (elements.get(key) != null)
            {
                float f = target.getElementResistance(key);
                if (f < minResist)
                    minResist = f;
            }
        }
        
        return result;
    }
 
    @Override
    public void roundStart(Battle battle)
    {
    }
 
    @Override
    public void roundEnd(Battle battle)
    {
        Actor actor = battle.getCurrentBattler().getActor();
        if (!isAlive(actor)) return;
 
        float value = getStateEffectValueSum(actor, EFF_POISON);
        if (value == 0) return;
            
        ActionResult result = new ActionResult();
        result.message = "Gift";
        result.damage = actor.getAttribut(ATT_HP).subRel(value);
        battle.getCurrentBattler().performAction(AnimationType.HIT, null, result);
        System.out.println("Gilfteffekt bei " + actor.getName() + ": " + result.damage + " Schaden");
    }
    
    protected boolean isStateEffected(Actor actor, String effectName)
    {
        return getStateEffectValueSum(actor, effectName) != 0;
    }
    
    protected float getStateEffectValueSum(Actor actor, String effectName)
    {
        float result = 0;
        for(ActorState state : actor.getStates())
        {
            Float value = state.state.effects.get(effectName);
            if (value != null) result += value;
        }
        return result;
    }
    
    protected float getStateEffectValueProduct(Actor actor, String effectName)
    {
        float result = 1;
        for(ActorState state : actor.getStates())
        {
            Float value = state.state.effects.get(effectName);
            if (value != null) result *= value;
        }
        return result;
    }
 
    @Override
    public void battleStart(Battle battle)
    {
        System.out.println("Kampf beginnt");
    }
 
    @Override
    public void battleEnd(Battle battle)
    {
        System.out.println("Kampf beendet");
    }
 
    @Override
    public boolean isAlive(Actor actor)
    {
        return actor.getAttributValue(ATT_HP) > 0;
    }
    
    @Override
    public boolean isSelectable(Actor actor, BattleAction action)
    {
        return isAlive(actor) || (action != null && (action.getSkill().scope & Skill.SCOPE_DEAD) != 0);
    }
 
    @Override
    public float getActionTime(Actor user, BattleAction action)
    {
        float baseTime = (action != null) ?  action.getExecutionTime() : 1;
        
        baseTime *= getStateEffectValueProduct(user, EFF_TIME);
        return baseTime * 100f / user.getAttributValue(ATT_SPD);
    }
 
    @Override
    public boolean isAvailable(Actor actor)
    {
        return isAlive(actor) && !isStateEffected(actor, EFF_STOP);
    }
}