package cuina.ks;
 
import static java.lang.Math.random;

import cuina.Context;
import cuina.Game;
import cuina.database.Database;
import cuina.object.BaseWorld;
import cuina.plugin.ForScene;
import cuina.plugin.LifeCycle;
import cuina.plugin.Plugin;
import cuina.rpg.actor.Actor;
import cuina.rpg.actor.ActorData;
import cuina.rpg.actor.ActorGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
 
/**
 * Update-Relevant.
 * Ein Kampfsystem, in dem zwei Gruppen gegenüber stehen und sich in einem strukturierten Ablauf bekämpfen.
 * @author TheWhiteShadow
 */
@ForScene(name="BattleController", scenes={"Battle"})
public class Battle implements Plugin, LifeCycle
{
    private static final long serialVersionUID = -8493881365793619435L;
    
    private static final int GROUP_ALL = 0;
    private static final int GROUP_HERO = 1;
    private static final int GROUP_ENEMY = 2;
    
    public static final String PARTY = "Party";
    public static final String BATTLE_MENU = "BattleMenu";
    public static final String DB_States = "States";
    
    private static final String PHASE_BATTLE_START      = "battle.start";
    private static final String PHASE_ROUND_START       = "round.start";
    private static final String PHASE_INPUT             = "action.input";
//  private static final String PHASE_INPUT_KI          = "input.ki";
    private static final String PHASE_ACTION_PERFORM    = "action.perform";
    private static final String PHASE_ACTION_RESULT     = "action.result";
    private static final String PHASE_BATTLE_END        = "battle.end";
    
    private ActorGroup      party;
    private ActorGroup      enemies;
//  private Actor           currentActor;
    private BattleObject    currentBattler;
//  private BattleMap       map;
//  private BattleAction    action;
    private BattleKI        ki;
    private BattleMenu      battleMenu;
//  private boolean         menuVisible;
    private BattleStrategy  strategy;
    private int rounds;
    private float battleTime;
    private ActionResult[] results;
    
    private HashMap<String, BattlePhase> phaseList = new HashMap<String, BattlePhase>();
    private HashMap<Actor, BattleObject> objects = new HashMap<Actor, BattleObject>();
    private BattlePhase phase;
    private boolean freeze;
    private float battleSpeed;
    
    /** Vergleicher zum Sortieren der Kämpfer nach Zeitwert. */
    private final Comparator<BattleObject> TIME_COMPERATOR = new Comparator<BattleObject>()
    {
        @Override
        public int compare(BattleObject b1, BattleObject b2)
        {
            return (int) ((b1.getBattleTime() - b2.getBattleTime()) * 1000);
        }
    };
    
    /**
     * Typen von Animationen, die im Kampf benötigt werden.
     */
    public enum AnimationType
    {
        STAND, ACTIVE, ATTACK, ITEM, MAGIC, GUARD, HIT, DIE, REVIVE
    }
    
    /**
     * Stellt den Zustand eines Kampes da.
     * Dieser kann mit {@link Battle#getBattleResult()} abgefragt werden.
     */
    public enum BattleResult
    {
        WIN, LOSE, TIE, RUNNING
    }
    
    public Battle()
    {
        this(new DefaultBattleStrategy(), new DefaultBattleKI(), 0.2f);
    }
    
    public Battle(BattleStrategy strategy, BattleKI ki, float battleSpeed)
    {
        if (strategy == null)
            throw new NullPointerException("strategy must not be null.");
        this.strategy = strategy;
        this.ki = ki;
        this.battleSpeed = battleSpeed;
 
        phaseList.put(PHASE_BATTLE_START, new BattleStart());
        phaseList.put(PHASE_ROUND_START, new RoundStart());
        
        phaseList.put(PHASE_INPUT, new InputPhase(true, 0.3f));
//      phaseList.put(PHASE_INPUT_KI, inputPhase);
        
        phaseList.put(PHASE_ACTION_PERFORM, new ActionPhase());
        phaseList.put(PHASE_ACTION_RESULT, new PostActionPhase());
        phaseList.put(PHASE_BATTLE_END, new PostBattlePhase());
    }
    
    public void freeze()        { freeze = true; }
    public void unfreeze()      { freeze = false; }
    public boolean isFreezed()  { return freeze; }
    
    public BattleKI getKI()
    {
        return ki;
    }
 
    public void setKI(BattleKI ki)
    {
        this.ki = ki;
    }
 
    public void setStrategy(BattleStrategy strategy)
    {
        this.strategy = strategy;
    }
 
    public BattleStrategy getStrategy()
    {
        return strategy;
    }
 
