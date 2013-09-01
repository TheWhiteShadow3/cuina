package cuina.ks;
 
import cuina.database.Database;
import cuina.rpg.actor.Actor;
import cuina.rpg.actor.ActorGroup;
import cuina.rpg.actor.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
 
public class DefaultBattleKI implements BattleKI
{   
    private static final int LOWER = 0;
    private static final int HIGHER = 1;
//  private static final int EXACT = 2; unused
    
    private Actor target;
    
    public static enum SkillSelection
    {
        RANDOM,
        SELF_LOW_ATTRIBUT,
        SELF_HIGH_ATTRIBUT,
        SELF_WITH_STATE,
        GROUP_LOW_ATTRIBUT,
        GROUP_HIGH_ATTRIBUT,
        GROUP_WITH_STATE,
        ENEMY_LOW_ATTRIBUT,
        ENEMY_HIGH_ATTRIBUT,
        ENEMY_WITH_STATE,
    }
    
    public static enum TargetSelection
    {
        SELF,
        USE_SKILL_SELECTION,
        GROUP_RANDOM,
        GROUP_LOWST_ATTRIBUT,
        GROUP_HIGHST_ATTRIBUT,
        GROUP_WITH_STATE,
        ENEMY_RANDOM,
        ENEMY_LOWST_ATTRIBUT,
        ENEMY_HIGHST_ATTRIBUT,
        ENEMY_WITH_STATE,
    }
    
    public static class Action implements Comparable<Action>
    {
        public String skill;
        public SkillSelection skillSelection;
        public TargetSelection targetSelection;
        public String ssName;
        public long ssValue;
        public String tsName;
        public int priority;
        
        public Action(String skill, SkillSelection skillSelection, TargetSelection targetSelection, String ssName,
                long ssValue, String tsName, int priority)
        {
            this.skill = skill;
            this.skillSelection = skillSelection;
            this.targetSelection = targetSelection;
            this.ssName = ssName;
            this.ssValue = ssValue;
            this.tsName = tsName;
            this.priority = priority;
        }
 
        @Override
        public int compareTo(Action o)
        {
            return o.priority - priority;
        }
    }
    
    @Override
    public BattleAction prepareAction(Battle battle, Enemy enemy)
    {
//      ArrayList<Skill> skills = enemy.getSkills();
        BattleAction battleAction = null;
        target = null;
        
        Action kiAction = findAction(battle, enemy);
        if (kiAction != null)
        {
            battleAction = new BattleAction(enemy, kiAction.skill);
            
            target = findTarget(battle, enemy, kiAction);
            assert target != null;
            battleAction.addTarget(target);
        }
        else
            System.err.println("[KI] Aktionsauswahl fehlgeschlagen!");
        
        return battleAction;
    }
 
    private Action findAction(Battle battle, Enemy enemy)
    {
        ArrayList<Action> actions = ((Setting) enemy.getKiSetting()).actions;
        Collections.sort(actions);
        
        Action action;
        ArrayList<Actor> list = null;
        for(int i = 0; i < actions.size(); i++)
        {
            action = actions.get(i);
            switch(action.skillSelection)
            {
                case RANDOM:
                    if (action.ssValue == 100 || Math.random() * 100 >= action.ssValue) return action;
                break;
                case SELF_LOW_ATTRIBUT:
                {
                    if (enemy.getAttributValue(action.ssName) <= action.ssValue) target = enemy;
                }
                break;  
                case SELF_HIGH_ATTRIBUT:
                {
                    if (enemy.getAttributValue(action.ssName) >= action.ssValue) target = enemy;
                }
                break;
                case SELF_WITH_STATE:
                {
                    boolean value = action.ssValue > 0 ? true : false;
                    if (enemy.containsState(getState(action.ssName)) == value) target = enemy;
                }
                break;
                
                case GROUP_LOW_ATTRIBUT:
                    list = getAttributThresholdMembers(
                            battle.getEnemies(), action.ssName, action.ssValue, LOWER);
                    break;
                    
                case GROUP_HIGH_ATTRIBUT:
                    list = getAttributThresholdMembers(
                            battle.getEnemies(), action.ssName, action.ssValue, HIGHER);
                    break;
                    
                case GROUP_WITH_STATE:
                    list = getInfectedMembers(
                            battle.getEnemies(), action.ssName, action.ssValue > 0 ? true : false);
                    break;
                    
                case ENEMY_LOW_ATTRIBUT:
                    list = getAttributThresholdMembers(
                            battle.getParty(), action.ssName, action.ssValue, LOWER);
                    break;
                    
                case ENEMY_HIGH_ATTRIBUT:
                    list = getAttributThresholdMembers(
                            battle.getParty(), action.ssName, action.ssValue, HIGHER);
                    break;
                    
                case ENEMY_WITH_STATE:
                    list = getInfectedMembers(
                            battle.getParty(), action.ssName, action.ssValue > 0 ? true : false);
                    break;
            }
            
            if (list != null)
            {
                target = pickRandom(list);
            }
            if (target != null) return action;
        }
        System.out.println("Keine Aktion gefunden!");
        return null;
    }
    
