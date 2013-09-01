package cuina.ks;
 
import cuina.ks.Battle.ActionResult;
import cuina.rpg.actor.Actor;
 
/**
 * Definiert Funktionen, die zur Berechnung während eines Kampfes genutzt werden.
 * @author TheWhiteShadow
 */
public interface BattleStrategy
{
    /** Gibt an, dass die Aktion tötlich war. */
    public static final int DEATH       = 1;
    /** Gibt an, dass die Aktion wiederbelebend war. */
    public static final int REANIMATE   = 2;
    /** Gibt an, dass die Aktion nicht getroffen hat. */
    public static final int MISS        = 4;
    /** Gibt an, dass die Aktion einen Positiveffekt hat und daher die Abwehr ignoriert. */
    public static final int HEAL        = 8;
    /** Gibt an, dass die Aktion eine kritische Wirkung hatte. */
    public static final int CRIT        = 16;
    /** Gibt an, dass die Aktion abgewehrt wurde. */
    public static final int BLOCK       = 32;
    /** Gibt an, dass die Aktion mindestens einen Zusatzeffekt hat, der Wirkungslos war. */
    public static final int IMUN        = 64;
    // Die weiteren Flags bis 1024 sind für zukünftige Implementierungen reserviert.
    
    public boolean canUseAction(BattleAction action);
    
    public void paySkillCost(BattleAction action);
    
    public void executeAction(BattleAction action, ActionResult[] results);
    
    public void roundStart(Battle battle);
    public void roundEnd(Battle battle);
    
    public void battleStart(Battle battle);
    public void battleEnd(Battle battle);
    
    public boolean isAlive(Actor actor);
    public boolean isAvailable(Actor actor);
    public boolean isSelectable(Actor target, BattleAction action);
    
    /**
     * Zeit, die eine Aktion zur Ausführung benötigt.
     * @param user Anwender der Aktion.
     * @param action Aktion. Kann <code>null</code> sein.
     * @return Aktionszeit.
     */
    public float getActionTime(Actor user, BattleAction action);
}