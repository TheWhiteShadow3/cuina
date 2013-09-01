package cuina.ks;
 
import cuina.object.BaseObject;
import cuina.rpg.actor.Actor;
 
/**
 * Update-Relevant.
 */
public class BattleObject extends BaseObject
{
	private static final long serialVersionUID = 6971983484774539058L;

	public int[] ANIMATION_MASK = new int[] {0, 1};
    
    private final Actor     actor;
    private BattleAction    action;
    private BattleModel     model;
    private float battleTime = 0;
    /** Deaktiviert das BattleObject während einer Menüeingabe. */
    boolean paused;
    
    public BattleObject(Battle battle, Actor actor)
    {
        this.actor = actor;
        model = new BattleModel(battle, actor);
        addExtension("Model", model);
    }
    
    public Actor getActor()
    {
        return actor;
    }
    
    public BattleAction getAction()
    {
        return action;
    }
 
    @Override
    public void update()
    {
        model.update();
    }
 
    public void setAction(BattleAction action)
    {
        this.action = action;
        if (action != null) action.setUser(actor);
    }
    
    public float getBattleTime()
    {
        return battleTime;
    }
 
    public void setBattleTime(float time)
    {
        this.battleTime = time;
    }
    
    public void addBattleTime(float time)
    {
        this.battleTime += time;
    }
 
    public void performBattleAnimation(Battle.AnimationType type, Battle.ActionResult result)
    {
        model.performAnimation(type, result);
    }
    
    public boolean inBattleAnimation()
    {
        return model.inAnimation();
    }
 
    @Override
    public String toString()
    {
        return "BattleObject [actor=" + actor + ", battleTime=" + battleTime + ", paused=" + paused + "]";
    }
    
    
}