    public BattlePhase getPhase(String name)
    {
        return phaseList.get(name);
    }
 
    /**
     * Fügt eine BattlePhase mit dem angegebenen Namen in die Liste ein.
     * Wenn der Name bereits existiert, wird er überschreiben.
     * @param name Name der BattlePhase
     * @param phase BattlePhase
     */
    public void putPhase(String name, BattlePhase phase)
    {
        phaseList.put(name, phase);
    }
    
    public void startBattle(BattleScene scene)
    {
        this.party = Game.getContext(Context.SESSION).<ActorGroup>get(PARTY);
        if (party == null) throw new NullPointerException("keine Party gefunden");
        
        this.enemies = new ActorGroup();
        for(String key : scene.troop.enemies)
        {
        	enemies.addActor( new Enemy(Database.<ActorData>get("Enemy", key)) );
        }

        this.battleMenu = Game.getContext(Context.SESSION).get(BATTLE_MENU);
//        if (battleMenu == null) throw new NullPointerException("kein Kampfmenü gefunden");
        this.rounds = 0;
        this.battleTime = 0;
        
        initObjects();
        setPhase(PHASE_BATTLE_START);
    }
    
    public void endBattle()
    {
        dispose();
    }
 
    @Override
    public void init() {}
 
    /* XXX: DebugNotiz: Wird von BattleMap im 1-Sekunden aufgerufen.
     */
    @Override
    public void update()
    {
        if (phase == null) return;
        
        if (!freeze) battleTime += battleSpeed;
        battleMenu.update();
        
        phase.update(this);
    }
 
    @Override
    public void dispose()
    {
//        System.exit(0);
    }
    
	public void initObjects()
	{
		BaseWorld world = BaseWorld.getInstance();
		if (world == null) throw new NullPointerException("No World defined!");

		BattleObject battler;
		for (Actor actor : getActors())
		{
			battler = new BattleObject(this, actor);
			battler.addBattleTime((float) Math.random() * strategy.getActionTime(actor, null));
			battler.setID(world.getAvilableID());
			world.addObject(battler);
			addBattler(battler);
		}
	}

	private void addBattler(BattleObject battler)
	{
		objects.put(battler.getActor(), battler);
	}

	public BattleObject getBattler(Actor actor)
	{
		return objects.get(actor);
	}

	public int getRounds()
	{
		return rounds;
	}

	public float getBattleTime()
	{
		return battleTime;
	}

	public void newRound()
	{
		rounds++;
		if (currentBattler != null) currentBattler.setAction(null);
		currentBattler = null;
	}
    
    public void setPhase(String name)
    {
        BattlePhase phase = phaseList.get(name);
        if (phase == null)
            throw new NullPointerException("Phase '" + name + "' does not exist!");
        
        this.phase = phase;
        System.out.println("[Battle] starte Phase: " + name);
        this.phase.start(this);
    }
    
    public void setBattler(Actor actor)
    {
        if (actor != null && isPartyMember(GROUP_ALL, actor))
            setCurrentBattler(objects.get(actor));
        else
            setCurrentBattler(null);
    }
    
    public void setBattler(int group, int index)
    {
        if (group == GROUP_HERO)
            setCurrentBattler( objects.get(party.getActor(index)) );
        else if (group == GROUP_ENEMY) 
            setCurrentBattler( objects.get(enemies.getActor(index)) );
        else
            throw new IllegalArgumentException("invalid group");
    }
    
    public BattleMenu getBattleMenu()
    {
        return battleMenu;
    }
    
    /**
     * Findet den nächsten Käpfer in der Reihe mit dem niedrigsten Zeitwert.
     * @return Nächster aktiver Kämpfer.
     */
    public BattleObject findNextBattler()
    {
//      List<Actor> list = getActors();
//      Collections.sort(list, TIME_COMPERATOR);
//      return list.get(skips);
        
        float minTime = Float.MAX_VALUE;
        BattleObject result = null;
        
        for(Actor key : objects.keySet())
        {
            BattleObject obj = objects.get(key);
            if (!obj.paused && strategy.isAvailable(obj.getActor()) && obj.getBattleTime() < minTime)
            {
                result = obj;
                minTime = obj.getBattleTime();
            }
        }
        return result;
    }
    
    public ArrayList<BattleObject> getBattlerOrder()
    {
        ArrayList<BattleObject> order = new ArrayList<BattleObject>(objects.size());
        
        for(BattleObject obj : objects.values())
        {
            if (obj.getBattleTime() != Float.MIN_VALUE) order.add(obj);
        }
        Collections.sort(order, TIME_COMPERATOR);
        return order;
    }
    
