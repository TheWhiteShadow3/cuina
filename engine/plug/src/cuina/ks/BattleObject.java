package cuina.ks;
 
import cuina.Logger;
import cuina.animation.Model;
import cuina.object.BaseObject;
import cuina.object.Instantiable;
import cuina.rpg.actor.Actor;
 
/**
 * Update-Relevant.
 */
public class BattleObject extends BaseObject
{
	private static final long serialVersionUID = 6971983484774539058L;

	private final Actor actor;
	private BattleAction action;
	private float battleTime = 0;
	/** Deaktiviert das BattleObject während einer Menüeingabe. */
	boolean paused;

	public BattleObject(Battle battle, Actor actor)
	{
		this.actor = actor;

		for (String key : actor.getExtensionKeys())
		{
			Object obj = actor.getExtension(key);
			if (obj instanceof Instantiable) try
			{
				addExtension(key, ((Instantiable) obj).createInstance(this));
			}
			catch (Exception e)
			{
				Logger.log(BaseObject.class, Logger.ERROR, e);
			}
		}
	}

	public Actor getActor()
	{
		return actor;
	}

	public BattleAction getAction()
	{
		return action;
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

	public void performAction(Battle.AnimationType type, BattleAction action, Battle.ActionResult result)
	{
		Model model = (Model) getExtension("model");
		if (model != null)
		{
			BattleAnimator animator = (BattleAnimator) model.getAnimator();
			animator.performAnimation(type, action, result);
		}
	}
	public boolean inBattleAnimation()
	{
		return false;// model.isAnimate(); TODO: Implementierung der Animationen im Animator fehlt.
	}

	@Override
	public String toString()
	{
		return "BattleObject [actor=" + actor + ", battleTime=" + battleTime + ", paused=" + paused + "]";
	}
}