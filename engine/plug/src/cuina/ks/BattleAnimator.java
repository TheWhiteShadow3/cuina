package cuina.ks;

import cuina.animation.Animator;
import cuina.animation.Model;
import cuina.ks.Battle.AnimationType;

public class BattleAnimator implements Animator
{
	private static final long serialVersionUID = -3367151691643290358L;
	
	/**
	 * Default-Mapping fürs Model.
	 */
	public static AnimationType[] DEFAULT_ANIMATION_MASK = new AnimationType[]
	{
		AnimationType.STAND,
		AnimationType.ATTACK,
		AnimationType.HIT,
		AnimationType.ITEM,
		AnimationType.MAGIC,
		AnimationType.DIE,
		AnimationType.REVIVE
	};
	
	private Model model;
	private boolean animate = false;

	@Override
	public void init(Model model)
	{
		this.model = model;
	}

	@Override
	public void update()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void animationFinished()
	{
		animate = false;
	}

    public void performAnimation(AnimationType type, BattleAction action, Battle.ActionResult result)
    {
    	animate = true;
    }
}