    public ActorGroup getParty()
    {
        return party;
    }
 
    public ActorGroup getEnemies()
    {
        return enemies;
    }
    
    public List<Actor> getActors()
    {
        ArrayList<Actor> actors = new ArrayList<Actor>(party.size() + enemies.size());
        
        for(Actor a : party)   actors.add(a);
        for(Actor a : enemies) actors.add(a);
        
        return actors;
    }
    
    public void setCurrentBattler(BattleObject battler)
    {
        this.currentBattler = battler;
    }
 
    public BattleObject getCurrentBattler()
    {
        return currentBattler;
    }
    
    private boolean isPartyMember(int group, Actor actor)
    {
        switch(group)
        {
            case GROUP_ALL:     return party.containsActor(actor) || enemies.containsActor(actor);
            case GROUP_HERO:    return party.containsActor(actor);
            case GROUP_ENEMY:   return enemies.containsActor(actor);
            default: throw new IllegalArgumentException("invalid group");
        }
    }
    
    public ArrayList<Actor> createTargetList(ActorGroup targetGroup, BattleAction action)
    {
        ArrayList<Actor> targets = new ArrayList<Actor>(targetGroup.size());
        for(Actor a : targetGroup)
        {
            if (strategy.isSelectable(a, action)) targets.add(a);
        }
        return targets;
    }
    
    public Actor[] simulateBattle(int size, BattleAction action)
    {
        if (size <= 0)
            throw new IllegalArgumentException("size must be > 0!");
        
        Actor[] actors = new Actor[size];
        HashMap<Actor, Float> times = new HashMap<Actor, Float>();
        
        for(Actor actor : getActors())
        {
            times.put(actor, objects.get(actor).getBattleTime());
        }
        
        Actor actor = getNextSimulationBattler(times);
        addSimulationTime(times, actor, action);
        actors[0] = actor;
        
        for(int i = 1; i < actors.length; i++)
        {
            actor = getNextSimulationBattler(times);
            addSimulationTime(times, actor, null);
            actors[i] = actor;
        }
 
        return actors;
    }
    
    private Actor getNextSimulationBattler(HashMap<Actor, Float> times)
    {
        float minTime = Float.MAX_VALUE;
        Actor actor = null;
        
        for(Actor key : objects.keySet())
        {
            BattleObject obj = objects.get(key);
            float time = times.get(key);
            if (strategy.isAlive(obj.getActor()) && time < minTime)
            {
                actor = key;
                minTime = time;
            }
        }
        
//      System.out.println("[Battle] Battler: " + actor);
        return actor;
    }
    
    private void addSimulationTime(HashMap<Actor, Float> times, Actor actor, BattleAction action)
    {
        times.put(actor, times.get(actor) + strategy.getActionTime(actor, action));
    }
 
    /**
     * CallBack-Methode für das Kampfmenü.
     * Nachdem das Menü eine Aktions-Anfrage erhalten hat, wird das BattleObject solange warten,
     * bis diese Methode es wieder aktiviert.
     * Das übergebene BattleObject sollte eine gesetzte Aktion beinhalten.
     */
    public void menuCallback(BattleObject battler)
    {
        battler.paused = false;
        if (battler.getBattleTime() < battleTime)
            battler.setBattleTime(battleTime);
//      if (phase == phaseList.get(PHASE_INPUT))
//          setCurrentBattler(findNextBattler());
        
        BattleAction act = battler.getAction();
        System.out.println("[Battle] Setze Aktion für " + battler.getActor().getName() +
                           ": " + act.getName() +
                           " auf " + act.targets.get(0).getName());
//      for(BattleObject obj : objects.values()) obj.paused = false;
    }
    
    /**
     * Diese Phase leitet den Kampf ein.
     * @author TheWhiteShadow
     */
    private class BattleStart implements BattlePhase
    {
        @Override
        public void start(Battle battle)
        {
            strategy.battleStart(battle);
        }
 
        @Override
        public void update(Battle battle)
        {
            setPhase(PHASE_ROUND_START);
        }
    }
    
    /**
     * Diese Phase leitet eine neue Runde ein.
     * @author TheWhiteShadow
     */
    public class RoundStart implements BattlePhase
    {
        @Override
        public void start(Battle battle)
        {
            if (getBattleResult() != BattleResult.RUNNING)
            {
                setPhase(PHASE_BATTLE_END);
                return;
            }
            
            newRound();
            strategy.roundStart(battle);
            //TODO: Events fürs KS einfügen.
        }
 