    private Actor findTarget(Battle battle, Enemy enemy, Action action)
    {
        switch(action.targetSelection)
        {
            case SELF:
                return enemy;
                
            case USE_SKILL_SELECTION:
                return target;
                
            case GROUP_HIGHST_ATTRIBUT:
                return getExtremAttributOwner(battle, battle.getEnemies(), action.tsName, HIGHER);
                
            case GROUP_LOWST_ATTRIBUT:
                return getExtremAttributOwner(battle, battle.getEnemies(), action.tsName, LOWER);
                
            case GROUP_RANDOM:
                return pickRandom(battle.getEnemies());
                
            case GROUP_WITH_STATE:
                return pickRandom(getInfectedMembers(
                        battle.getEnemies(), action.tsName, action.ssValue > 0 ? true : false));
                
            case ENEMY_HIGHST_ATTRIBUT:
                return getExtremAttributOwner(battle, battle.getParty(), action.tsName, HIGHER);
                
            case ENEMY_LOWST_ATTRIBUT:
                return getExtremAttributOwner(battle, battle.getParty(), action.tsName, LOWER);
                
            case ENEMY_RANDOM:
                return pickRandom(battle.getParty());
                
            case ENEMY_WITH_STATE:
                return pickRandom(getInfectedMembers(
                        battle.getParty(), action.tsName, action.ssValue > 0 ? true : false));
        }
        return null;
    }
    
    private State getState(String name)
    {
        return Database.<State>get(name, "States");
    }
    
    private Actor pickRandom(List<Actor> list)
    {
        return list.get( (int) (Math.random() * list.size()) );
    }
    
    private Actor pickRandom(ActorGroup group)
    {
        return group.getActor( (int) (Math.random() * group.size()) );
    }
    
    private ArrayList<Actor> getAttributThresholdMembers(ActorGroup group, String stateName, long threshold, int side)
    {
        ArrayList<Actor> list = new ArrayList<Actor>(group.size());
        for (Actor actor : group)
        {
            if (side == HIGHER)
                if (actor.getAttributValue(stateName) >= threshold) list.add(actor);
            else if (side == LOWER)
                if (actor.getAttributValue(stateName) <= threshold) list.add(actor);
        }
        return list;
    }
    
    private Actor getExtremAttributOwner(Battle battle, ActorGroup group, String stateName, int side)
    {
        Actor maxActor = null;
        long maxValue = 0;
        for (Actor actor : group)
        {
            if (!battle.getStrategy().isSelectable(actor, null)) continue;
            long testValue = actor.getAttributValue(stateName);
            
            if (maxActor == null || 
               (side == HIGHER && testValue > maxValue) ||
               (side == LOWER  && testValue < maxValue))
            {
                maxActor = actor;
                maxValue = testValue;
            }
        }
        return maxActor;
    }
    
    private ArrayList<Actor> getInfectedMembers(ActorGroup group, String stateName, boolean include)
    {
        ArrayList<Actor> list = new ArrayList<Actor>(group.size());
        for (Actor actor : group)
        {
            if (actor.containsState(getState(stateName)) == include) list.add(actor);
        }
        return list;
    }
    
    public static class Setting implements KiSetting
    {
        public final ArrayList<Action> actions = new ArrayList<Action>();
    }
}