        @Override
        public void update(Battle battle)
        {
            if (currentBattler != null && currentBattler.getAction() != null)
            {
                if (currentBattler.getBattleTime() <= battleTime) setPhase(PHASE_ACTION_PERFORM);
                return;
            }
            
            setPhase(PHASE_INPUT);
        }
    }
    
    /**
     * Diese Phase steuert die Eingaben und Auswahl einer Aktion.
     * @author TheWhiteShadow
     */
    public class InputPhase implements BattlePhase
    {
        private boolean lazyInput;      // Verhindert Menüeingaben vor der Aktionsfreigabe.
        private float coolDownSpeed;    // Zeitreduzierung für Aktions-CoolDowns. Sinnvoll bei aktivem lazyInput.
        
        public InputPhase()
        {
            this(false, 0);
        }
        
        public InputPhase(boolean lazyInput, float coolDownSpeed)
        {
            this.lazyInput = lazyInput || battleSpeed == 0;
            this.coolDownSpeed = coolDownSpeed;
        }
        
        @Override
        public void start(Battle battle)
        {
            if (!lazyInput) directInput(battle);
            
            setCurrentBattler(findNextBattler());
            if (currentBattler != null)
            {
                if (battleSpeed == 0)
                    battleTime = currentBattler.getBattleTime();
                else
                    speedUpTime();
            
                System.out.println("[Battle] Battler= " + currentBattler.getActor());
            }
 
            //XXX: Weil der Debug-Frame so lange dauert
//            update(battle);
        }
        
        @Override
        public void update(Battle battle)
        {
            if (freeze) return;
            
            setCurrentBattler(findNextBattler());
            speedUpTime();
            
            if (currentBattler == null) return;
            if (currentBattler.getAction() != null)
            {
                if (currentBattler.getBattleTime() <= battleTime) setPhase(PHASE_ACTION_PERFORM);
                return;
            }
            
            if (isPartyMember(GROUP_HERO, currentBattler.getActor()))
            {
                if (lazyInput && currentBattler.getBattleTime() > battleTime) return;
                playerInput(battle, currentBattler);
                currentBattler = null;
            }
            else
            {
                if (currentBattler.getBattleTime() > battleTime) return;
                kiInput(battle);
                setPhase(PHASE_ACTION_PERFORM);
            }
        }
        
        private void directInput(Battle battle)
        {
            ArrayList<BattleObject> availableBattlers = new ArrayList<BattleObject>(party.size());
            for(Actor actor : party)
            {
                BattleObject battler = getBattler(actor);
                if (battler.paused == false && battler.getAction() == null && strategy.isAvailable(actor))
                {
                    availableBattlers.add(battler);
                }
            }
            Collections.sort(availableBattlers, TIME_COMPERATOR);
            for(BattleObject b : availableBattlers)
            {
                playerInput(battle, b);
            }
        }
        
        private void speedUpTime()
        {
            if (battleSpeed > 0 && coolDownSpeed > 0 && currentBattler != null)
            {
                if (battleTime + coolDownSpeed <= currentBattler.getBattleTime())
                    battleTime += coolDownSpeed;
                else
                    battleTime = currentBattler.getBattleTime();
            }
        }
        
        private void playerInput(Battle battle, BattleObject battler)
        {
            battler.paused = true;
            battler.performAction(AnimationType.ACTIVE, null, null);
            battleMenu.requestInput(battle, currentBattler);
        }
        
        private void kiInput(Battle battle)
        {
            BattleAction action;
            if (ki != null)
            {
                action = ki.prepareAction(battle, (Enemy) currentBattler.getActor());
                if (action == null)
                    throw new NullPointerException("The method BattleKI#prepareAction must not return null!");
            }
            else
            {
                action = new BattleAction(currentBattler.getActor(), "Angriff", 1f);
                ArrayList<Actor> targets = createTargetList(party, action);
                if (targets.size() == 0)
                {
                    setPhase(PHASE_ROUND_START);
                    return;
                }
                int index = (int) (random() * targets.size());
                action.targets.add(party.getActor(index));
            }
            currentBattler.setAction(action);
        }
    }
    
    /**
     * Diese Phase führt eine zuvor gewählteAktion aus und  zeigt die Animation an.
     * @author TheWhiteShadow
     */
    public class ActionPhase implements BattlePhase
    {
        private BattleAction action;
        private int waitCount;
        
        @Override
        public void start(Battle battle)
        {
            this.action = currentBattler.getAction();
            if (action == null)
            {
                setPhase(PHASE_ROUND_START);
                return;
            }
            
            if (action.getSkill() != null) strategy.paySkillCost(action);
            currentBattler.addBattleTime(strategy.getActionTime(action.getUser(), action));
            currentBattler.performAction(AnimationType.ATTACK, action, null);
            
            int size = action.targets.size();
            results = new ActionResult[size];
            // Skill-Ausführung
            for (int i = 0; i < size; i++)
            {
                results[i] = new ActionResult();
                strategy.executeAction(action, results);
            }
        }
 
        @Override
        public void update(Battle battle)
        {
            if (currentBattler.inBattleAnimation()) return;
            
            if (--waitCount > 0) return;
            for(int i = 0; i < action.targets.size(); i++)
            {
                getBattler(action.targets.get(i)).performAction(AnimationType.HIT, action, results[i]);
            }
            
            setPhase(PHASE_ACTION_RESULT);
        }
    }
    
    /**
     * Diese Phase zeigt das Ergebnis der zuvor ausgeführten Aktion an und führt evtl. Abschlussanimationen aus.
     * @author TheWhiteShadow
     */
    public class PostActionPhase implements BattlePhase
    {
        ArrayList<Actor> targets;
        
        @Override
        public void start(Battle battle)
        {
        	BattleAction action = currentBattler.getAction();
            targets = action.targets;
            for (int i = 0; i < targets.size(); i++)
            {
                Actor target = targets.get(i);
                BattleObject targetBattler = getBattler(target);
                ActionResult result = results[i];
                
                if ((result.flags & BattleStrategy.DEATH) != 0)
                {
                    targetBattler.setBattleTime(Float.MAX_VALUE);
                    targetBattler.performAction(AnimationType.DIE, action, result);
                    
                    if (targetBattler.paused)
                    {
                        targetBattler.paused = false;
                        battleMenu.cancelRequest(battle, targetBattler);
                    }
                }
                if ((result.flags & BattleStrategy.REANIMATE) != 0)
                {
                    targetBattler.setBattleTime(battleTime + strategy.getActionTime(target, null));
                    targetBattler.performAction(AnimationType.REVIVE, action, result);
                }
            }
            
            strategy.roundEnd(battle);
        }
 
        @Override
        public void update(Battle battle)
        {
            for (int i = 0; i < targets.size(); i++)
                if (getBattler(targets.get(i)).inBattleAnimation()) return;
            
            setPhase(PHASE_ROUND_START);
        }
    }
    
    /**
     * Diese Phase meldet das Kampfende ein.
     * @author TheWhiteShadow
     */
    public class PostBattlePhase implements BattlePhase
    {
        @Override
        public void start(Battle battle)
        {
            strategy.battleEnd(battle);
        }
 
        @Override
        public void update(Battle battle)
        {
            endBattle();
        }
    }
    
    /**
     * Gibt an, ob mindestens ein Mitglied der angegebenen Gruppe noch lebt.
     * @param group Die Gruppe
     * @return <code>true</code>, wenn mindestens ein Mitglid lebt, andernfalls <code>false</code>.
     */
    public boolean isGroupAlive(ActorGroup group)
    {
        for(Actor actor : group)
        {
            if (strategy.isAlive(actor)) return true;
        }
        return false;
    }
    
    /**
     * Gibt den aktuellen Zustand des Kampfes zurück.
     * @return Den Zustand des Kampfes.
     */
    public BattleResult getBattleResult()
    {
        boolean partyAlive = isGroupAlive(party);
        boolean enemyAlive = isGroupAlive(enemies);
        
        if (partyAlive && !enemyAlive) return BattleResult.WIN;
        if (!partyAlive && enemyAlive) return BattleResult.LOSE;
        if (!partyAlive && !enemyAlive) return BattleResult.TIE;
        return BattleResult.RUNNING;
    }
 
    /**
     * Gibt an, ob der Kampf aktiv ist.
     * Der aufruf ist äquivalent zu:
     * <pre>Battle#getBattleResult() == BattleResult.RUNNING</pre>
     * @return <code>true</code>, wenn der Kampf läuft, andernfalls <code>false</code>.
     */
    public boolean isRunning()
    {
        return (getBattleResult() == BattleResult.RUNNING);
    }
    
	@Override
	public void postUpdate() {}
    
    /**
     * Das Ergebnis einer Aktion.
     */
    public static class ActionResult
    {
        /** Der Schadenswert der Aktion. Negativ bei Heilung. */
        public long damage;
        /** Die Ergebnismeldung der Aktion. z.B. "Verfehlt!" */
        public String message;
        /** Eine Reihe von Schaltern, die das Ergebnis beschreiben. z.B. "Kritisch".
         * Einige Vorgaben sind bereits in BattleStrategy definiert.
         * Die Flags mit den Werten 1 bis 1024 sind reserviert.
         * @see BattleStrategy
         */
        public int flags;
    }
}